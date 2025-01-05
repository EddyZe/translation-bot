package ru.eddyz.translationbot.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.translationbot.domain.models.TelegramBotConfig;
import ru.eddyz.translationbot.domain.models.YandexTranslateData;

@Configuration
public class BotConfig {

    @Bean
    public TelegramBotConfig telegramBotConfig(
            @Value("${telegram.bot.token}") String token,
            @Value("${telegram.bot.username}") String botUsername,
            @Value("${telegram.bot.username-admin}") String adminUsername) {
        return TelegramBotConfig
                .builder()
                .token(token)
                .botUsername(botUsername)
                .adminUsername(adminUsername)
                .build();
    }

    @Bean
    public YandexTranslateData yandexTranslateData(
            @Value("${yandex.translate.token}") String token,
            @Value("${yandex.translate.folder-id}") String folderId,
            @Value("${yandex.translate.base-url}") String baseUrl
    ) {
        return YandexTranslateData.builder()
                .baseUrl(baseUrl)
                .folderId(folderId)
                .token(token)
                .build();
    }

    @Bean
    public TelegramClient telegramClient(TelegramBotConfig config) {
        return new OkHttpTelegramClient(config.getToken());
    }
}
