package ru.eddyz.translationbot.translaters;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.eddyz.translationbot.clients.yandex.YandexTranslateClient;
import ru.eddyz.translationbot.translaters.enums.TranslatorService;
import ru.eddyz.translationbot.translaters.impls.GoogleTranslator;
import ru.eddyz.translationbot.translaters.impls.OpenAiTranslator;
import ru.eddyz.translationbot.translaters.impls.YandexTranslator;

import java.util.NoSuchElementException;


@Component
@RequiredArgsConstructor
@Slf4j
public class TranslatorFactory {

    private final YandexTranslator yandexTranslator;
    private final OpenAiTranslator openAiTranslator;
    private final GoogleTranslator googleTranslator;


    public Translator getTranslator(TranslatorService translator) {
        switch (translator) {
            case YANDEX -> {
                return yandexTranslator;
            }
            case OPEN_AI -> {
                return openAiTranslator;
            }
            case GOOGLE -> {
                return googleTranslator;
            }
            default -> throw new NoSuchElementException("Такой переводчик не найден.");
        }
    }

}
