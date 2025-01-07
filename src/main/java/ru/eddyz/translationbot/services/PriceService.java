package ru.eddyz.translationbot.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.eddyz.translationbot.domain.entities.Price;
import ru.eddyz.translationbot.domain.enums.PaymentType;

public interface PriceService {

    Price findById(Long id);

    void save(Price price);

    Page<Price> findByType(PaymentType paymentType, Pageable pageable);
}
