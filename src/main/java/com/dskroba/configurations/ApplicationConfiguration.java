package com.dskroba.configurations;

import com.dskroba.actor.ActorManager;
import com.dskroba.actor.ActorManagerImpl;
import com.dskroba.base.ClockProvider;
import com.dskroba.base.exception.InitializationException;
import com.dskroba.configurations.properties.ApplicationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import java.util.List;

@Configuration
@EnableConfigurationProperties({ApplicationProperties.class})
public class ApplicationConfiguration {
    @Bean
    public Clock applicationClock(ApplicationProperties applicationProperties) {
        String zoneProperty = applicationProperties.timeZone();
        try {
            ZoneId zoneId = ZoneId.of(zoneProperty);
            Clock system = Clock.system(zoneId);
            ClockProvider.setClock(system);
            return system;
        } catch (ZoneRulesException e) {
            throw new InitializationException("Unrecognized application time zone " + zoneProperty, e);
        } catch (DateTimeException e) {
            throw new InitializationException("Invalid format of application time zone " + zoneProperty, e);
        }
    }

    @Bean
    public ActorManager actorManager() {
        return new ActorManagerImpl(List.of());
    }
}
