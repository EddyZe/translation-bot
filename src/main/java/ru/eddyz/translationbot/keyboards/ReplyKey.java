package ru.eddyz.translationbot.keyboards;


import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.eddyz.translationbot.domain.enums.MainMenuButton;

import java.util.Arrays;
import java.util.List;

public class ReplyKey {

    public static ReplyKeyboardMarkup mainMenu() {
        var row1 = new KeyboardRow();
        var row2 = new KeyboardRow();

        row1.add(MainMenuButton.MY_GROUPS.toString());
        row1.add(MainMenuButton.MY_HISTORY_PAYMENTS.toString());
        row2.add(MainMenuButton.ADD_GROUP.toString());

        return ReplyKeyboardMarkup
                .builder()
                .keyboard(List.of(row1, row2))
                .resizeKeyboard(true)
                .build();
    }
}
