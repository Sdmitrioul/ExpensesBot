package com.dskroba.configurations.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "application.google")
@Validated
public record GoogleProperties(
        @NotBlank
        String credentialsFile,
        @NotBlank
        String tokenStoragePath,
        @NotBlank
        String applicationName
) {
}
