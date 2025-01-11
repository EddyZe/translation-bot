package ru.eddyz.translationbot.commands.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.translationbot.commands.Start;
import ru.eddyz.translationbot.domain.entities.User;
import ru.eddyz.translationbot.keyboards.ReplyKey;
import ru.eddyz.translationbot.services.UserService;

import java.time.LocalDateTime;
import java.util.Optional;


@Component
@RequiredArgsConstructor
@Slf4j
public class StartImpl implements Start {

    private final UserService userService;
    private final TelegramClient telegramClient;


    @Override
    public void execute(Message message) {
        var chatId = message.getChatId();
        var username = Optional.ofNullable(message.getChat().getUserName()).orElse("unknown");
        var firstName = Optional.ofNullable(message.getChat().getFirstName()).orElse("unknown");
        var lastName = Optional.ofNullable(message.getChat().getLastName()).orElse("unknown");
        var createdAt = LocalDateTime.now();

        var userOp = userService.findByChatId(chatId);

        sendHelloMessage(chatId, firstName);

        if (userOp.isPresent()) {
            return;
        }

        var newUser = User.builder()
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .chatId(chatId)
                .createdAt(createdAt)
                .build();

        userService.save(newUser);
    }

    private void sendHelloMessage(Long chatId, String name) {
        try {
            var message = SendMessage.builder()
                    .text(generateMessage(name))
                    .chatId(chatId)
                    .replyMarkup(ReplyKey.mainMenu())
                    .build();

            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке приветствия");
        }
    }

    private String generateMessage(String name) {
        return """
                %s, добро пожаловать. С помощью меня вы сможете переводить сообщения в ваших группах.
               
                """.formatted(name);

    }
}
