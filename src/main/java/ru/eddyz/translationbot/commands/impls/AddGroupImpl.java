package ru.eddyz.translationbot.commands.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.translationbot.commands.AddGroup;
import ru.eddyz.translationbot.domain.entities.Group;
import ru.eddyz.translationbot.services.DeletedGroupService;
import ru.eddyz.translationbot.services.GroupService;
import ru.eddyz.translationbot.services.LanguageService;
import ru.eddyz.translationbot.services.UserService;
import ru.eddyz.translationbot.util.UserState;

import java.util.ArrayList;
import java.util.NoSuchElementException;


@Component
@RequiredArgsConstructor
@Slf4j
public class AddGroupImpl implements AddGroup {

    private final TelegramClient client;
    private final GroupService groupService;
    private final UserService userService;
    private final DeletedGroupService deletedGroupService;
    private final LanguageService languageService;

    @Value("${telegram.groups.starting-chars}")
    private Integer startingCharGroup;


    @Override
    @Transactional
    public void execute(Message message) {
        var groupChatId = message.getChatId();
        var userChatId = message.getFrom().getId();
        var title = message.getChat().getTitle();
        var commandChatId = Long.parseLong(message.getText().split(" ")[1].trim());

        if (userChatId != commandChatId)
            return;

        addNewGroup(userChatId, groupChatId, title);
    }


    private void addNewGroup(Long chatId, Long groupChatId, String title) {
        var user = userService.findByChatId(chatId);
        try {
            if (user.isEmpty()) {
                sendMessage(chatId, "Что-то пошло не так. Попробуйте снова.");
                UserState.clearUserState(chatId);
                return;
            }


            var newGroup = Group.builder()
                    .chatId(chatId)
                    .title(title)
                    .owner(user.get())
                    .languages(new ArrayList<>())
                    .translatingMessages(true)
                    .telegramGroupId(groupChatId)
                    .limitCharacters(startingCharGroup)
                    .build();

            var deletedGroup = deletedGroupService.findByTelegramGroupId(groupChatId);

            deletedGroup.ifPresent(group -> newGroup.setLimitCharacters(group.getChars()));



            var saved = groupService.save(newGroup);

            try {
                var ru = languageService.findByCode("ru");
                var en = languageService.findByCode("en");
                saved.getLanguages().add(ru);
                saved.getLanguages().add(en);
                ru.getChats().add(saved);
                en.getChats().add(saved);
                groupService.update(saved);
                languageService.save(ru);
                languageService.save(en);
            } catch (NoSuchElementException e) {
                log.error("Ошибка при добавлении стандартных языков {}", e.toString());
            }


            sendMessage(chatId, "Группа %s успешна добавлена.".formatted(title));
        } catch (NumberFormatException e) {
            sendMessage(chatId, "ID должно содержать только цифры! Введите ID снова: ");
        } catch (IllegalArgumentException e) {
            sendMessage(groupChatId, "Данная группа уже существует в базе!");
        }
    }

    private void sendMessage(Long chatId, String message) {
        try {
            var sendMessage = SendMessage.builder()
                    .text(message)
                    .chatId(chatId)
                    .parseMode(ParseMode.HTML)
                    .build();

            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения пользователю: {}. {}", client, e.toString());
        }
    }
}
