package ru.eddyz.translationbot.clients.proxyapi.payloads;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChoiceOpenAi {

    private Integer index;
    private MessageOpenAi message;
}
