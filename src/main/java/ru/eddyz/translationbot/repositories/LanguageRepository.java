package ru.eddyz.translationbot.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.eddyz.translationbot.domain.entities.LanguageTranslation;

import java.util.Optional;

@Repository
public interface LanguageRepository extends JpaRepository<LanguageTranslation, Long> {

    Optional<LanguageTranslation> findByCode(String code);


}
