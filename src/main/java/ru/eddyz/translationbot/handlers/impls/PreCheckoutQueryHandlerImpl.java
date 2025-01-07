package ru.eddyz.translationbot.handlers.impls;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.translationbot.handlers.PreCheckoutQueryHandler;



@Component
@Slf4j
@RequiredArgsConstructor
public class PreCheckoutQueryHandlerImpl implements PreCheckoutQueryHandler {

    private final TelegramClient telegramClient;

    @Override
    public void handle(PreCheckoutQuery preCheckoutQuery) {
        try {
            var answerPreCheckout = AnswerPreCheckoutQuery.builder()
                    .preCheckoutQueryId(preCheckoutQuery.getId())
                    .errorMessage("Ошибка при получении платежа!")
                    .ok(true)
                    .build();

            telegramClient.execute(answerPreCheckout);
        } catch (TelegramApiException e) {
            log.error("Ошибка при ответе на запрос по оплате счета: {}", e.toString());
        }
    }
}
