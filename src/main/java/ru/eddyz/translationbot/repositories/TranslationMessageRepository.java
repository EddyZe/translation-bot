package ru.eddyz.translationbot.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.eddyz.translationbot.domain.entities.TranslationMessage;

@Repository
public interface TranslationMessageRepository extends JpaRepository<TranslationMessage, Long> {
}
