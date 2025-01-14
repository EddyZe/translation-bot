package ru.eddyz.translationbot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.eddyz.translationbot.clients.proxyapi.ProxyApiOpenAiClient;
import ru.eddyz.translationbot.clients.proxyapi.payloads.MessageOpenAi;
import ru.eddyz.translationbot.clients.proxyapi.payloads.ModelOpenAi;
import ru.eddyz.translationbot.clients.proxyapi.payloads.RequestOpenAi;
import ru.eddyz.translationbot.clients.proxyapi.payloads.ResponseOpenAi;
import ru.eddyz.translationbot.clients.yandex.YandexTranslateClient;
import ru.eddyz.translationbot.clients.yandex.payloads.ResponseDetect;
import ru.eddyz.translationbot.clients.yandex.payloads.ResponseTranslate;

import java.util.List;

@SpringBootTest
class TranslationBotApplicationTests {

    @Autowired
    YandexTranslateClient yandexTranslateClient;

    @Autowired
    ProxyApiOpenAiClient proxyApiOpenAiClient;

    @Test
    void checkYandexTranslateClientV2() {

        ResponseTranslate translate = yandexTranslateClient.translate("angel", "ru");
        Assertions.assertEquals(translate.getTranslations().getFirst().getText(), "ангел");

        translate = yandexTranslateClient.translate("angel", "ru", "de");
        Assertions.assertEquals("удочка", translate.getTranslations().getFirst().getText());

        ResponseDetect detect = yandexTranslateClient.detect("hello world");
        Assertions.assertEquals("en", detect.getLanguageCode());

    }

    @Test
    void checkOpenAiApi() {

        MessageOpenAi promt = proxyApiOpenAiClient.promtDetectLanguage();

        ResponseOpenAi ai = proxyApiOpenAiClient.sendPromt(RequestOpenAi
                .builder()
                .model(ModelOpenAi.GPT_4o_MINI.toString())
                .messages(List.of(promt, MessageOpenAi.builder()
                        .role("user")
                        .content("hallo")
                        .build()))
                .build());


        System.out.println(ai.getId());
        System.out.println(ai.getModel());
        ai.getChoices()
                .forEach(choiceOpenAi -> {
                    System.out.println(choiceOpenAi.getIndex());
                    System.out.println(choiceOpenAi.getMessage().getContent());
                });

        MessageOpenAi translatePromt = proxyApiOpenAiClient.promtTranslateText("Russia");

        MessageOpenAi text = MessageOpenAi.builder()
                .role("user")
                .content("ในความคิดของฉันไม่มีคำสั่งถูกนำมาในห้องเล่นเกม")
                .build();

        ResponseOpenAi ai1 = proxyApiOpenAiClient.sendPromt(RequestOpenAi.builder()
                        .model(ModelOpenAi.GPT_4o_MINI.toString())
                        .messages(List.of(translatePromt, text))
                .build());


        ai1.getChoices().forEach(choiceOpenAi -> System.out.println(choiceOpenAi.getMessage().getContent()));
    }
}
