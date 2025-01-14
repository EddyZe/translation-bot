package ru.eddyz.translationbot.translaters.impls;

import kotlin.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.eddyz.translationbot.clients.yandex.YandexTranslateClient;
import ru.eddyz.translationbot.translaters.Translator;
import ru.eddyz.translationbot.translaters.enums.TranslatorService;


@Slf4j
@RequiredArgsConstructor
@Component
public class YandexTranslator implements Translator {

    private final YandexTranslateClient yandexTranslateClient;


    @Override
    public String detect(String text) {
        return yandexTranslateClient.detect(text)
                .getLanguageCode();
    }

    @Override
    public String translate(String text, Pair<String, String> language) {
        return yandexTranslateClient
                .translate(text, language.getFirst())
                .getTranslations()
                .getFirst()
                .getText();
    }

    @Override
    public TranslatorService translatorType() {
        return TranslatorService.YANDEX;
    }

    public String translate(String text, String code, String sourceCode) {
        return yandexTranslateClient
                .translate(text, code, sourceCode)
                .getTranslations()
                .getFirst()
                .getText();
    }
}
