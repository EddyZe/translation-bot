package ru.eddyz.translationbot.commands.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.translationbot.commands.ShowPrices;
import ru.eddyz.translationbot.domain.entities.Price;
import ru.eddyz.translationbot.domain.enums.PaymentType;
import ru.eddyz.translationbot.keyboards.InlineKey;
import ru.eddyz.translationbot.services.PriceService;
import ru.eddyz.translationbot.util.UserCurrentPages;

import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class ShowPricesImpl implements ShowPrices {

    private final PriceService priceService;
    private final TelegramClient telegramClient;

    private final int PAGE_SIZE = 5;


    @Override
    public void execute(CallbackQuery callbackQuery, PaymentType type) {
        var chatId = callbackQuery.getMessage().getChatId();
        var messageId = callbackQuery.getMessage().getMessageId();

        if (type == null) {
            sendErrorMessage(chatId);
            return;
        }

        var data = callbackQuery.getData();
        var split = data.split(":");
        var groupId = Long.parseLong(split[1]);

        var currentPage = UserCurrentPages.getPriceCurrentPage(chatId);
        var elements = getPages(type, currentPage);

        var totalPages = elements.getTotalPages();
        var visNext = currentPage < totalPages - 1;
        var visBack = currentPage > 0;

        editMessage(chatId, messageId, elements.stream().toList(), type, groupId, visNext, visBack);
    }

    private void sendErrorMessage(Long chatId) {
        try {
            var sendMessage = SendMessage.builder()
                    .text("Что-то пошло не так, попробуйте выбрать способ оплаты еще раз!")
                    .chatId(chatId)
                    .build();

            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения с ошибкой, при отображении цен: {}", e.toString());
        }
    }

    private void editMessage(Long chatId,
                             Integer messageId,
                             List<Price> prices,
                             PaymentType type,
                             Long groupId,
                             boolean visNext,
                             boolean visBack) {
        try {
            var editMessage = EditMessageText.builder()
                    .text("Выберите кол-во символов, которое хотите приобрести: ")
                    .messageId(messageId)
                    .chatId(chatId)
                    .replyMarkup(InlineKey.prices(prices, groupId, type, visNext, visBack))
                    .build();

            telegramClient.execute(editMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка изменения сообщения при открытии цен: {}", e.toString());
        }

    }

    private Page<Price> getPages(PaymentType type, Integer currentPage) {
        Pageable pageable = PageRequest.of(currentPage, PAGE_SIZE);
        return priceService.findByType(type, pageable);
    }
}
