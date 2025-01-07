package ru.eddyz.translationbot.commands.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.translationbot.commands.CheckLimit;
import ru.eddyz.translationbot.commands.CloseWindow;
import ru.eddyz.translationbot.commands.ListGroup;
import ru.eddyz.translationbot.util.UserCurrentPages;


@Component
@RequiredArgsConstructor
@Slf4j
public class CloseWindowImpl implements CloseWindow {

    private final TelegramClient client;


    @Override
    public void execute(CallbackQuery callbackQuery, Class<?> clazz) {
        var chatId = callbackQuery.getMessage().getChatId();

        if (clazz.getSimpleName().startsWith(ListGroup.class.getSimpleName()))
            UserCurrentPages.resetListGroupCurrentPage(chatId);
        else if (clazz.getSimpleName().startsWith(CheckLimit.class.getSimpleName())) {
            UserCurrentPages.resetCheckListCurrentPage(chatId);
        }

        try {
            var deleteMessage = DeleteMessage.builder()
                    .chatId(chatId)
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .build();

            client.execute(deleteMessage);
            client.execute(new AnswerCallbackQuery(callbackQuery.getId()));
        } catch (TelegramApiException e) {
            log.error("Ошибка при удалении сообщения с списком групп: {}", e.toString());
        }

    }
}
