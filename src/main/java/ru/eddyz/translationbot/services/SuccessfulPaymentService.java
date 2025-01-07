package ru.eddyz.translationbot.services;


import ru.eddyz.translationbot.domain.payloads.PaymentPayload;

public interface SuccessfulPaymentService {

    void successfulPaymentChars(PaymentPayload paymentPayload, String telegramSuccessfulId);
}
