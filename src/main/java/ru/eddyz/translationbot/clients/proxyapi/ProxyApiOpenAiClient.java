package ru.eddyz.translationbot.clients.proxyapi;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.eddyz.translationbot.clients.proxyapi.payloads.MessageOpenAi;
import ru.eddyz.translationbot.clients.proxyapi.payloads.RequestOpenAi;
import ru.eddyz.translationbot.clients.proxyapi.payloads.ResponseOpenAi;

@Component
@Slf4j
public class ProxyApiOpenAiClient {



    private final String baseUrl;

    private final String token;

    private final RestClient restClient;

    private final String PATH_CHAT = "/v1/chat/completions";

    public ProxyApiOpenAiClient(@Value("${proxyapi.openai.base_url}") String baseUrl,
                                @Value("${proxyapi.openai.token}") String token) {
        this.baseUrl = baseUrl;
        this.token = token;
        this.restClient = buildRestClient();
    }

    public ResponseOpenAi sendPromt(RequestOpenAi request) {
        return restClient
                .post()
                .uri(PATH_CHAT)
                .body(request)
                .retrieve()
                .body(ResponseOpenAi.class);
    }

    public MessageOpenAi promtDetectLanguage() {
        return MessageOpenAi.builder()
                .role("assistant")
                .content("You are assistant, why recognize the language writing text. You should response in as a key:value, the key is the language code, but value - title. For example: ru:Russia, en:English")
                .build();
    }

    public MessageOpenAi promtTranslateText(String language) {
        return MessageOpenAi.builder()
                .role("assistant")
                .content("Yoy are translator. Translate the received text on the %s. Result send without explanations. All you need is a translation.".formatted(language))
                .build();
    }

    private RestClient buildRestClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                })
                .build();
    }
}
