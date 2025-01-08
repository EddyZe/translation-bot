package ru.eddyz.translationbot.services.impls;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.translationbot.domain.entities.Group;
import ru.eddyz.translationbot.domain.entities.Payment;
import ru.eddyz.translationbot.domain.entities.Price;
import ru.eddyz.translationbot.domain.entities.User;
import ru.eddyz.translationbot.domain.payloads.PaymentPayload;
import ru.eddyz.translationbot.services.*;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;


@Component
@Slf4j
public class SuccessfulPaymentServiceImpl implements SuccessfulPaymentService {

    private final UserService userService;
    private final GroupService groupService;
    private final PriceService priceService;
    private final PaymentService paymentService;

    private final String adminUsername;

    private final TelegramClient telegramClient;
    private final ObjectMapper objectMapper;

    public SuccessfulPaymentServiceImpl(UserService userService, GroupService groupService,
                                        PriceService priceService, PaymentService paymentService,
                                        @Value("${telegram.bot.username-admin}") String adminUsername, TelegramClient telegramClient,
                                        ObjectMapper objectMapper) {
        this.userService = userService;
        this.groupService = groupService;
        this.priceService = priceService;
        this.paymentService = paymentService;
        this.adminUsername = adminUsername;
        this.telegramClient = telegramClient;
        this.objectMapper = objectMapper;
    }


    @Override
    public void successfulPaymentChars(PaymentPayload paymentPayload, String telegramSuccessfulId) {
        var group = groupService.findById(paymentPayload.getGroupId());
        var price = priceService.findById(paymentPayload.getPriceId());
        var user = userService.findByChatId(group.getChatId());

        if (user.isEmpty())
            throw new NoSuchElementException("Пользователь с таким ChatID не найден");

        var payment = Payment.builder()
                .payer(user.get())
                .type(price.getType())
                .asset(price.getAsset())
                .chatId(group.getChatId())
                .amount(price.getPrice())
                .createdAt(LocalDateTime.now())
                .numberCharacters(price.getNumberCharacters())
                .build();

        if (telegramSuccessfulId != null)
            payment.setTelegramPaymentId(telegramSuccessfulId);

        paymentService.save(payment);

        var groupCurrentCharacters = group.getLimitCharacters();
        group.setLimitCharacters(groupCurrentCharacters + price.getNumberCharacters());
        groupService.update(group);

        sendMessage(group.getChatId(), generateMessage(payment, group));
        try {
            var admin = userService.findByUsername(adminUsername);
            sendMessage(admin.getChatId(), generateAdminMessage(user.get(), payment, group));
        } catch (NoSuchElementException e) {
            log.error(e.toString());
        }
    }

    private void sendMessage(Long chatId, String message) {
        try {
            var sendMessage = SendMessage.builder()
                    .text(message)
                    .chatId(chatId)
                    .build();

            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения в контролере с оплатой: {}", e.toString());
        }
    }

    private String generateMessage(Payment payment, Group group) {
        return """
                Вы успешно приобрели %d символов за %.2f %s.
                                
                Кол-во символов в группе %s обновлено.
                Новое кол-во символов: %d
                """.formatted(payment.getNumberCharacters(), payment.getAmount(), payment.getAsset(), group.getTitle(), group.getLimitCharacters());
    }

    private String generateAdminMessage(User user, Payment payment, Group group) {
        return """
                Пользователь %s приобрел %d символов для группы %s
                """.formatted(user.getUsername(), payment.getNumberCharacters(), group.getTitle());
    }
}
