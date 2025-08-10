package com.dskroba.configurations.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "application")
@Validated
public record ApplicationProperties(
        @NotBlank
        String timeZone,

        @NotNull
        Console console
) {
    record Console(
            boolean enabled
    ) {
    }
}
