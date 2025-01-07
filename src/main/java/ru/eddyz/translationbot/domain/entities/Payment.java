package ru.eddyz.translationbot.domain.entities;


import jakarta.persistence.*;
import lombok.*;
import ru.eddyz.translationbot.domain.enums.PaymentType;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "payment")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private Long chatId;

    private Integer numberCharacters;
    private String asset;

    private Float amount;

    private LocalDateTime createdAt;

    private String telegramPaymentId;

    @Enumerated(EnumType.STRING)
    private PaymentType type;

    @ManyToOne
    @JoinColumn(name = "user_payments", referencedColumnName = "user_id")
    private User payer;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(paymentId, payment.paymentId) && Objects.equals(numberCharacters, payment.numberCharacters) && Objects.equals(amount, payment.amount) && Objects.equals(createdAt, payment.createdAt) && type == payment.type && Objects.equals(payer, payment.payer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentId, numberCharacters, amount, createdAt, type, payer);
    }
}
