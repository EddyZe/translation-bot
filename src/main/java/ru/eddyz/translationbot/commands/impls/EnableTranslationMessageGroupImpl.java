package ru.eddyz.translationbot.commands.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.translationbot.commands.EnableTranslationMessageGroup;
import ru.eddyz.translationbot.domain.entities.Group;
import ru.eddyz.translationbot.keyboards.InlineKey;
import ru.eddyz.translationbot.services.GroupService;

import java.util.NoSuchElementException;


@Component
@RequiredArgsConstructor
@Slf4j
public class EnableTranslationMessageGroupImpl implements EnableTranslationMessageGroup {

    private final GroupService groupService;
    private final TelegramClient telegramClient;


    @Override
    public void execute(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getMessage().getChatId();
        var messageId = callbackQuery.getMessage().getMessageId();
        var data = callbackQuery.getData();

        var split = data.split(":");
        var groupId = Long.parseLong(split[1]);

        try {
            var group = groupService.findById(groupId);
            group.setTranslatingMessages(!group.getTranslatingMessages());
            groupService.update(group);
            editMessage(chatId, messageId, group);
            answerCallBack(callbackQuery.getId());
        } catch (NoSuchElementException e) {
            log.error(e.toString());
            sendErrorMessage(chatId);
        }
    }

    private void sendErrorMessage(Long chatId) {
        try {
            var sendMessage = SendMessage.builder()
                    .text("Группа не найдена, возможно она была удалена. Попробуйте обновить список.")
                    .chatId(chatId)
                    .build();

            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения {}", e.toString());
        }
    }

    private void editMessage (Long chatId, Integer messageId, Group group) {
        try {
            var editMessage = EditMessageReplyMarkup
                    .builder()
                    .messageId(messageId)
                    .chatId(chatId)
                    .replyMarkup(InlineKey.groupSetting(group))
                    .build();

            telegramClient.execute(editMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при изменении сообщения. {}", e.toString());
        }
    }

    private void answerCallBack(String id) {
        try {
            telegramClient.execute(new AnswerCallbackQuery(id));
        } catch (TelegramApiException e) {
            log.error("Ошибка при нажатии кнопки: {}", e.toString());
        }
    }
}
