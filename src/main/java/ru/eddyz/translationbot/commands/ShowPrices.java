package ru.eddyz.translationbot.commands;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.eddyz.translationbot.domain.enums.PaymentType;

public interface ShowPrices {

    void execute(CallbackQuery callbackQuery, PaymentType type);
}
