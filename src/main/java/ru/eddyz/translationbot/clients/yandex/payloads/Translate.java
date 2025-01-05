package ru.eddyz.translationbot.clients.yandex.payloads;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Translate {
    private String text;
    private String detectedLanguageCode;
}
