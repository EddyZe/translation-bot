package ru.eddyz.translationbot.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.translationbot.clients.cryptopay.payloads.UpdateCrypto;
import ru.eddyz.translationbot.domain.entities.Group;
import ru.eddyz.translationbot.domain.entities.Payment;
import ru.eddyz.translationbot.domain.payloads.PaymentPayload;
import ru.eddyz.translationbot.services.GroupService;
import ru.eddyz.translationbot.services.PaymentService;
import ru.eddyz.translationbot.services.PriceService;
import ru.eddyz.translationbot.services.UserService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("payment")
@Slf4j
public class PaymentController {


    private final String cryptoPayToken;
    private final ObjectMapper objectMapper;
    private final GroupService groupService;
    private final PriceService priceService;
    private final PaymentService paymentService;
    private final UserService userService;

    private final TelegramClient telegramClient;
    public PaymentController(@Value("${crypto.pay.token}") String cryptoPayToken,
                             ObjectMapper objectMapper, GroupService groupService, PriceService priceService,
                             PaymentService paymentService,
                             UserService userService, TelegramClient telegramClient) {
        this.cryptoPayToken = cryptoPayToken;
        this.objectMapper = objectMapper;
        this.groupService = groupService;
        this.priceService = priceService;
        this.paymentService = paymentService;
        this.userService = userService;
        this.telegramClient = telegramClient;
    }

    @Transactional
    @PostMapping("crypto-pay/{token}")
    public ResponseEntity<?> paymentWebhook(@PathVariable String token, @RequestBody UpdateCrypto updateCrypto) {
        if (!token.equals(cryptoPayToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Token invalid"));
        }
        log.info("Обработка updateId: {}", updateCrypto.getUpdateId());
        try {
            var paymentPayload = objectMapper.readValue(updateCrypto.getPayload().getPayload(), PaymentPayload.class);
            var group = groupService.findById(paymentPayload.getGroup().getGroupId());
            var price = priceService.findById(paymentPayload.getPrice().getPriceId());
            var user = userService.findByChatId(group.getChatId());

            if (user.isEmpty())
                return ResponseEntity.notFound().build();

            var payment = Payment.builder()
                    .payer(user.get())
                    .type(price.getType())
                    .asset(price.getAsset())
                    .chatId(group.getChatId())
                    .amount(price.getPrice())
                    .createdAt(LocalDateTime.now())
                    .numberCharacters(price.getNumberCharacters())
                    .build();

            paymentService.save(payment);

            var groupCurrentCharacters = group.getLimitCharacters();
            group.setLimitCharacters(groupCurrentCharacters + price.getNumberCharacters());
            groupService.save(group);

            sendMessage(group.getChatId(), generateMessage(payment, group));

            //TODO добавить уведомление для админа
        } catch (JsonProcessingException e) {
            log.error("Ошибка при парсинге json в payload crypto: {}", e.toString());
        }


        return ResponseEntity.ok(HttpStatus.OK);
    }

    private void sendMessage(Long chatId, String message) {
        try {
            var sendMessage = SendMessage.builder()
                    .text(message)
                    .chatId(chatId)
                    .build();

            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения в контролере с оплатой: {}", e.toString());
        }
    }

    private String generateMessage(Payment payment, Group group) {
        return """
                Вы успешно приобрели %d символов за %.2f %s.
                
                Кол-во символов в группе %s обновлено.
                Новое кол-во символов: %d
                """.formatted(payment.getNumberCharacters(), payment.getAmount(), payment.getAsset(), group.getTitle(), group.getLimitCharacters());
    }
}
