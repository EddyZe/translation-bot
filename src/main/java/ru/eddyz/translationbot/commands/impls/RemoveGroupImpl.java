package ru.eddyz.translationbot.commands.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.translationbot.commands.ListGroup;
import ru.eddyz.translationbot.commands.RemoveGroup;
import ru.eddyz.translationbot.domain.entities.DeletedGroup;
import ru.eddyz.translationbot.repositories.LanguageRepository;
import ru.eddyz.translationbot.services.DeletedGroupService;
import ru.eddyz.translationbot.services.GroupService;

import java.util.NoSuchElementException;


@Component
@RequiredArgsConstructor
@Slf4j
public class RemoveGroupImpl implements RemoveGroup {

    private final GroupService groupService;
    private final TelegramClient client;
    private final ListGroup listGroup;
    private final DeletedGroupService deletedGroupService;
    private final LanguageRepository languageRepository;


    @Override
    @Transactional
    public void execute(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getMessage().getChatId();
        var messageId = callbackQuery.getMessage().getMessageId();
        var splitData = callbackQuery.getData().split(":");
        var groupId = Long.parseLong(splitData[1]);


        try {
            var group = groupService.findById(groupId);
            var deletedGroupOp = deletedGroupService.findByTelegramGroupId(group.getTelegramGroupId());

            if (deletedGroupOp.isEmpty()) {
                deletedGroupService.save(DeletedGroup.builder()
                        .telegramGroupId(group.getTelegramGroupId())
                        .chars(group.getLimitCharacters())
                        .build());
            } else {
                var deletedGroup = deletedGroupOp.get();
                deletedGroup.setChars(group.getLimitCharacters());
                deletedGroupService.save(deletedGroup);
            }
            groupService.deleteLinksLanguages(groupId);
            groupService.deleteById(groupId);
            sendMessage(chatId, "Группа Удалена");
            deleteMessage(chatId, messageId);
            answerCallBack(callbackQuery.getId());
            listGroup.execute((Message) callbackQuery.getMessage());
        } catch (NoSuchElementException e) {
            sendMessage(chatId, "Что-то пошло не так. Возможно, что группа уже удалена, попробуйте открыть список заново, чтобы он обновился!");
        }
    }

    private void sendMessage(Long chatId, String message) {
        try {
            var sendMessage = SendMessage.builder()
                    .text(message)
                    .chatId(chatId)
                    .build();

            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения, при успешном удалении! {}", e.toString());
        }
    }

    private void answerCallBack(String id) {
        try {
            client.execute(new AnswerCallbackQuery(id));
        } catch (TelegramApiException e) {
            log.error("Ошибка при нажатии кнопки при удалении группы из списка {}", e.toString());
        }
    }

    private void deleteMessage(Long chatId, Integer messageId) {
        try {
            var deleteMessage = DeleteMessage
                    .builder()
                    .messageId(messageId)
                    .chatId(chatId)
                    .build();

            client.execute(deleteMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при удалении сообщения, при удалении группы {}", e.toString());
        }
    }
}
