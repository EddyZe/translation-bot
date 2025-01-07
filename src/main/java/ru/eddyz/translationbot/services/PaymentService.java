package ru.eddyz.translationbot.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.eddyz.translationbot.domain.entities.Payment;

public interface PaymentService {

    void save(Payment payment);

    Page<Payment> findByChatId(Long chatId, Pageable pageable);
}
