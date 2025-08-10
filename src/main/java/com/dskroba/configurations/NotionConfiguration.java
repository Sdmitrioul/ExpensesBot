package com.dskroba.configurations;

import com.dskroba.configurations.properties.NotionProperties;
import com.dskroba.notion.NotionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@ConditionalOnProperty(
        name = "notion.enabled",
        havingValue = "true"
)
@EnableConfigurationProperties(NotionProperties.class)
public class NotionConfiguration {
    @Bean
    @ConditionalOnProperty(name = "notion.enabled", havingValue = "true")
    public NotionContext notionContext(@Autowired Clock clock, @Autowired NotionProperties notionProperties) {
        return new NotionContext(clock, notionProperties);
    }
}
