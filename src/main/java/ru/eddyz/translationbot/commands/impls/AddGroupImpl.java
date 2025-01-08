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
import ru.eddyz.translationbot.domain.enums.MainMenuButton;
import ru.eddyz.translationbot.domain.enums.UserStates;
import ru.eddyz.translationbot.services.GroupService;
import ru.eddyz.translationbot.services.UserService;
import ru.eddyz.translationbot.util.UserState;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


@Component
@RequiredArgsConstructor
@Slf4j
public class AddGroupImpl implements AddGroup {

    private final TelegramClient client;
    private final GroupService groupService;
    private final UserService userService;

    @Value("${telegram.groups.starting-chars}")
    private Integer startingCharGroup;

    private final Map<Long, String> nameGroup = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public void execute(Message message) {
        var chatId = message.getChatId();

        var currentState = UserState.getUserState(chatId);

        if (currentState.isEmpty())
            return;

        if (currentState.get() == MainMenuButton.ADD_GROUP) {
            sendMessage(chatId, generateMessageResponseSetName());
            UserState.setUserState(chatId, UserStates.ADD_GROUP_SET_NAME);
        } else if (currentState.get() == UserStates.ADD_GROUP_SET_NAME) {
            var name = message.getText();
            nameGroup.put(chatId, name);
            UserState.setUserState(chatId, UserStates.ADD_GROUP_SET_ID);
            sendMessage(chatId, "<b>Добавление группы</b>\n\nВведите ID группы: ");
        } else if (currentState.get() == UserStates.ADD_GROUP_SET_ID) {
            addNewGroup(chatId, message.getText());
        }
    }

    private void addNewGroup(Long chatId, String text) {
        var user = userService.findByChatId(chatId);
        try {
            var idGroup = Long.parseLong(text);

            if (user.isEmpty()) {
                sendMessage(chatId, "Что-то пошло не так. Попробуйте снова.");
                UserState.clearUserState(chatId);
                nameGroup.remove(chatId);
                return;
            }

            var newGroup = Group.builder()
                    .chatId(chatId)
                    .title(Optional.ofNullable(nameGroup.get(chatId)).orElse("Не указано"))
                    .owner(user.get())
                    .telegramGroupId(idGroup)
                    .limitCharacters(startingCharGroup)
                    .build();

            groupService.save(newGroup);
            UserState.clearUserState(chatId);
            nameGroup.remove(chatId);

            sendMessage(chatId, "Группа успешна добавлена. Не забудьте добавить бота в группу и сделать его администратором");
        } catch (NumberFormatException e) {
            sendMessage(chatId, "ID должно содержать только цифры! Введите ID снова: ");
        } catch (IllegalArgumentException e) {
            sendMessage(chatId, "Группа с таким ID уже существует. Повторите попытку!");
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

    private String generateMessageResponseSetName() {
        return """
                <b>Добавление группы</b>
                
                Введите название группы, это нужно для отображения в списке ваших групп:
                """;
    }
}
