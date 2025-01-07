package ru.eddyz.translationbot.commands.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.translationbot.commands.CloseHistoryPayment;
import ru.eddyz.translationbot.util.UserCurrentPages;


@Component
@RequiredArgsConstructor
@Slf4j
public class CloseHistoryPaymentImpl implements CloseHistoryPayment {

    private final TelegramClient telegramClient;

    @Override
    public void execute(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getMessage().getChatId();
        var messageId = callbackQuery.getMessage().getMessageId();

        try {
            var deleteMessage = DeleteMessage.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .build();

            telegramClient.execute(deleteMessage);
            telegramClient.execute(new AnswerCallbackQuery(callbackQuery.getId()));
            UserCurrentPages.resetHistoryPaymentCurrentPage(chatId);
        } catch (TelegramApiException e) {
            log.error("Ошибка при закрытии истории: {}", e.toString());
        }
    }
}
