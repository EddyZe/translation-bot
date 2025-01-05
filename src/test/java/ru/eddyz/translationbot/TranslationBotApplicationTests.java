package ru.eddyz.translationbot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.eddyz.translationbot.clients.yandex.YandexTranslateClient;
import ru.eddyz.translationbot.clients.yandex.payloads.ResponseDetect;
import ru.eddyz.translationbot.clients.yandex.payloads.ResponseTranslate;

@SpringBootTest
class TranslationBotApplicationTests {

    @Autowired
    YandexTranslateClient yandexTranslateClient;
    @Test
    void checkYandexTranslateClientV2() {

        ResponseTranslate translate = yandexTranslateClient.translate("angel", "ru");
        Assertions.assertEquals(translate.getTranslations().getFirst().getText(), "ангел");

        translate = yandexTranslateClient.translate("angel", "ru", "de");
        Assertions.assertEquals("удочка", translate.getTranslations().getFirst().getText());

        ResponseDetect detect = yandexTranslateClient.detect("hello world");
        Assertions.assertEquals("en", detect.getLanguageCode());

    }

}
