package ru.eddyz.translationbot.clients.yandex;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.eddyz.translationbot.clients.yandex.payloads.RequestDetect;
import ru.eddyz.translationbot.clients.yandex.payloads.ResponseDetect;
import ru.eddyz.translationbot.clients.yandex.payloads.ResponseTranslate;
import ru.eddyz.translationbot.clients.yandex.payloads.RequestTranslate;
import ru.eddyz.translationbot.domain.models.YandexTranslateData;

import java.util.List;


@Component
public class YandexTranslateClient {

    private final RestClient restClient;
    private final YandexTranslateData yandexTranslateData;
    public YandexTranslateClient(YandexTranslateData yandexTranslateData) {
        this.yandexTranslateData = yandexTranslateData;
        restClient = buildRestClient();
    }

    public ResponseTranslate translate(String text, String code) {
        return restClient
                .post()
                .uri("/translate")
                .body(RequestTranslate
                        .builder()
                        .folderId(yandexTranslateData.getFolderId())
                        .texts(List.of(text))
                        .targetLanguageCode(code)
                        .build())
                .retrieve()
                .body(ResponseTranslate.class);
    }

    public ResponseTranslate translate(String text, String code, String sourceCode) {
        return restClient
                .post()
                .uri("/translate")
                .body(RequestTranslate
                        .builder()
                        .folderId(yandexTranslateData.getFolderId())
                        .texts(List.of(text))
                        .sourceLanguageCode(sourceCode)
                        .targetLanguageCode(code)
                        .build())
                .retrieve()
                .body(ResponseTranslate.class);
    }

    public ResponseDetect detect(String text) {
        return restClient
                .post()
                .uri("/detect")
                .body(RequestDetect.builder()
                        .text(text)
                        .folderId(yandexTranslateData.getFolderId())
                        .build())
                .retrieve()
                .body(ResponseDetect.class);
    }

    private RestClient buildRestClient() {
        return RestClient.builder()
                .baseUrl(yandexTranslateData.getBaseUrl())
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    httpHeaders.add("Authorization", "Api-Key " + yandexTranslateData.getToken());
                    httpHeaders.add("x-folder-id", yandexTranslateData.getFolderId());
                })
                .build();
    }
}
