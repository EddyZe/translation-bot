package ru.eddyz.translationbot.services;

import ru.eddyz.translationbot.domain.entities.TranslationMessage;

public interface TranslationMessagesService {


    void save(TranslationMessage translationMessage);
}
