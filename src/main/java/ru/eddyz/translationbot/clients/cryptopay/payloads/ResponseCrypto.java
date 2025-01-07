package ru.eddyz.translationbot.clients.cryptopay.payloads;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseCrypto {

    private Boolean ok;
    private InvoicePayload result;

}
