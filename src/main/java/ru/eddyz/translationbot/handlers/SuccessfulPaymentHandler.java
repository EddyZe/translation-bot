package ru.eddyz.translationbot.handlers;

import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment;

public interface SuccessfulPaymentHandler {


    void handle(SuccessfulPayment successfulPayment);
}
