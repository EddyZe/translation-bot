package ru.eddyz.translationbot.commands;


import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CloseWindow {

    void execute(CallbackQuery callbackQuery, Class<?> clazz);
}
