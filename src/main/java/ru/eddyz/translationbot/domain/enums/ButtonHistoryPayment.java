package ru.eddyz.translationbot.domain.enums;

public enum ButtonHistoryPayment {
    NEXT_PAGE_HISTORY("Далее ⏭️"), BACK_PAGE_HISTORY("Назад ⏮️"), CLOSE_HISTORY("Закрыть ❌");

    private final String btn;

    ButtonHistoryPayment(String btn) {
        this.btn = btn;
    }

    @Override
    public String toString() {
        return btn;
    }
}
