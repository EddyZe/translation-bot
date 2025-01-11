package ru.eddyz.translationbot.domain.enums;





public enum ButtonSettingGroup {
    BUY_CHARACTERS("Купить символы 💳"),
    SELECT_LANGUAGE("Выбрать языки"),
    TRANSLATION_GROUP("Перевод: %s"),
    DELETE_GROUP("Удалить группу ❌"),
    BACK_TO_LIST_GROUP("Вернуться к списку ⏮️");

    private final String btn;

    ButtonSettingGroup(String btn) {
        this.btn = btn;
    }

    public String toString() {
        return btn;
    }
}
