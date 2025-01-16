package ru.eddyz.translationbot.commands.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.translationbot.commands.SelectPaymentType;
import ru.eddyz.translationbot.keyboards.InlineKey;


@Component
@RequiredArgsConstructor
@Slf4j
public class SelectPaymentTypeImpl implements SelectPaymentType {

    private final TelegramClient client;

    @Override
    public void execute(CallbackQuery callbackQuery, boolean edit) {
        var chatId = callbackQuery.getMessage().getChatId();
        var messageId = callbackQuery.getMessage().getMessageId();

        var data = callbackQuery.getData();
        var split = data.split(":");
        var groupId = Long.parseLong(split[1]);

        if (edit)
            editMessage(chatId, messageId, groupId);
        else
            sendMessage(chatId, groupId);

        answerCallBack(callbackQuery.getId());
    }

    private String generateMessage() {
        return """
                Тут вы можете выбрать способ оплаты, который будет вам удобнее.
                
                Выберите способ оплаты:
                """;
    }

    private void sendMessage(Long chatId, Long groupId) {
        try {
            var sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text(generateMessage())
                    .replyMarkup(InlineKey.selectPaymentMode(groupId))
                    .build();

            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при изменении сообщения при выборе способа оплаты {}", e.toString());
        }
    }

    private void editMessage(Long chatId, Integer messageId, Long groupId) {
        try {
            var editMessage = EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text(generateMessage())
                    .replyMarkup(InlineKey.selectPaymentMode(groupId))
                    .build();

            client.execute(editMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при изменении сообщения при выборе способа оплаты {}", e.toString());
        }
    }

    private void answerCallBack(String id) {
        try {
            client.execute(new AnswerCallbackQuery(id));
        } catch (TelegramApiException e) {
            log.error("Ошибка при нажатии на кнопку {}", e.toString());
        }
    }

}
