package ru.eddyz.translationbot.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.eddyz.translationbot.domain.entities.Price;
import ru.eddyz.translationbot.domain.enums.PaymentType;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {

    Page<Price> findByType(PaymentType type, Pageable pageable);
}
