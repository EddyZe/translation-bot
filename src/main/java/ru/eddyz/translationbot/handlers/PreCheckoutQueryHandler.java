package ru.eddyz.translationbot.handlers;


import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;

public interface PreCheckoutQueryHandler {

    void handle(PreCheckoutQuery preCheckoutQuery);
}
