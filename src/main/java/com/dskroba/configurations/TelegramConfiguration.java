package com.dskroba.configurations;

import com.dskroba.configurations.properties.TelegramBotProperties;
import com.dskroba.telegram.TelegramContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.Clock;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Configuration
@ConditionalOnProperty(
        name = "telegram.bot.enabled",
        havingValue = "true"
)
@Import({ApplicationConfiguration.class})
@EnableConfigurationProperties({TelegramBotProperties.class})
public class TelegramConfiguration {
    @Bean
    public Set<String> verifiedUsers(@Autowired TelegramBotProperties telegramBotProperties) {
        return Arrays.stream(telegramBotProperties.allowedUsers().split(";"))
                .collect(Collectors.toSet());
    }

    @Bean
    public ExecutorService telegramExecutorService(@Autowired TelegramBotProperties telegramBotProperties) {
        return Executors.newFixedThreadPool(telegramBotProperties.threadsCount());
    }

    @Bean
    public TelegramContext telegramContext(@Autowired TelegramBotProperties telegramBotProperties, @Autowired Clock clock) {
        ExecutorService executorService = Executors.newFixedThreadPool(telegramBotProperties.threadsCount());
        Set<String> allowedUsers = Arrays.stream(telegramBotProperties.allowedUsers().split(";"))
                .collect(Collectors.toSet());
        return new TelegramContext(executorService, telegramBotProperties.token(), null, clock, allowedUsers);
    }
}
