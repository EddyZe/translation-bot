package ru.eddyz.translationbot.commands.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.translationbot.clients.proxyapi.ProxyApiOpenAiClient;
import ru.eddyz.translationbot.clients.proxyapi.payloads.MessageOpenAi;
import ru.eddyz.translationbot.clients.proxyapi.payloads.ModelOpenAi;
import ru.eddyz.translationbot.clients.proxyapi.payloads.RequestOpenAi;
import ru.eddyz.translationbot.clients.yandex.YandexTranslateClient;
import ru.eddyz.translationbot.commands.TranslationGroupMessage;
import ru.eddyz.translationbot.domain.entities.Group;
import ru.eddyz.translationbot.domain.entities.LanguageTranslation;
import ru.eddyz.translationbot.domain.entities.TranslationMessage;
import ru.eddyz.translationbot.keyboards.InlineKey;
import ru.eddyz.translationbot.services.GroupService;
import ru.eddyz.translationbot.services.TranslationMessagesService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
@Slf4j
public class TranslationGroupMessageImpl implements TranslationGroupMessage {

    private final TelegramClient telegramClient;
    private final GroupService groupService;
    private final TranslationMessagesService translationMessagesService;
    private final YandexTranslateClient yandexTranslateClient;
    private final ProxyApiOpenAiClient proxyApiOpenAiClient;

    @Override
    @Transactional
    public void execute(Message message) {
        var text = Optional.ofNullable(message.getText()).orElse(message.getCaption());
        var groupChatId = message.getChat().getId();
        var messageId = message.getMessageId();

        var textSplit = text.split(" ");

        if (textSplit.length == 1 && textSplit[0].matches("^(https?:\\/\\/)?(www\\.)?\\S+\\.\\S+"))
            return;

        if (text.length() > 1000) {
            sendMessage(groupChatId, "Не могу перевести сообщение. Сообщение не должно превышать 1000 символов.",
                    messageId, null);
            return;
        }

        //var detectLanguageCode = yandexTranslateClient.detect(text).getLanguageCode();
        var promtDetectCode = proxyApiOpenAiClient.promtDetectLanguage();
        var promtText = buildMessageText(text);
        var response = proxyApiOpenAiClient.sendPromt(buildPromt(promtDetectCode, promtText));

        var splitResponse = response.getChoices().getFirst().getMessage().getContent().split(":");
        var code = splitResponse[0];

        var group = groupService.findByTelegramChatId(groupChatId);
        var groupLanguages = new ArrayList<>(group.getLanguages());


        var currentChars = group.getLimitCharacters();
        var newChatsLimit = currentChars - text.length();

        if (newChatsLimit <= 0) {
            sendMessage(group.getChatId(), "Лимит символов для группы %s исчерпан. Докупите символы, для этой группы"
                    .formatted(group.getTitle()), null, InlineKey.selectPaymentMode(group.getGroupId()));
            return;
        }

        if (!group.getTranslatingMessages())
            return;

        var sb = new StringBuilder();

        for (LanguageTranslation lang : groupLanguages) {
            if (!lang.getCode().equals(code)) {
                var promtTranslate = proxyApiOpenAiClient.promtTranslateText(lang.getTitle());
                var messageOpenAi = buildMessageText(text);
//                var translationText = yandexTranslateClient
//                        .translate(text, lang.getCode())
//                        .getTranslations().getFirst().getText();

                response = proxyApiOpenAiClient.sendPromt(buildPromt(promtTranslate, messageOpenAi));

                var translationText = response.getChoices().getFirst().getMessage().getContent();

                log.info("translate: {}", translationText);

                var temp = "&#8226 %s\n".formatted(translationText);
                if (temp.length() + sb.length() >= 4096) {
                    sendMessage(groupChatId, sb.toString(), messageId, null);
                    sb = new StringBuilder();
                }
                sb.append(temp);
                group.setLimitCharacters(newChatsLimit);
                groupService.update(group);
                translationMessagesService.save(buildTranslationMesssage(group, text, translationText,
                        message.getFrom().getUserName()));
            }
        }
        if (sb.toString().isEmpty())
            return;

        sendMessage(groupChatId, sb.toString(), messageId, null);
    }

    private RequestOpenAi buildPromt(MessageOpenAi promtDetectCode, MessageOpenAi promtText) {
        return RequestOpenAi.builder()
                .model(ModelOpenAi.GPT_4o_MINI.toString())
                .messages(List.of(promtDetectCode, promtText))
                .build();
    }

    private MessageOpenAi buildMessageText(String text) {
        return MessageOpenAi.builder()
                .role("user")
                .content(text)
                .build();
    }

    private TranslationMessage buildTranslationMesssage(Group group, String text, String translationText, String userName) {
        return TranslationMessage.builder()
                .group(group)
                .translationTime(LocalDateTime.now())
                .message(text)
                .fromUsername(userName)
                .messageTranslate(translationText)
                .numberCharacters(text.length())
                .build();
    }

    private void sendMessage(Long chatId, String text, Integer messageId, InlineKeyboardMarkup inlineKeyboardMarkup) {
        try {
            var sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .disableNotification(true)
                    .parseMode(ParseMode.HTML)
                    .build();

            if (messageId != null)
                sendMessage.setReplyToMessageId(messageId);

            if (inlineKeyboardMarkup != null)
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);

            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке перевода в группу: {}", e.toString());
        }
    }
}
