package ru.eddyz.translationbot.commands;


import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CreateInvoice {

    void execute(CallbackQuery callbackQuery);
}
