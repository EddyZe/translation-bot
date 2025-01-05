package ru.eddyz.translationbot.commands;


import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface Start {

    void execute(Message message);
}
