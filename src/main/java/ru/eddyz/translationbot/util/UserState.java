package ru.eddyz.translationbot.util;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserState {

    private static final Map<Long, Enum<?>> userState = new ConcurrentHashMap<>();

    public static void setUserState(Long chatId, Enum<?> state) {
        userState.put(chatId, state);
    }

    public static Optional<Enum<?>> getUserState(Long chatId) {
        if (userState.containsKey(chatId))
            return Optional.of(userState.get(chatId));

        return Optional.empty();
    }

    public static void clearUserState(Long chatId) {
        userState.remove(chatId);
    }
    public static void clearAll(Long chatId) {
        userState.remove(chatId);
    }

}
