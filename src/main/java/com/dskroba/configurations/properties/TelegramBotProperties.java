package com.dskroba.configurations.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "telegram.bot")
@Validated
public record TelegramBotProperties(
        boolean enabled,

        @NotBlank
        String token,

        @NotBlank
        String allowedUsers,

        @Min(1) @Max(10)
        int threadsCount
) {
}
