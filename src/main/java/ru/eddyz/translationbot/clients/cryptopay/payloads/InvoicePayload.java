package ru.eddyz.translationbot.clients.cryptopay.payloads;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class InvoicePayload {

    @JsonProperty("invoice_id")
    private Long invoiceId;

    private String hash;

    @JsonProperty("currency_type")
    private String currencyType;

    private String asset;

    private String fiat;

    private String amount;

    @JsonProperty("paid_amount")
    private String paidAmount;

    @JsonProperty("paid_fiat_rate")
    private String paidFiatRate;

    @JsonProperty("accepted_assets")
    private List<String> acceptedAssets;

    @JsonProperty("fee_asset")
    private String feeAsset;

    @JsonProperty("fee_amount")
    private String feeAmount;

    private String fee;

    @JsonProperty("pay_url")
    private String payUrl;

    @JsonProperty("bot_invoice_url")
    private String botInvoiceUrl;

    @JsonProperty("mini_app_invoice_url")
    private String miniAppInvoiceUrl;

    @JsonProperty("web_app_invoice_url")
    private String webAppInvoiceUrl;

    @JsonProperty("description")
    private String description;

    @JsonProperty("status")
    private String status;

    @JsonProperty("created_at")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;

    @JsonProperty("paid_usd_rate")
    private String paidUsdRate;

    @JsonProperty("allow_comments")
    private Boolean allowComments;

    @JsonProperty("allow_anonymous")
    private Boolean allowAnonymous;

    @JsonProperty("expiration_date")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime expirationDate;

    @JsonProperty("paid_at")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime paidAt;

    @JsonProperty("paid_anonymously")
    private Boolean paidAnonymously;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("hidden_message")
    private String hiddenMessage;

    @JsonProperty("payload")
    private String payload;

    @JsonProperty("paid_btn_name")
    private String paidBtnName;

    @JsonProperty("paid_btn_url")
    private String paidBtnUrl;
}
