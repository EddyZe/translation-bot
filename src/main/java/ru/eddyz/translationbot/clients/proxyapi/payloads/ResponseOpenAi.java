package ru.eddyz.translationbot.clients.proxyapi.payloads;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseOpenAi {

    private List<ChoiceOpenAi> choices;
    private Instant created;
    private String id;
    private String model;

}
