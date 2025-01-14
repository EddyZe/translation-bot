package ru.eddyz.translationbot.translaters.impls;

import kotlin.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.eddyz.translationbot.clients.proxyapi.ProxyApiOpenAiClient;
import ru.eddyz.translationbot.clients.proxyapi.payloads.MessageOpenAi;
import ru.eddyz.translationbot.clients.proxyapi.payloads.RequestOpenAi;
import ru.eddyz.translationbot.translaters.Translator;
import ru.eddyz.translationbot.translaters.enums.TranslatorService;

import java.util.List;


@Slf4j
@Component
public class OpenAiTranslator implements Translator {

    private final String model;
    private final ProxyApiOpenAiClient proxyApiOpenAiClient;

    public OpenAiTranslator(@Value("${proxyapi.openai.model}") String model,
                            ProxyApiOpenAiClient proxyApiOpenAiClient) {
        this.model = model;
        this.proxyApiOpenAiClient = proxyApiOpenAiClient;
    }

    @Override
    public String detect(String text) {
        var detectMessage = proxyApiOpenAiClient.promtDetectLanguage();
        var messageText = buildMessageUser(text);

        var response = proxyApiOpenAiClient.sendPromt(buildRequest(List.of(detectMessage, messageText)))
                .getChoices()
                .getFirst()
                .getMessage()
                .getContent();

        return response.split(":")[0];
    }

    @Override
    public String translate(String text, Pair<String, String> language) {
        var translateMessage = proxyApiOpenAiClient.promtTranslateText(language.getSecond());
        var textMessage = buildMessageUser(text);

        return proxyApiOpenAiClient.sendPromt(buildRequest(List.of(translateMessage, textMessage)))
                .getChoices()
                .getFirst()
                .getMessage()
                .getContent();
    }

    @Override
    public TranslatorService translatorType() {
        return TranslatorService.OPEN_AI;
    }

    private RequestOpenAi buildRequest(List<MessageOpenAi> messages) {
        return RequestOpenAi
                .builder()
                .model(model)
                .messages(messages)
                .build();
    }

    private MessageOpenAi buildMessageUser(String content) {
        return MessageOpenAi.builder()
                .content(content)
                .role("user")
                .build();
    }
}
