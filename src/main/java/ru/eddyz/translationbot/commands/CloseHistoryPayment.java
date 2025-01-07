package ru.eddyz.translationbot.commands;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CloseHistoryPayment {

    void execute(CallbackQuery callbackQuery);
}
