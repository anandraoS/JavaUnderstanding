package com.learning.user_service.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * ═══════════════════════════════════════════════════════════════════
 * SECONDARY DATASOURCE CONFIGURATION (MySQL)
 * ═══════════════════════════════════════════════════════════════════
 * Demonstrates: Multiple database connections (MySQL for audit logs)
 *
 * CONCEPT — WHY A SEPARATE DB FOR AUDITING?
 *   User creates account → save to PostgreSQL (fast, returns immediately)
 *   Background thread writes audit log → save to MySQL (async, doesn't block user)
 *   If MySQL is slow → user's request is NOT affected
 *
 * PSEUDOCODE — How this differs from PrimaryDataSourceConfig:
 *   - NO @Primary → this is the "secondary" bean
 *   - basePackages = "...repository.secondary" → only repos in this package use MySQL
 *   - Reads YAML: spring.datasource.secondary.url → jdbc:mysql://localhost:3306/user_audit_db
 *
 * PSEUDOCODE — How @Transactional("secondaryTransactionManager") works:
 *   @Transactional                             → uses @Primary (PostgreSQL)
 *   @Transactional("secondaryTransactionManager") → uses THIS (MySQL)
 *   This is how UserService.createAuditAsync() writes to MySQL, not PostgreSQL.
 *
 * KEY CLASSES:
 *   DataSourceProperties → reads url, username, password from YAML
 *   HikariDataSource     → connection pool implementation
 *   EntityManagerFactory  → JPA's bridge between Java objects and DB tables
 *   JpaTransactionManager → wraps DB operations in BEGIN/COMMIT/ROLLBACK
 */
@Configuration
@EnableJpaRepositories(
    basePackages = "com.learning.user_service.repository.secondary",
    entityManagerFactoryRef = "secondaryEntityManagerFactory",
    transactionManagerRef = "secondaryTransactionManager"
)
public class SecondaryDataSourceConfig {

    @Bean(name = "secondaryDataSourceProperties")
    @ConfigurationProperties("spring.datasource.secondary")
    public DataSourceProperties secondaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "secondaryDataSource")
    @ConfigurationProperties("spring.datasource.secondary.hikari")
    public DataSource secondaryDataSource(@Qualifier("secondaryDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "secondaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("secondaryDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.learning.user_service.entity")
                .persistenceUnit("secondary")
                .build();
    }

    @Bean(name = "secondaryTransactionManager")
    public PlatformTransactionManager secondaryTransactionManager(
            @Qualifier("secondaryEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
