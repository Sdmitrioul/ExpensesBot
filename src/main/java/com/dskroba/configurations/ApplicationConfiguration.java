package com.dskroba.configurations;

import com.dskroba.configurations.properties.ApplicationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;

@Configuration
@EnableConfigurationProperties({ApplicationProperties.class})
public class ApplicationConfiguration {
    @Bean
    public Clock applicationClock(ApplicationProperties applicationProperties) {
        String zoneProperty = applicationProperties.timeZone();
        try {
            ZoneId zoneId = ZoneId.of(zoneProperty);
            return Clock.system(zoneId);
        } catch (ZoneRulesException e) {
            throw new RuntimeException("Unrecognized application time zone " + zoneProperty, e);
        } catch (DateTimeException e) {
            throw new RuntimeException("Invalid format of application time zone " + zoneProperty, e);
        } finally {
            System.out.println(System.getProperty("application.console.enabled"));
        }
    }
}
