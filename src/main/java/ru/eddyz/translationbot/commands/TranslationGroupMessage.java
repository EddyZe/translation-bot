package ru.eddyz.translationbot.commands;


import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface TranslationGroupMessage {

    void execute(Message message);
}
