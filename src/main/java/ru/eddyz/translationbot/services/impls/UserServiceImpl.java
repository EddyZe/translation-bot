package ru.eddyz.translationbot.services.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.eddyz.translationbot.domain.entities.User;
import ru.eddyz.translationbot.repositories.UserRepository;
import ru.eddyz.translationbot.services.UserService;

import java.util.NoSuchElementException;
import java.util.Optional;



@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findByChatId(Long chatId) {
        return userRepository.findByChatId(chatId);
    }

    @Override
    public User save(User newUser) {
        if (userRepository.findByChatId(newUser.getChatId()).isPresent())
            throw new IllegalArgumentException("User with this chatId exists");

        return userRepository.save(newUser);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с таким username не найден"));
    }
}
