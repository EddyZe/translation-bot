package ru.eddyz.translationbot.commands.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.translationbot.commands.ShowLanguage;
import ru.eddyz.translationbot.domain.entities.Group;
import ru.eddyz.translationbot.domain.entities.LanguageTranslation;
import ru.eddyz.translationbot.keyboards.InlineKey;
import ru.eddyz.translationbot.services.GroupService;
import ru.eddyz.translationbot.services.LanguageService;
import ru.eddyz.translationbot.util.UserCurrentPages;

import java.util.List;
import java.util.NoSuchElementException;


@Component
@RequiredArgsConstructor
@Slf4j
public class ShowLanguageImpl implements ShowLanguage {

    private final LanguageService languageService;
    private final GroupService groupService;

    private final TelegramClient telegramClient;

    private final int MAX_ELEMENTS = 5;

    @Override
    @Transactional
    public void execute(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getMessage().getChatId();
        var messageId = callbackQuery.getMessage().getMessageId();
        var data = callbackQuery.getData();

        var splitData = data.split(":");
        var groupId = Long.parseLong(splitData[1]);

        try {
            var group = groupService.findById(groupId);

            var currentPage = UserCurrentPages.getLanguagesCurrentPage(chatId);
            var elements = getPages(currentPage);

            var totalPages = elements.getTotalPages();
            var visNext = currentPage < totalPages - 1;
            var visBack = currentPage > 0;

            editMessage(chatId, messageId, elements.stream().toList(), group, visNext, visBack);
        } catch (NoSuchElementException e) {
            sendMessage(chatId);
        }

        answerCallback(callbackQuery.getId());
    }

    private void sendMessage(Long chatId) {
        try {
            var sendMessage = SendMessage.builder()
                    .text("Группа не найдена, возможно она была удалена. Попробуйте открыть список заново.")
                    .chatId(chatId)
                    .parseMode(ParseMode.HTML)
                    .build();

            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения в списке языков: {}", e.toString());
        }
    }

    private void editMessage(Long chatId,
                             Integer messageId,
                             List<LanguageTranslation> languages,
                             Group group,
                             boolean visNext,
                             boolean visBack) {
        try {
            var editMessage = EditMessageText.builder()
                    .text("Выберите языки, которые хотите использовать для переводов в группе:")
                    .chatId(chatId)
                    .messageId(messageId)
                    .replyMarkup(InlineKey.selectLanguage(languages, group, visNext, visBack))
                    .build();

            telegramClient.execute(editMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при формировании списка языков: {}", e.toString());
        }
    }

    private Page<LanguageTranslation> getPages(Integer currentPage) {
        Pageable pageable = PageRequest.of(currentPage, MAX_ELEMENTS);
        return languageService.finaAll(pageable);
    }

    private void answerCallback(String callBackQueryId) {
        try {
            telegramClient.execute(new AnswerCallbackQuery(callBackQueryId));
        } catch (TelegramApiException e) {
            log.error("Ошибка при нажатии кнопки: {}", e.toString());
        }
    }
}
