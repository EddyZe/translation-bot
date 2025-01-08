package ru.eddyz.translationbot.commands;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface AddLanguage {

    void execute(CallbackQuery callbackQuery);
}
