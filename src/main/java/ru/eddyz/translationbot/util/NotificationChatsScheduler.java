package ru.eddyz.translationbot.util;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.translationbot.domain.entities.Group;
import ru.eddyz.translationbot.services.GroupService;

import java.util.List;

@Service
@Slf4j
public class NotificationChatsScheduler {

    private final Integer minChars;

    private final TelegramClient telegramClient;
    private final GroupService groupService;

    public NotificationChatsScheduler(@Value("${telegram.bot.notification-chars}") Integer minChars, TelegramClient telegramClient, GroupService groupService) {
        this.minChars = minChars;
        this.telegramClient = telegramClient;
        this.groupService = groupService;
    }


    @Scheduled(cron = "* * 6 * * *")
    public void checkChars() {
        List<Group> groupMinChars = groupService.findByMinChars(minChars);

        groupMinChars.forEach(group -> {
            sendMessage(group.getChatId(), generateMessage(group));
        });
    }

    private void sendMessage(Long chatId, String message) {
        try {
            var sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text(message)
                    .parseMode(ParseMode.HTML)
                    .build();

            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e ) {
            log.error("Ошибка при отправке уведомления, что символы заканчиваются: {}", e.toString());
        }
    }

    private String generateMessage(Group group) {
        return """
                <b>‼️ Уведомление</b>
                
                В группе %s осталось %d символов.
                Не забудьте докупить символы! 
                """.formatted(group.getTitle(), group.getLimitCharacters());
    }
}
