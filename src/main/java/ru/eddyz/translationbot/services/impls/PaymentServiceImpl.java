package ru.eddyz.translationbot.services.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.eddyz.translationbot.domain.entities.Payment;
import ru.eddyz.translationbot.repositories.PaymentRepository;
import ru.eddyz.translationbot.services.PaymentService;



@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;


    @Override
    public void save(Payment payment) {
        paymentRepository.save(payment);
    }

    @Override
    public Page<Payment> findByChatId(Long chatId, Pageable pageable) {
        return paymentRepository.findByChatId(chatId, pageable);
    }
}
