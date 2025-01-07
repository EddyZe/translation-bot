package ru.eddyz.translationbot.clients.cryptopay.payloads;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateInvoiceRequest {

    @JsonProperty("currency_type")
    private String currencyType;

    private String asset;

    private String fiat;

    @JsonProperty("accepted_asserts")
    private String acceptedAsserts;

    private Float amount;

    private String description;

    @JsonProperty("hidden_message")
    private String hiddenMessage;

    @JsonProperty("paid_btn_name")
    private String paidBtnName;

    @JsonProperty("paid_btn_url")
    private String paidBtnUrl;

    @JsonProperty("payload")
    private String payload;

    @JsonProperty("allow_comments")
    private Boolean allowComments;

    @JsonProperty("allow_anonymous")
    private Boolean allowAnonymous;

    @JsonProperty("expires_in")
    private Long expiresIn;

}
