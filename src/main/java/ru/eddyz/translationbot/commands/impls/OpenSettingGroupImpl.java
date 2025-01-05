package ru.eddyz.translationbot.commands.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.translationbot.commands.OpenSettingGroup;
import ru.eddyz.translationbot.domain.entities.Group;
import ru.eddyz.translationbot.keyboards.InlineKey;
import ru.eddyz.translationbot.services.GroupService;

import java.util.NoSuchElementException;


@Component
@RequiredArgsConstructor
@Slf4j
public class OpenSettingGroupImpl implements OpenSettingGroup {

    private final GroupService groupService;
    private final TelegramClient client;


    @Override
    public void execute(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getMessage().getChatId();
        var messageId = callbackQuery.getMessage().getMessageId();
        var splitData = callbackQuery.getData().split(":");
        var btn = splitData[0];
        var groupId = Long.parseLong(splitData[1]);

        try {
            var group = groupService.findById(groupId);
            editMessage(chatId, messageId, generateMessage(group), groupId);
            answerCallBack(callbackQuery.getId());
        } catch (NoSuchElementException e) {
            log.error(e.toString());
            sendErrorMessage(chatId);
        }

    }

    private String generateMessage(Group group) {
        return """
                <b>Настройка %s</b>
                
                ID канала: %s
                Осталось символов: %s
                
                """.formatted(group.getTitle(), group.getTelegramGroupId(), group.getLimitCharacters());
    }

    private void sendErrorMessage(Long chatId) {
        try {
            var sendMessage = SendMessage.builder()
                    .text("Группа не найдена, возможно она была удалена. Попробуйте обновить список.")
                    .chatId(chatId)
                    .build();

            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения {}", e.toString());
        }
    }

    private void editMessage (Long chatId, Integer messageId, String message, Long groupId) {
        try {
            var editMessage = EditMessageText
                    .builder()
                    .text(message)
                    .messageId(messageId)
                    .chatId(chatId)
                    .parseMode(ParseMode.HTML)
                    .replyMarkup(InlineKey.groupSetting(groupId))
                    .build();

            client.execute(editMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при изменении сообщения. {}", e.toString());
        }
    }

    private void answerCallBack(String id) {
        try {
            client.execute(new AnswerCallbackQuery(id));
        } catch (TelegramApiException e) {
            log.error("Ошибка при нажатии кнопки: {}", e.toString());
        }
    }
}
