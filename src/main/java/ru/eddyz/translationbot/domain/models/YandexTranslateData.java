package ru.eddyz.translationbot.domain.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YandexTranslateData {

    private String folderId;
    private String baseUrl;
    private String token;
}
