package ru.eddyz.translationbot.commands.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.translationbot.commands.HistoryPayments;
import ru.eddyz.translationbot.domain.entities.Payment;
import ru.eddyz.translationbot.keyboards.InlineKey;
import ru.eddyz.translationbot.services.PaymentService;
import ru.eddyz.translationbot.util.UserCurrentPages;

import java.time.format.DateTimeFormatter;
import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class HistoryPaymentsImpl implements HistoryPayments {

    private final TelegramClient telegramClient;
    private final PaymentService paymentService;

    private final int MAX_ELEMENTS = 5;

    @Override
    public void execute(Message message) {
        var chatId = message.getChatId();

        var currentPage = UserCurrentPages.getHistoryPaymentCurrentPage(chatId);
        var elements = getPages(chatId, currentPage);

        var totalPages = elements.getTotalPages();
        var visNext = currentPage < totalPages - 1;
        var visBack = currentPage > 0;

        sendMessage(chatId, generateMessage(elements.stream().toList()), visNext, visBack);
    }

    @Override
    public void execute(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getMessage().getChatId();
        var messageId = callbackQuery.getMessage().getMessageId();

        var currentPage = UserCurrentPages.getHistoryPaymentCurrentPage(chatId);
        var elements = getPages(chatId, currentPage);

        var totalPages = elements.getTotalPages();
        var visNext = currentPage < totalPages - 1;
        var visBack = currentPage > 0;

        editMessage(chatId, messageId, generateMessage(elements.stream().toList()), visNext, visBack);
        answerCallback(callbackQuery.getId());
    }

    private void editMessage(Long chatId, Integer messageId, String message, boolean visNext, boolean visBack) {
        try {
            var editMessage = EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .parseMode(ParseMode.HTML)
                    .text(message)
                    .replyMarkup(InlineKey.historyPayment(visNext, visBack))
                    .build();

            telegramClient.execute(editMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при изменении сообщения в истории платежей: {}", e.toString());
        }
    }

    private void sendMessage(Long chatId, String message, boolean visNext, boolean visBack) {
        try {
            var sendMessage = SendMessage.builder()
                    .text(message)
                    .chatId(chatId)
                    .parseMode(ParseMode.HTML)
                    .replyMarkup(InlineKey.historyPayment(visNext, visBack))
                    .build();

            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения с историей платежей: {}", e.toString());
        }
    }

    private String generateMessage(List<Payment> payments) {
        var sb = new StringBuilder("<b>История платежей 📋</b>\n\n");
        var dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm");

        payments.stream()
                .sorted(((o1, o2) -> {
                    if (o2.getCreatedAt().isAfter(o1.getCreatedAt()))
                        return 1;
                    else return -1;
                }))
                .forEach(payment -> sb
                .append("<b>ID платежа</b>: %s\n".formatted(payment.getPaymentId()))
                .append("<b>Сумма</b>: %.2f %s\n".formatted(payment.getAmount(),payment.getAsset()))
                .append("<b>Дата</b>: %s\n\n".formatted(dtf.format(payment.getCreatedAt()))));

        return sb.toString();
    }

    private Page<Payment> getPages(Long chatId, Integer currentPage) {
        Pageable pageable = PageRequest.of(currentPage, MAX_ELEMENTS);
        return paymentService.findByChatId(chatId, pageable);
    }

    private void answerCallback(String callBackQueryId) {
        try {
            telegramClient.execute(new AnswerCallbackQuery(callBackQueryId));
        } catch (TelegramApiException e) {
            log.error("Ошибка при нажатии кнопки: {}", e.toString());
        }
    }
}
