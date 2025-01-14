package ru.eddyz.translationbot.translaters.enums;




public enum TranslatorService {
    OPEN_AI("OpenAI"), YANDEX("Yandex"), GOOGLE("Google");

    private final String title;

    TranslatorService(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
