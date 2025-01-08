package ru.eddyz.translationbot.services.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.eddyz.translationbot.domain.entities.TranslationMessage;
import ru.eddyz.translationbot.repositories.TranslationMessageRepository;
import ru.eddyz.translationbot.services.TranslationMessagesService;



@Service
@RequiredArgsConstructor
@Slf4j
public class TranslationMessagesServiceImpl implements TranslationMessagesService {

    private final TranslationMessageRepository translationMessageRepository;

    @Override
    public void save(TranslationMessage translationMessage) {
        translationMessageRepository.save(translationMessage);
    }
}
