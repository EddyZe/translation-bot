package ru.eddyz.translationbot.translaters.impls;

import kotlin.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.eddyz.translationbot.clients.google.GoogleTranslateClient;
import ru.eddyz.translationbot.translaters.Translator;
import ru.eddyz.translationbot.translaters.enums.TranslatorService;



@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleTranslator implements Translator {

    private final GoogleTranslateClient googleTranslateClient;

    @Override
    public String detect(String text) {
        return googleTranslateClient.detect(text);
    }

    @Override
    public String translate(String text, Pair<String, String> codeAndTitleLanguage) {
        return googleTranslateClient.translate(text, codeAndTitleLanguage.getFirst());
    }

    @Override
    public TranslatorService translatorType() {
        return TranslatorService.GOOGLE;
    }
}
