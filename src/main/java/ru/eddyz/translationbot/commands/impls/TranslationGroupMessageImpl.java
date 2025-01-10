package ru.eddyz.translationbot.commands.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.translationbot.clients.yandex.YandexTranslateClient;
import ru.eddyz.translationbot.commands.TranslationGroupMessage;
import ru.eddyz.translationbot.domain.entities.Group;
import ru.eddyz.translationbot.domain.entities.LanguageTranslation;
import ru.eddyz.translationbot.domain.entities.TranslationMessage;
import ru.eddyz.translationbot.services.GroupService;
import ru.eddyz.translationbot.services.TranslationMessagesService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;


@Component
@RequiredArgsConstructor
@Slf4j
public class TranslationGroupMessageImpl implements TranslationGroupMessage {

    private final TelegramClient telegramClient;
    private final GroupService groupService;
    private final TranslationMessagesService translationMessagesService;
    private final YandexTranslateClient yandexTranslateClient;

    @Override
    @Transactional
    public void execute(Message message) {
        var text = Optional.ofNullable(message.getText()).orElse(message.getCaption());
        var groupChatId = message.getChat().getId();
        var messageId = message.getMessageId();

        var detectLanguageCode = yandexTranslateClient.detect(text).getLanguageCode();
        var group = groupService.findByTelegramChatId(groupChatId);
        var groupLanguages = new ArrayList<>(group.getLanguages());


        var currentChars = group.getLimitCharacters();
        var newChatsLimit = currentChars - text.length();

        if (newChatsLimit < 0) {
            sendMessage(group.getChatId(), "Лимит символов для группы %s исчерпан. Докупите символы, для этой группы"
                    .formatted(group.getTitle()), null);
            return;
        }

        for (LanguageTranslation lang : groupLanguages) {
            if (!lang.getCode().equals(detectLanguageCode)) {
                var translationText = yandexTranslateClient
                        .translate(text, lang.getCode())
                        .getTranslations().getFirst().getText();
                sendMessage(groupChatId, translationText, messageId);
                group.setLimitCharacters(newChatsLimit);
                groupService.update(group);
                translationMessagesService.save(buildTranslationMesssage(group, text, translationText,
                        message.getChat().getUserName()));
            }
        }

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

    private void sendMessage(Long chatId, String text, Integer messageId) {
        try {
            var sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .parseMode(ParseMode.HTML)
                    .build();

            if (messageId != null)
                sendMessage.setReplyToMessageId(messageId);

            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке перевода в группу: {}", e.toString());
        }
    }

}
