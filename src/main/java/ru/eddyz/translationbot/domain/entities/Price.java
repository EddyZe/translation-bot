package ru.eddyz.translationbot.domain.entities;


import jakarta.persistence.*;
import lombok.*;
import ru.eddyz.translationbot.domain.enums.PaymentType;

import java.util.Objects;

@Table(name = "price")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long priceId;

    private Integer numberCharacters;

    private Float price;
    private String asset;

    @Enumerated(EnumType.STRING)
    private PaymentType type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price1 = (Price) o;
        return Objects.equals(numberCharacters, price1.numberCharacters) && Objects.equals(price, price1.price) && type == price1.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numberCharacters, price, type);
    }
}
