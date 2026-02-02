package com.dskroba.configurations;

import com.dskroba.configurations.properties.DatabaseProperties;
import com.dskroba.principals.Principal;
import com.dskroba.principals.PrincipalConverter;
import com.dskroba.storage.KeyValueStorage;
import com.dskroba.storage.LongConverter;
import com.dskroba.storage.PermanentStorage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
@EnableConfigurationProperties({DatabaseProperties.class})
public class DatabaseConfiguration {
    @Bean
    public KeyValueStorage<Long, Principal> principalStorage(DatabaseProperties properties) {
        return new PermanentStorage<>(new File(properties.path(), properties.name()), LongConverter.INSTANCE, new PrincipalConverter());
    }
}
