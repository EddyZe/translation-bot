package ru.eddyz.translationbot.commands.impls;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.translationbot.commands.CloseGroupList;
import ru.eddyz.translationbot.util.UserCurrentPages;
import ru.eddyz.translationbot.util.UserState;


@Component
@RequiredArgsConstructor
public class CloseGroupListImpl implements CloseGroupList {


    private static final Logger log = LoggerFactory.getLogger(CloseGroupListImpl.class);
    private final TelegramClient client;


    @Override
    public void execute(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getMessage().getChatId();
        UserCurrentPages.resetListGroupCurrentPage(chatId);

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
