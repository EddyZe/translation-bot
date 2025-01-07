package ru.eddyz.translationbot.domain.enums;

public enum ButtonPriceList {
    PRICE_LIST(""),
    NEXT_PAGE_PRICE("Далее ⏭️"),
    BACK_PAGE_PRICE("Назад ⏮️"),
    BACK_SELECTED_PRICE("Вернуться к выбору оплаты ⏮️");

    private final String btn;

    ButtonPriceList(String btn) {
        this.btn = btn;
    }

    @Override
    public String toString() {
        return btn;
    }
}
