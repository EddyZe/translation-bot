package ru.eddyz.translationbot.domain.enums;




public enum ButtonCheckLimit {
    NEXT_CHECK_LIMIT("Далее ⏭️"), BACK_CHECK_LIMIT("Назад ⏮️"), CLOSE_CHECK_LIMIT("Закрыть ❌");

    private final String btn;

    ButtonCheckLimit(String btn) {
        this.btn = btn;
    }

    public String toString() {
        return btn;
    }
}
