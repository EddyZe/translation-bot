package ru.eddyz.translationbot.clients.cryptopay;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.eddyz.translationbot.clients.cryptopay.payloads.CreateInvoiceRequest;
import ru.eddyz.translationbot.clients.cryptopay.payloads.ResponseCrypto;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class CryptoPayClient {

    private final RestClient restClient;
    private final String urlBot;

    public CryptoPayClient(@Value("${crypto.pay.token}") String token,
                           @Value("${crypto.pay.base-url}") String baseUrl,
                           @Value("${telegram.bot.bot-url}") String urlBot) {
        this.urlBot = urlBot;

        restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(httpHeaders ->
                        httpHeaders.add("Crypto-Pay-API-Token", token))
                .build();
    }


    public ResponseCrypto createInvoice(Float amount, String asset, String description, String payload) {
        var data = CreateInvoiceRequest
                .builder()
                .amount(amount)
                .paidBtnName("callback")
                .paidBtnUrl(urlBot)
                .currencyType("crypto")
                .asset(asset)
                .description(description)
                .expiresIn(Duration.ofMinutes(10).toSeconds())
                .payload(payload)
                .allowComments(false)
                .allowAnonymous(true)
                .build();

        return restClient
                .post()
                .uri("/createInvoice")
                .body(data)
                .contentType(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .retrieve()
                .body(ResponseCrypto.class);
    }
}
