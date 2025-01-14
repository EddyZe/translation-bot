package ru.eddyz.translationbot.clients.google;


import com.google.cloud.translate.Translate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class GoogleTranslateClient {

    private final Translate googleTranslate;

    public String translate(String text, String code) {
        return googleTranslate.translate(text, Translate.TranslateOption.targetLanguage(code))
                .getTranslatedText();
    }

    public String translate(String text, String code, String sourceCode) {
        return googleTranslate.translate(
                        text,
                        Translate.TranslateOption.targetLanguage(code),
                        Translate.TranslateOption.sourceLanguage(sourceCode)
                )
                .getTranslatedText();
    }

    public String detect(String text) {
        return googleTranslate.detect(text)
                .getLanguage();
    }
}
