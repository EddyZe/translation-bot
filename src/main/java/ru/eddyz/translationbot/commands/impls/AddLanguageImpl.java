package ru.eddyz.translationbot.commands.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.translationbot.commands.AddLanguage;
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
public class AddLanguageImpl implements AddLanguage {

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

        var split = data.split(":");
        var languageId = Long.parseLong(split[1]);
        var groupId = Long.parseLong(split[2]);

        try {
            var group = groupService.findById(groupId);
            var groupLanguages = group.getLanguages();
            var currentPage = UserCurrentPages.getLanguagesCurrentPage(chatId);
            var elements = getPages(currentPage);
            var currentLanguage = elements.stream()
                    .filter(l -> l.getLanguageId().equals(languageId))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Язык с таким ID не найден."));

            if (groupLanguages.contains(currentLanguage)) {
                groupLanguages.remove(currentLanguage);
                currentLanguage.getChats().remove(group);
            } else {
                groupLanguages.add(currentLanguage);
                currentLanguage.getChats().add(group);
            }

            group.setLanguages(groupLanguages);
            groupService.update(group);
            languageService.save(currentLanguage);

            var totalPages = elements.getTotalPages();
            var visNext = currentPage < totalPages - 1;
            var visBack = currentPage > 0;

            editMessage(chatId, messageId, elements.stream().toList(), group, visNext, visBack);

        } catch (NoSuchElementException e) {
            sendErrorMessage(chatId);
        }

        answerCallback(callbackQuery.getId());
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
            log.error("Ошибка при выборе языка: {}", e.toString());
        }
    }

    private void sendErrorMessage(Long chatId) {
        try {
            var sendMessage = SendMessage.builder()
                    .text("Группа или язык не доступны. Попробуйте повторить попытку позже.")
                    .chatId(chatId)
                    .build();

            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения с ошибкой в выборе языка: {}", e.toString());
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
