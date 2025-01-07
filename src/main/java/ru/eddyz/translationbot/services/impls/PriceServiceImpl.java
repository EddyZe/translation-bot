package ru.eddyz.translationbot.services.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.eddyz.translationbot.domain.entities.Price;
import ru.eddyz.translationbot.domain.enums.PaymentType;
import ru.eddyz.translationbot.repositories.PriceRepository;
import ru.eddyz.translationbot.services.PriceService;

import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class PriceServiceImpl implements PriceService {

    private final PriceRepository priceRepository;

    @Override
    public Price findById(Long id) {
        return priceRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Цена с таким ID не найдена"));
    }

    @Override
    public void save(Price price) {
        priceRepository.save(price);
    }

    @Override
    public Page<Price> findByType(PaymentType paymentType, Pageable pageable) {
        return priceRepository.findByType(paymentType, pageable);
    }
}
