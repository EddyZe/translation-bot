package ru.eddyz.translationbot.domain.enums;




public enum ListGroupMenu {
    GROUP_LIST(""), NEXT_PAGE_GROUP("Далее ⏭️"), BACK_PAGE_GROUP("Назад ⏮️"), CLOSE_LIST_GROUP("Закрыть ❌ ");

    private final String btn;

    ListGroupMenu(String btn) {
        this.btn = btn;
    }

    public String toString() {
        return btn;
    }
}
