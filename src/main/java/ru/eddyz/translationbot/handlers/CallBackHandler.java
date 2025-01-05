package ru.eddyz.translationbot.handlers;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallBackHandler {


    void handle(CallbackQuery callbackQuery);
}
