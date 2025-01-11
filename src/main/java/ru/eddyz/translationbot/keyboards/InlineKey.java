package ru.eddyz.translationbot.keyboards;

import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.eddyz.translationbot.domain.entities.Group;
import ru.eddyz.translationbot.domain.entities.LanguageTranslation;
import ru.eddyz.translationbot.domain.entities.Price;
import ru.eddyz.translationbot.domain.enums.*;

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

        var nextAndBackRow = generateNextAndBackRow(visNext,
                visBack,
                ListGroupMenu.NEXT_PAGE_GROUP,
                ListGroupMenu.BACK_PAGE_GROUP);

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

    public static InlineKeyboardMarkup checkLimits(boolean visNext, boolean visBack) {
        var rows = new ArrayList<InlineKeyboardRow>();

        var nextAndBackButton = generateNextAndBackRow(visNext, visBack,
                ButtonCheckLimit.NEXT_CHECK_LIMIT, ButtonCheckLimit.BACK_CHECK_LIMIT);

        var close = InlineKeyboardButton.builder()
                .text(ButtonCheckLimit.CLOSE_CHECK_LIMIT.toString())
                .callbackData(ButtonCheckLimit.CLOSE_CHECK_LIMIT.name())
                .build();

        rows.add(nextAndBackButton);
        rows.add(new InlineKeyboardRow(close));

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }

    public static InlineKeyboardMarkup historyPayment(boolean visNext, boolean visBack) {
        var rows = new ArrayList<InlineKeyboardRow>();

        var nextAndBackButton = generateNextAndBackRow(visNext, visBack,
                ButtonHistoryPayment.NEXT_PAGE_HISTORY, ButtonHistoryPayment.BACK_PAGE_HISTORY);

        var close = InlineKeyboardButton.builder()
                .text(ButtonHistoryPayment.CLOSE_HISTORY.toString())
                .callbackData(ButtonHistoryPayment.CLOSE_HISTORY.name())
                .build();

        rows.add(nextAndBackButton);
        rows.add(new InlineKeyboardRow(close));

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }

    public static InlineKeyboardMarkup groupSetting(Group group) {
        List<InlineKeyboardRow> rows = Arrays.stream(ButtonSettingGroup.values())
                .map(b -> {
                    var text = b.toString();
                    if (b == ButtonSettingGroup.TRANSLATION_GROUP) {
                        var emoj = group.getTranslatingMessages() ? "Вкл ✅" : "Выкл ";
                        text = b.toString().formatted(emoj);
                    }

                    var btn = InlineKeyboardButton.builder()
                            .text(text)
                            .callbackData(b.name() + ":" + group.getGroupId())
                            .build();
                    return new InlineKeyboardRow(btn);
                })
                .toList();

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }

    public static InlineKeyboardMarkup selectLanguage(List<LanguageTranslation> languages,
                                                      Group group,
                                                      boolean visNext,
                                                      boolean visBack) {
        var rows = new ArrayList<InlineKeyboardRow>();
        var nextAndBackRow = new InlineKeyboardRow();

        languages.forEach(l -> {
            var text = l.getTitle();
            if (group.getLanguages().contains(l))
                text = l.getTitle() + " ✅";

            var btn = InlineKeyboardButton.builder()
                    .text(text)
                    .callbackData(ButtonLanguageList.BUTTON_LANGUAGE_LIST.name() + ":" + l.getLanguageId() + ":" + group.getGroupId())
                    .build();

            rows.add(new InlineKeyboardRow(btn));
        });

        if (visBack) {
            var back = InlineKeyboardButton.builder()
                    .text(ButtonLanguageList.BACK_PAGE_LANGUAGE.toString())
                    .callbackData(ButtonLanguageList.BACK_PAGE_LANGUAGE.name() + ":" + group.getGroupId())
                    .build();

            nextAndBackRow.add(back);
        }

        if (visNext) {
            var next = InlineKeyboardButton.builder()
                    .text(ButtonLanguageList.NEXT_PAGE_LANGUAGE.toString())
                    .callbackData(ButtonLanguageList.NEXT_PAGE_LANGUAGE.name() + ":" + group.getGroupId())
                    .build();

            nextAndBackRow.add(next);
        }

        var close = InlineKeyboardButton.builder()
                .text(ButtonLanguageList.BACK_SETTING_FROM_LANGUAGE_LIST.toString())
                .callbackData(ButtonLanguageList.BACK_SETTING_FROM_LANGUAGE_LIST.name() + ":" + group.getGroupId())
                .build();

        rows.add(nextAndBackRow);
        rows.add(new InlineKeyboardRow(close));

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }

    public static InlineKeyboardMarkup selectPaymentMode(Long groupId) {

        var telegramStars = InlineKeyboardButton.builder()
                .text(ButtonSelectPaymentType.TELEGRAM_STARS_BTN.toString())
                .callbackData(ButtonSelectPaymentType.TELEGRAM_STARS_BTN.name() + ":" + groupId)
                .build();

        var cryptoPay = InlineKeyboardButton.builder()
                .text(ButtonSelectPaymentType.CRYPTO_PAY_BTN.toString())
                .callbackData(ButtonSelectPaymentType.CRYPTO_PAY_BTN.name() + ":" + groupId)
                .build();

        var goBack = InlineKeyboardButton.builder()
                .text(ButtonSelectPaymentType.BACK_SETTING_GROUP.toString())
                .callbackData(ButtonSelectPaymentType.BACK_SETTING_GROUP.name() + ":" + groupId)
                .build();

        var rows = new ArrayList<>(
                List.of(new InlineKeyboardRow(telegramStars),
                        new InlineKeyboardRow(cryptoPay),
                        new InlineKeyboardRow(goBack))
        );

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }

    public static InlineKeyboardMarkup prices(List<Price> prices, Long groupId, PaymentType type, boolean visNext, boolean visBack) {
        var rows = new ArrayList<InlineKeyboardRow>();

        prices.forEach(price -> {
            boolean integerNumber = price.getPrice() == price.getPrice().intValue();
            var btn = InlineKeyboardButton.builder()
                    .text("%s символов %s %s"
                            .formatted(
                                    price.getNumberCharacters(),
                                    integerNumber ? String.valueOf(price.getPrice().intValue()) : price.getPrice().toString(),
                                    price.getAsset()))
                    .callbackData(ButtonPriceList.PRICE_LIST.name() + ":" + price.getPriceId() + ":" + groupId)
                    .build();

            rows.add(new InlineKeyboardRow(btn));
        });

        var nextAndBackRow = new InlineKeyboardRow();

        if (visNext) {
            var back = InlineKeyboardButton.builder()
                    .text(ButtonPriceList.NEXT_PAGE_PRICE.toString())
                    .callbackData(ButtonPriceList.NEXT_PAGE_PRICE.name() + ":" + groupId + ":" + type.name())
                    .build();
            nextAndBackRow.add(back);
        }

        if (visBack) {
            var next = InlineKeyboardButton
                    .builder()
                    .text(ButtonPriceList.BACK_PAGE_PRICE.toString())
                    .callbackData(ButtonPriceList.BACK_PAGE_PRICE.name() + ":" + groupId + ":" + type.name())
                    .build();
            nextAndBackRow.add(next);
        }

        var backSelectedPayment = InlineKeyboardButton.builder()
                .text(ButtonPriceList.BACK_SELECTED_PRICE.toString())
                .callbackData(ButtonPriceList.BACK_SELECTED_PRICE.name() + ":" + groupId)
                .build();

        rows.add(nextAndBackRow);
        rows.add(new InlineKeyboardRow(backSelectedPayment));
        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }

    @NotNull
    private static InlineKeyboardRow generateNextAndBackRow(boolean visNext, boolean visBack, Enum<?> nextBtn, Enum<?> backBtn) {
        var nextAndBackRow = new InlineKeyboardRow();

        if (visBack) {
            var back = InlineKeyboardButton.builder()
                    .text(backBtn.toString())
                    .callbackData(backBtn.name())
                    .build();
            nextAndBackRow.add(back);
        }

        if (visNext) {
            var next = InlineKeyboardButton
                    .builder()
                    .text(nextBtn.toString())
                    .callbackData(nextBtn.name())
                    .build();
            nextAndBackRow.add(next);
        }
        return nextAndBackRow;
    }
}
