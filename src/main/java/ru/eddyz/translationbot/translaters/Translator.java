package ru.eddyz.translationbot.translaters;


import kotlin.Pair;
import ru.eddyz.translationbot.translaters.enums.TranslatorService;

public interface Translator {

    String detect(String text);

    String translate(String text, Pair<String, String> codeAndTitleLanguage);

    TranslatorService translatorType();

}
