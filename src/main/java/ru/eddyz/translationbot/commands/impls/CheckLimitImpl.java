package ru.eddyz.translationbot.commands.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.eddyz.translationbot.commands.CheckLimit;
import ru.eddyz.translationbot.domain.entities.Group;
import ru.eddyz.translationbot.keyboards.InlineKey;
import ru.eddyz.translationbot.services.GroupService;
import ru.eddyz.translationbot.util.UserCurrentPages;
import ru.eddyz.translationbot.util.UserState;

import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class CheckLimitImpl implements CheckLimit {

    private final TelegramClient client;
    private final GroupService groupService;

    private final int PAGE_ELEMENTS = 5;

    @Override
    public void execute(Message message) {
        var chatId = message.getChatId();
        UserState.clearAll(chatId);

        var elements = getPages(chatId);
        var currentPage = UserCurrentPages.getCheckLimitCurrentPage(chatId);

        var totalPages = elements.getTotalPages();
        var visNext = currentPage < totalPages - 1;
        var visBack = currentPage > 0;

        sendMessage(chatId, generateMessage(elements.stream().toList()), visNext, visBack);
    }

    @Override
    public void execute(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getMessage().getChatId();
        UserState.clearAll(chatId);

        var messageId = callbackQuery.getMessage().getMessageId();
        var currentPage = UserCurrentPages.getCheckLimitCurrentPage(chatId);
        var groups = getPages(chatId);

        var totalPages = groups.getTotalPages();
        var visNext = currentPage < totalPages - 1;
        var visBack = currentPage > 0;

        editMessage(chatId, messageId, generateMessage(groups.stream().toList()), visNext, visBack);

        answerCallback(callbackQuery.getId());
    }

    private void editMessage(Long chatId, Integer messageId, String message, boolean visNext, boolean visBack) {
        try {
            var editMessage = EditMessageText
                    .builder()
                    .messageId(messageId)
                    .chatId(chatId)
                    .text(message)
                    .parseMode(ParseMode.HTML)
                    .replyMarkup(InlineKey.checkLimits(visNext, visBack))
                    .build();

            client.execute(editMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при изменении сообщения: {}",  e.toString());
        }
    }

    private void sendMessage(Long chatId, String message, boolean visNext, boolean visBack) {
        try {
            var sendMessage = SendMessage.builder()
                    .text(message)
                    .chatId(chatId)
                    .parseMode(ParseMode.HTML)
                    .replyMarkup(InlineKey.checkLimits(visNext, visBack))
                    .build();
            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке списка групп. Пользователь: {}. Ошибка: {}", chatId, e.toString());
        }
    }

    private Page<Group> getPages(Long chatId) {
        var currentPage = UserCurrentPages.getCheckLimitCurrentPage(chatId);
        Pageable pageable = PageRequest.of(currentPage, PAGE_ELEMENTS);
        return groupService.findByChatId(chatId, pageable);
    }

    private void answerCallback(String callBackQueryId) {
        try {
            client.execute(new AnswerCallbackQuery(callBackQueryId));
        } catch (TelegramApiException e) {
            log.error("Ошибка при нажатии кнопки: {}", e.toString());
        }
    }

    private String generateMessage(List<Group> groups) {
        var sb = new StringBuilder("<b>Оставшиеся лимиты ваших групп 📋</b>\n\n");

        groups.forEach(g -> sb.append("Группа: %s\n".formatted(g.getTitle()))
                .append("Осталось символов: %d\n\n".formatted(g.getLimitCharacters())));

        return sb.toString();
    }
}
