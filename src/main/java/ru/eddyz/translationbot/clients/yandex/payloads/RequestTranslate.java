package ru.eddyz.translationbot.clients.yandex.payloads;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestTranslate {

    private String folderId;
    private List<String> texts;
    private String targetLanguageCode;
    private String sourceLanguageCode;
}
