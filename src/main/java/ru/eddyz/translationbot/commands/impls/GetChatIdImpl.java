package ru.eddyz.translationbot.commands.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.translationbot.commands.GetChatId;




@Component
@RequiredArgsConstructor
@Slf4j
public class GetChatIdImpl implements GetChatId {

    private final TelegramClient telegramClient;


    public void execute(Message message) {
        var chatId = message.getChatId();
        var text = message.getText();

        if (text.equals("/getid")) {
            sendMessage(message.getFrom().getId(), "ID вашей группы: ");
            sendMessage(message.getFrom().getId(), chatId.toString());
            deleteMessage(chatId, message.getMessageId());
        } else {
            sendMessage(chatId, generateMessage());
        }
    }

    private void sendMessage(Long chatId, String message) {
        try {
            var sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text(message)
                    .parseMode(ParseMode.HTML)
                    .build();

            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения в команде получения ID группы: {}", e.toString());
        }

    }

    private void deleteMessage(Long chatId, Integer messageId) {
        try {
            var deleteMessage = DeleteMessage.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .build();

            telegramClient.execute(deleteMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при удалении сообщения из группы: {}", e.toString());
        }
    }

    private String generateMessage() {
        return """
                Чтобы узнать ID группы, вы можете добавить меня в группу и написать в ней команду /getid
                
                Так же вы можете воспользоваться веб версией.
                Чтобы узнать ID с помощью веб версии телеграм, откройте вашу группу и в ссылке скопируйте цифры которые идут после #.
                Например: "https://web.telegram.org/a/#-10012345678" тут ID будет "-10012345678"
                """;
    }
}
