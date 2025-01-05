package ru.eddyz.translationbot.services;


import ru.eddyz.translationbot.domain.entities.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findByChatId(Long chatId);

    void save(User newUser);
}
