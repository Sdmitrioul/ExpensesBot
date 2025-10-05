package com.dskroba.configurations;

import com.dskroba.actor.ActorManager;
import com.dskroba.configurations.properties.TelegramBotProperties;
import com.dskroba.principals.PrincipalService;
import com.dskroba.services.TelegramBotService;
import com.dskroba.statemachine.handlers.EventHandlerRegistry;
import com.dskroba.telegram.TelegramAuthorizationService;
import com.dskroba.telegram.TelegramIOModule;
import com.dskroba.telegram.TelegramStateMachine;
import com.pengrad.telegrambot.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@ConditionalOnProperty(
        name = "telegram.bot.enabled",
        havingValue = "true"
)
@Import({ApplicationConfiguration.class, DatabaseConfiguration.class})
@EnableConfigurationProperties({TelegramBotProperties.class})
public class TelegramConfiguration {
    @Bean
    public TelegramBot telegramBot(@Autowired TelegramBotProperties telegramBotProperties) {
        return new TelegramBot(telegramBotProperties.token());
    }


    @Bean
    public TelegramAuthorizationService authorizationModule(@Autowired TelegramBotProperties telegramBotProperties,
                                                            @Autowired PrincipalService principalService) {
        Set<String> allowedUsers = Arrays.stream(telegramBotProperties.allowedUsers().split(","))
                .collect(Collectors.toSet());
        return new TelegramAuthorizationService(principalService, allowedUsers);
    }

    @Bean
    public TelegramIOModule telegramIOModule(@Autowired TelegramBot bot) {
        return new TelegramIOModule(bot);
    }

    @Bean
    public TelegramStateMachine telegramStateMachine(@Autowired TelegramIOModule ioModule,
                                                     @Autowired EventHandlerRegistry eventHandlerRegistry,
                                                     @Autowired TelegramAuthorizationService authorizationModule,
                                                     @Autowired ActorManager actorManager) {
        return new TelegramStateMachine(ioModule, eventHandlerRegistry, authorizationModule, actorManager);
    }

    @Bean
    public TelegramBotService telegramContext(@Autowired TelegramBot bot, @Autowired TelegramStateMachine stateMachine) {
        return new TelegramBotService(bot, stateMachine);
    }
}
