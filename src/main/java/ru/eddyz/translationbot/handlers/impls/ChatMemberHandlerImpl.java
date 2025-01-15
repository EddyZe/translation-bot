package ru.eddyz.translationbot.handlers.impls;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberUpdated;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.translationbot.domain.entities.DeletedGroup;
import ru.eddyz.translationbot.domain.entities.Group;
import ru.eddyz.translationbot.domain.entities.LanguageTranslation;
import ru.eddyz.translationbot.domain.entities.User;
import ru.eddyz.translationbot.handlers.ChatMemberHandler;
import ru.eddyz.translationbot.services.DeletedGroupService;
import ru.eddyz.translationbot.services.GroupService;
import ru.eddyz.translationbot.services.LanguageService;
import ru.eddyz.translationbot.services.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;


@Component
@Slf4j
public class ChatMemberHandlerImpl implements ChatMemberHandler {

    private final GroupService groupService;
    private final DeletedGroupService deletedGroupService;
    private final LanguageService languageService;
    private final UserService userService;
    private final TelegramClient telegramClient;

    private final String botUsername;

    @Value("${telegram.groups.starting-chars}")
    private Integer startingCharGroup;

    public ChatMemberHandlerImpl(GroupService groupService, DeletedGroupService deletedGroupService, LanguageService languageService, UserService userService, TelegramClient telegramClient,
                                 @Value("${telegram.bot.username}") String botUsername) {
        this.groupService = groupService;
        this.deletedGroupService = deletedGroupService;
        this.languageService = languageService;
        this.userService = userService;
        this.telegramClient = telegramClient;
        this.botUsername = botUsername;
    }



    @Override
    @Transactional
    public void handler(ChatMemberUpdated chatMemberUpdated) {
        var fromChatId = chatMemberUpdated.getFrom().getId();
        var fromUsername = chatMemberUpdated.getFrom().getUserName();
        var fromFirstName = chatMemberUpdated.getFrom().getFirstName();
        var fromLastName = chatMemberUpdated.getFrom().getLastName();
        var createdAt = LocalDateTime.now();
        var groupChatId = chatMemberUpdated.getChat().getId();
        var title = chatMemberUpdated.getChat().getTitle();
        var status = chatMemberUpdated.getNewChatMember().getStatus();
        var usernameNewChatMember = Optional.ofNullable(chatMemberUpdated.getNewChatMember().getUser().getUserName())
                .orElse("");

        if (status.equals("member") && usernameNewChatMember.equals(botUsername)) {
            var user = createUser(fromChatId, fromUsername, fromFirstName, fromLastName, createdAt);
            createGroup(fromChatId, title, user, groupChatId);
        } else if (status.equals("left") && usernameNewChatMember.equals(botUsername)) {
            leftGroupBot(groupChatId);
        }
    }

    private String generateMessage(String title, Integer chars) {
        return """
                Группа %s добавлена в список.
                Бесплатно %d символов для перевода.
                Выберите языки тут: %s
                
                В меню бота можно докупить символы, выбрать другие языки перевода."""
                .formatted(title, chars, botUsername);
    }

    private void leftGroupBot(Long groupChatId) {
        var group = groupService.findByTelegramChatId(groupChatId);
        groupService.deleteLinksLanguages(group.getGroupId());
        groupService.deleteById(group.getGroupId());
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

        sendMessage(group.getChatId(), "Группа %s была удалена из списка.".formatted(group.getTitle()));
    }

    private void sendMessage(Long chatId, String message) {
        try {
            var sendMessage = SendMessage.builder()
                    .text(message)
                    .chatId(chatId)
                    .parseMode(ParseMode.HTML)
                    .build();

            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения, при добавлении в группу {}", e.toString());
        }
    }

    private void createGroup(Long fromChatId, String title, User user, Long groupChatId) {
        var languagesTarting = new ArrayList<LanguageTranslation>();

        var ru = languageService.findByCode("ru");
        var en = languageService.findByCode("en");

        languagesTarting.add(ru);
        languagesTarting.add(en);

        var newGroup = Group.builder()
                .chatId(fromChatId)
                .title(title)
                .owner(user)
                .languages(languagesTarting)
                .translatingMessages(true)
                .telegramGroupId(groupChatId)
                .limitCharacters(startingCharGroup)
                .build();

        var deletedGroup = deletedGroupService.findByTelegramGroupId(groupChatId);

        deletedGroup.ifPresent(group -> newGroup.setLimitCharacters(group.getChars()));

        groupService.save(newGroup);

        sendMessage(groupChatId, generateMessage(title, newGroup.getLimitCharacters()));
    }

    private User createUser(Long fromChatId, String fromUsername, String fromFirstName, String fromLastName, LocalDateTime createdAt) {
        var userOp = userService.findByChatId(fromChatId);
        if (userOp.isPresent()) {
            return userOp.get();
        }

        var newUser = User.builder()
                .username(fromUsername)
                .firstName(fromFirstName)
                .lastName(fromLastName)
                .chatId(fromChatId)
                .createdAt(createdAt)
                .build();

        return userService.save(newUser);
    }
}
