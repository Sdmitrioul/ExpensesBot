package com.dskroba.configurations.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties(prefix = "notion")
@Validated
public record NotionProperties(
        boolean enabled,

        @NotBlank
        String token,

        @NotBlank
        String databaseId,

        @Valid
        @NotNull
        Api api,

        @Valid
        @NotNull
        RateLimit rateLimit
) {

    @Validated
    public record Api(
            @NotBlank
            String version,

            @NotBlank
            String url,

            @NotNull
            Duration retryDelay,

            @NotNull
            int retryCount
    ) {
    }

    @Validated
    public record RateLimit(
            @NotNull
            Duration duration,

            @Min(1)
            int threshold,

            @Min(1)
            int retry
    ) {
    }
}
