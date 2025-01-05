package ru.eddyz.translationbot.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.eddyz.translationbot.domain.entities.Price;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {
}
