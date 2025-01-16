package ru.eddyz.translationbot.commands.impls;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.translationbot.commands.ListGroup;
import ru.eddyz.translationbot.domain.entities.Group;
import ru.eddyz.translationbot.keyboards.InlineKey;
import ru.eddyz.translationbot.services.GroupService;
import ru.eddyz.translationbot.util.UserCurrentPages;
import ru.eddyz.translationbot.util.UserState;

import java.util.List;


@Component
@RequiredArgsConstructor
public class ListGroupImpl implements ListGroup {

    private static final Logger log = LoggerFactory.getLogger(ListGroupImpl.class);
    private final GroupService groupService;
    private final TelegramClient client;
    private final Integer MAX_ELEMENT_PAGE = 5;


    @Override
    public void execute(Message message) {
        var chatId = message.getChatId();
        UserState.clearAll(chatId);

        var currentPage = UserCurrentPages.getListGroupCurrentPage(chatId);
        Page<Group> groups = getPages(chatId);

        var totalPages = groups.getTotalPages();
        var visNext = currentPage < totalPages - 1;
        var visBack = currentPage > 0;

        sendMessage(chatId, groups.stream().toList(), visNext, visBack);
    }

    @Override
    public void execute(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getMessage().getChatId();
        UserState.clearAll(chatId);
        var messageId = callbackQuery.getMessage().getMessageId();
        var currentPage = UserCurrentPages.getListGroupCurrentPage(chatId);
        Page<Group> groups = getPages(chatId);

        var totalPages = groups.getTotalPages();
        var visNext = currentPage < totalPages - 1;
        var visBack = currentPage > 0;

        editMessage(chatId, messageId, groups.stream().toList(), visNext, visBack);
        answerCallback(callbackQuery.getId());
    }

    private void editMessage(Long chatId, Integer messageId, List<Group> list, boolean visNext, boolean visBack) {
        try {
            var editMessage = EditMessageText
                    .builder()
                    .messageId(messageId)
                    .chatId(chatId)
                    .text(generateMessage(chatId))
                    .parseMode(ParseMode.HTML)
                    .replyMarkup(InlineKey.listGroupButton(list, visNext, visBack))
                    .build();

            client.execute(editMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при изменении сообщения: {}",  e.toString());
        }
    }

    private void answerCallback(String callBackQueryId) {
        try {
            client.execute(new AnswerCallbackQuery(callBackQueryId));
        } catch (TelegramApiException e) {
            log.error("Ошибка при нажатии кнопки: {}", e.toString());
        }
    }

    private void sendMessage(Long chatId, List<Group> groups, boolean visNext, boolean visBack) {
        try {
            var sendMessage = SendMessage.builder()
                    .text(generateMessage(chatId))
                    .chatId(chatId)
                    .parseMode(ParseMode.HTML)
                    .replyMarkup(InlineKey.listGroupButton(groups, visNext, visBack))
                    .build();
            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке списка групп. Пользователь: {}. Ошибка: {}", chatId, e.toString());
        }
    }

    private Page<Group> getPages(Long chatId) {
        var currentPage = UserCurrentPages.getListGroupCurrentPage(chatId);
        Pageable pageable = PageRequest.of(currentPage, MAX_ELEMENT_PAGE);
        return groupService.findByChatId(chatId, pageable);
    }

    private String generateMessage(Long chatId) {
        return """
                <b>Список групп</b>
                
                Тут можно посмотреть список добавленных групп.
                Или удалить ненужные, докупить символы.
                
                Если вы добавили группу, но она не отображается здесь, то напишите в привязываемой группе❗️ команду 👇
                /translate %s
                """.formatted(chatId);
    }
}
