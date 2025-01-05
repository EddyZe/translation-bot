package ru.eddyz.translationbot.domain.enums;





public enum SettingGroupBtn {
    BUY_CHARACTERS("Купить символы 💳"), SELECT_LANGUAGE("Выбрать языки"), DELETE_GROUP("Удалить группу ❌"), BACK_TO_LIST_GROUP("Вернуться к списку ⏮️");

    private final String btn;

    SettingGroupBtn(String btn) {
        this.btn = btn;
    }

    public String toString() {
        return btn;
    }
}
