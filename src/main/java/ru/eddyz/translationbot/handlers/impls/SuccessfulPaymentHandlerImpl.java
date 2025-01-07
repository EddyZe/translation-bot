package ru.eddyz.translationbot.handlers.impls;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment;
import ru.eddyz.translationbot.domain.payloads.PaymentPayload;
import ru.eddyz.translationbot.handlers.SuccessfulPaymentHandler;
import ru.eddyz.translationbot.services.SuccessfulPaymentService;


@Component
@Slf4j
@RequiredArgsConstructor
public class SuccessfulPaymentHandlerImpl implements SuccessfulPaymentHandler {

    private final ObjectMapper objectMapper;

    private final SuccessfulPaymentService successfulPaymentService;

    @Override
    public void handle(SuccessfulPayment successfulPayment) {
        try {
            var paymentPayload = objectMapper.readValue(successfulPayment.getInvoicePayload(), PaymentPayload.class);

            successfulPaymentService.successfulPaymentChars(paymentPayload, successfulPayment.getTelegramPaymentChargeId());
        } catch (JsonProcessingException e) {
            log.error("Ошибка при парсинге payload из полученной успешной оплаты: {}", e.toString());
        }
    }
}
