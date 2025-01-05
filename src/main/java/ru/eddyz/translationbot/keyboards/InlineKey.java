package ru.eddyz.translationbot.keyboards;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.eddyz.translationbot.domain.entities.Group;
import ru.eddyz.translationbot.domain.enums.ListGroupMenu;
import ru.eddyz.translationbot.domain.enums.SettingGroupBtn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InlineKey {

    public static InlineKeyboardMarkup listGroupButton(List<Group> groups, boolean visNext, boolean visBack) {
        List<InlineKeyboardRow> groupsBtn = new ArrayList<>();

        groups.forEach(group -> groupsBtn.add(new InlineKeyboardRow(
                InlineKeyboardButton
                        .builder()
                        .text(group.getTitle())
                        .callbackData(ListGroupMenu.GROUP_LIST.name() + ":" + group.getGroupId())
                        .build()
        )));

        var nextAndBackRow = new InlineKeyboardRow();

        if (visBack) {
            var backBtn = InlineKeyboardButton.builder()
                    .text(ListGroupMenu.BACK_PAGE_GROUP.toString())
                    .callbackData(ListGroupMenu.BACK_PAGE_GROUP.name())
                    .build();
            nextAndBackRow.add(backBtn);
        }

        if (visNext) {
            var nextBtn = InlineKeyboardButton
                    .builder()
                    .text(ListGroupMenu.NEXT_PAGE_GROUP.toString())
                    .callbackData(ListGroupMenu.NEXT_PAGE_GROUP.name())
                    .build();
            nextAndBackRow.add(nextBtn);
        }
        var closeBtn = InlineKeyboardButton.builder()
                .text(ListGroupMenu.CLOSE_LIST_GROUP.toString())
                .callbackData(ListGroupMenu.CLOSE_LIST_GROUP.name())
                .build();

        groupsBtn.add(nextAndBackRow);
        groupsBtn.add(new InlineKeyboardRow(closeBtn));

        return InlineKeyboardMarkup.builder()
                .keyboard(groupsBtn)
                .build();
    }

    public static InlineKeyboardMarkup groupSetting(Long groupId) {
        List<InlineKeyboardRow> rows = Arrays.stream(SettingGroupBtn.values())
                        .map(b -> {
                            var btn = InlineKeyboardButton.builder()
                                    .text(b.toString())
                                    .callbackData(b.name() + ":" + groupId)
                                    .build();
                            return new InlineKeyboardRow(btn);
                        })
                        .toList();

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }
}
