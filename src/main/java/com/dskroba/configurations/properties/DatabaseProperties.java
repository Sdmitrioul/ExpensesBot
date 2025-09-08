package com.dskroba.configurations.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "application.database")
@Validated
public record DatabaseProperties(
        boolean enabled,
        @NotBlank
        String url,

        @NotBlank
        String username,

        @NotBlank
        String password
) {
}
