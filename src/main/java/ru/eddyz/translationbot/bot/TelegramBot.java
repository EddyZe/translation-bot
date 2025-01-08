package ru.eddyz.translationbot.bot;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.eddyz.translationbot.domain.models.TelegramBotConfig;
import ru.eddyz.translationbot.handlers.CallBackHandler;
import ru.eddyz.translationbot.handlers.MessageHandler;
import ru.eddyz.translationbot.handlers.PreCheckoutQueryHandler;

@Component
@RequiredArgsConstructor
public class TelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final TelegramBotConfig telegramBotConfig;

    private final MessageHandler messageHandler;
    private final CallBackHandler callBackHandler;
    private final PreCheckoutQueryHandler preCheckoutQueryHandler;

    @Override
    public String getBotToken() {
        return telegramBotConfig.getToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {

        if (update.hasMessage())
            messageHandler.handle(update.getMessage());
        else if (update.hasCallbackQuery()) {
            callBackHandler.handle(update.getCallbackQuery());
        } else if (update.hasPreCheckoutQuery()) {
            preCheckoutQueryHandler.handle(update.getPreCheckoutQuery());
        }
    }
}
