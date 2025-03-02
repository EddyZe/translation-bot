package ru.eddyz.translationbot.handlers.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.eddyz.translationbot.commands.*;
import ru.eddyz.translationbot.domain.enums.MainMenuButton;
import ru.eddyz.translationbot.domain.enums.UserStates;
import ru.eddyz.translationbot.handlers.MessageHandler;
import ru.eddyz.translationbot.handlers.SuccessfulPaymentHandler;
import ru.eddyz.translationbot.util.UserState;

import java.util.Optional;


@Component
@RequiredArgsConstructor
public class MessageHandlerImpl implements MessageHandler {

    private final Start start;
    private final ListGroup listGroup;
    private final AddGroup addGroup;
    private final CheckLimit checkLimit;
    private final SuccessfulPaymentHandler successfulPaymentHandler;
    private final HistoryPayments historyPayments;
    private final TranslationGroupMessage translationGroupMessage;
    private final GetChatId getChatId;

    @Override
    public void handle(Message message) {
        if (message.hasText() && message.isUserMessage()) {
            var text = message.getText();
            if (text.equals("/start"))
                start.execute(message);
            else if (text.equals("/check_limit")) {
                checkLimit.execute(message);
            } else if (text.equals(MainMenuButton.MY_GROUPS.toString()) || text.equals("/groups")) {
                listGroup.execute(message);
            } else if (text.equals(MainMenuButton.ADD_GROUP.toString()) || text.equals("/addgroup")) {
                UserState.setUserState(message.getChatId(), MainMenuButton.ADD_GROUP);
                addGroup.execute(message);
            } else if (text.equals(MainMenuButton.MY_HISTORY_PAYMENTS.toString()) || text.equals("/payments")) {
                historyPayments.execute(message);
            } else if (text.equals(MainMenuButton.ID_GROUP.toString())) {
                getChatId.execute(message);
            } else {
                var currentState = UserState.getUserState(message.getChatId());
                if (currentState.isPresent()) {

                    if (currentState.get() == UserStates.ADD_GROUP_SET_ID ||
                        currentState.get() == UserStates.ADD_GROUP_SET_NAME) {
                        addGroup.execute(message);
                    }

                }
            }
        } else if (message.hasSuccessfulPayment()) {
            successfulPaymentHandler.handle(message.getSuccessfulPayment());
        } else if (message.isGroupMessage() || message.isSuperGroupMessage()) {
            var text = Optional.ofNullable(message.getText()).orElse("");

            if (text.startsWith("/translate")) {
                addGroup.execute(message);
            } else if (!text.isEmpty()){
                translationGroupMessage.execute(message);
            }
        }
    }
}
