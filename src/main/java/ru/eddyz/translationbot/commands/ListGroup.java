package ru.eddyz.translationbot.commands;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface ListGroup {

    void execute(CallbackQuery callbackQuery);
    void execute(Message message);
}
