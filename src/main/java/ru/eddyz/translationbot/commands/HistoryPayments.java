package ru.eddyz.translationbot.commands;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface HistoryPayments {

    void execute(Message message);
    void execute(CallbackQuery callbackQuery);
}
