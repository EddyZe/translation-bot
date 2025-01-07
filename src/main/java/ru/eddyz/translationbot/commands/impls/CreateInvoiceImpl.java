package ru.eddyz.translationbot.commands.impls;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.translationbot.clients.cryptopay.CryptoPayClient;
import ru.eddyz.translationbot.clients.cryptopay.payloads.ResponseCrypto;
import ru.eddyz.translationbot.commands.CreateInvoice;
import ru.eddyz.translationbot.domain.entities.Group;
import ru.eddyz.translationbot.domain.entities.Price;
import ru.eddyz.translationbot.domain.enums.PaymentType;
import ru.eddyz.translationbot.domain.payloads.GroupPayload;
import ru.eddyz.translationbot.domain.payloads.PaymentPayload;
import ru.eddyz.translationbot.domain.payloads.PricePayload;
import ru.eddyz.translationbot.services.GroupService;
import ru.eddyz.translationbot.services.PriceService;

import java.util.List;
import java.util.NoSuchElementException;


@Component
@RequiredArgsConstructor
@Slf4j
public class CreateInvoiceImpl implements CreateInvoice {

    private final CryptoPayClient cryptoPayClient;
    private final TelegramClient telegramClient;

    private final GroupService groupService;
    private final PriceService priceService;

    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void execute(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getMessage().getChatId();
        var data = callbackQuery.getData();

        var split = data.split(":");
        var priceId = Long.parseLong(split[1]);
        var groupId = Long.parseLong(split[2]);

        try {
            var price = priceService.findById(priceId);

            if (price.getType() == PaymentType.CRYPTO_PAY) {
                createInvoiceCrypto(groupId, price, chatId);
            }

        } catch (NoSuchElementException e) {
            sendMessage(chatId, "Что-то пошло не так. Попробуйте повторить попытку!");
        } catch (JsonProcessingException e) {
            log.error("Ошибка при составления json в выставлении счета crypto pay: {}", e.toString());
            sendMessage(chatId, "Произошла ошибка, попробуйте снова.");
        }

        answerCallBack(callbackQuery.getId());
    }

    private void createInvoiceCrypto(long groupId, Price price, Long chatId) throws JsonProcessingException {
        var group = groupService.findById(groupId);

        var payload = objectMapper.writeValueAsString(buildPaymentPayload(group, price));

        var invoice = createInvoice(price, payload);
        showInvoiceCrypto(chatId, invoice.getResult().getMiniAppInvoiceUrl());
    }

    private PaymentPayload buildPaymentPayload(Group group, Price price) {
        var groupPayload = GroupPayload.builder()
                .title(group.getTitle())
                .groupId(group.getGroupId())
                .telegramGroupId(group.getTelegramGroupId())
                .limitCharacters(group.getLimitCharacters())
                .languages(group.getLanguages())
                .chatId(group.getChatId())
                .build();

        var pricePayload = PricePayload.builder()
                .priceId(price.getPriceId())
                .asset(price.getAsset())
                .numberCharacters(price.getNumberCharacters())
                .price(price.getPrice())
                .build();

        return PaymentPayload.builder()
                .group(groupPayload)
                .price(pricePayload)
                .build();
    }

    private ResponseCrypto createInvoice(Price price, String payload) {
        return cryptoPayClient.createInvoice(
                price.getPrice(),
                price.getAsset(),
                "Покупка %d символов".formatted(price.getNumberCharacters()),
                payload
        );
    }

    private void sendMessage(Long chatId, String message) {
        try {
            var sendMessage = SendMessage.builder()
                    .text(message)
                    .chatId(chatId)
                    .build();

            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения в создании счета crypto pay: {}", e.toString());
        }
    }

    private void showInvoiceCrypto(Long chatId, String invoiceUrl) {
        try {
            var sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("Чтобы получить символы сделайте платеж, нажав на кнопку ниже 👇")
                    .replyMarkup(createInvoiceUrl(invoiceUrl))
                    .build();

            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при изменении сообщения в создании счета: {}", e.toString());
        }
    }

    private InlineKeyboardMarkup createInvoiceUrl(String invoiceUrl) {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(new InlineKeyboardRow(
                        InlineKeyboardButton.builder()
                                .text("Оплатить")
                                .url(invoiceUrl)
                                .build()
                )))
                .build();
    }

    private void answerCallBack(String id) {
        try {
            telegramClient.execute(new AnswerCallbackQuery(id));
        } catch (TelegramApiException e) {
            log.error("Ошибка при нажатии на кнопку в выставлении счета crypto pay");
        }
    }
}
