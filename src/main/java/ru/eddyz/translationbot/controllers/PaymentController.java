package ru.eddyz.translationbot.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.eddyz.translationbot.clients.cryptopay.payloads.UpdateCrypto;
import ru.eddyz.translationbot.domain.payloads.PaymentPayload;
import ru.eddyz.translationbot.services.SuccessfulPaymentService;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("payment")
@Slf4j
public class PaymentController {


    private final String cryptoPayToken;
    private final SuccessfulPaymentService successfulPaymentService;
    private final ObjectMapper objectMapper;
    public PaymentController(@Value("${crypto.pay.token}") String cryptoPayToken,
                             SuccessfulPaymentService successfulPaymentService, ObjectMapper objectMapper) {
        this.cryptoPayToken = cryptoPayToken;
        this.successfulPaymentService = successfulPaymentService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    @PostMapping("crypto-pay/buy-chars/{token}")
    public ResponseEntity<?> paymentWebhook(@PathVariable String token, @RequestBody UpdateCrypto updateCrypto) {
        if (!token.equals(cryptoPayToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Token invalid"));
        } else {
            log.info("Обработка updateId: {}", updateCrypto.getUpdateId());
            try {

                if (updateCrypto.getPayload().getStatus().equals("paid")) {
                    var paymentPayload = objectMapper.readValue(updateCrypto.getPayload().getPayload(), PaymentPayload.class);
                    successfulPaymentService.successfulPaymentChars(paymentPayload, null);
                    return ResponseEntity.ok(HttpStatus.OK);
                }

            } catch (JsonProcessingException e) {
                log.error("Ошибка при парсинге json в payload crypto: {}", e.toString());
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuchElementExeption(NoSuchElementException e) {
        return ResponseEntity.badRequest()
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.toString()));
    }
}
