package com.dskroba.configurations;

import com.dskroba.configurations.properties.DatabaseProperties;
import com.dskroba.principals.PrincipalRepository;
import com.dskroba.principals.PrincipalRepositoryDAO;
import com.dskroba.expenses.ExpenseRepository;
import com.dskroba.expenses.ExpenseRepositoryImpl;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(
        name = "application.database.enabled",
        havingValue = "true"
)
@EnableConfigurationProperties({DatabaseProperties.class})
public class DatabaseConfiguration {

    @Bean
    public DataSource getDataSource(@Autowired DatabaseProperties databaseProperties) {
        var config = new HikariConfig();
        config.setJdbcUrl(databaseProperties.url());
        config.setUsername(databaseProperties.username());
        config.setPassword(databaseProperties.password());
        return new HikariDataSource(config);
    }

    @Bean
    public SpringLiquibase liquibase(@Autowired DataSource dataSource) {
        var liquibase = new SpringLiquibase();
        liquibase.setChangeLog("classpath:database/db.changelog-master.yaml");
        liquibase.setDataSource(dataSource);
        return liquibase;
    }

    @Bean
    public ExpenseRepository expenseRepository(@Autowired DataSource dataSource) {
        return new ExpenseRepositoryImpl(dataSource);
    }

    @Bean
    public PrincipalRepository principalRepository(@Autowired DataSource dataSource) {
        return new PrincipalRepositoryDAO(dataSource);
    }
}
