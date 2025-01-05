package ru.eddyz.translationbot.handlers;

import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface MessageHandler {

    void handle(Message message);
}
