package ru.eddyz.translationbot.services;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.eddyz.translationbot.domain.entities.LanguageTranslation;

public interface LanguageService {

    LanguageTranslation findByCode(String code);
    Page<LanguageTranslation> finaAll(Pageable pageable);
}
