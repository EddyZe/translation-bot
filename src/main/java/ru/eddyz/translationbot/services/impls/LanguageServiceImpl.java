package ru.eddyz.translationbot.services.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.eddyz.translationbot.domain.entities.LanguageTranslation;
import ru.eddyz.translationbot.repositories.LanguageRepository;
import ru.eddyz.translationbot.services.LanguageService;

import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
@Slf4j
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository languageRepository;

    @Override
    public LanguageTranslation findByCode(String code) {
        return languageRepository.findByCode(code)
                .orElseThrow(() -> new NoSuchElementException("Языка с кодом %s нет в базе".formatted(code)));
    }

    @Override
    public Page<LanguageTranslation> finaAll(Pageable pageable) {
        return languageRepository.findAll(pageable);
    }
}
