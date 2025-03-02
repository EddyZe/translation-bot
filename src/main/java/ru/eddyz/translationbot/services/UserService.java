package ru.eddyz.translationbot.services;


import ru.eddyz.translationbot.domain.entities.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findByChatId(Long chatId);

    User save(User newUser);

    User findByUsername(String username);
}
