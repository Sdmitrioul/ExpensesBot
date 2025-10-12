package com.dskroba.configurations;

import com.dskroba.configurations.properties.NotionProperties;
import com.dskroba.groups.GroupRepository;
import com.dskroba.groups.GroupRepositoryInMem;
import com.dskroba.notion.NotionContext;
import com.dskroba.notion.NotionFacade;
import com.dskroba.principals.PrincipalRepository;
import com.dskroba.principals.PrincipalsRepositoryInMem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        name = "notion.enabled",
        havingValue = "true"
)
@EnableConfigurationProperties(NotionProperties.class)
public class NotionConfiguration {
    @Bean
    public NotionContext notionContext(@Autowired NotionProperties notionProperties) {
        return new NotionContext(notionProperties);
    }

    @Bean
    public NotionFacade notionContext(@Autowired NotionContext context) {
        return context.getFacade();
    }

    @Bean
    public PrincipalRepository principalRepository() {
        return new PrincipalsRepositoryInMem();
    }

    @Bean
    public GroupRepository groupRepository() {
        return new GroupRepositoryInMem();
    }
}
