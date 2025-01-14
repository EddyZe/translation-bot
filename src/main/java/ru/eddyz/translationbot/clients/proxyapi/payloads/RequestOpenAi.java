package ru.eddyz.translationbot.clients.proxyapi.payloads;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestOpenAi {

    private String model;
    private List<MessageOpenAi> messages;
}
