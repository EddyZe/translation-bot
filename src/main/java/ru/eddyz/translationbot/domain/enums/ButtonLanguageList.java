package ru.eddyz.translationbot.domain.enums;





public enum ButtonLanguageList {
    BUTTON_LANGUAGE_LIST(""),
    NEXT_PAGE_LANGUAGE("Далее ⏭️"),
    BACK_PAGE_LANGUAGE("Назад ⏮️"),
    BACK_SETTING_FROM_LANGUAGE_LIST("Вернуться к группе");

    private final String btn;

    ButtonLanguageList(String btn) {
        this.btn = btn;
    }

    @Override
    public String toString() {
        return btn;
    }
}
