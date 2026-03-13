package com.learning.user_service.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * ═══════════════════════════════════════════════════════════════════
 * PRIMARY DATASOURCE CONFIGURATION (PostgreSQL)
 * ═══════════════════════════════════════════════════════════════════
 * Demonstrates: Multiple database connections, HikariCP connection pool
 *
 * CONCEPT — WHY MULTIPLE DATASOURCES?
 *   Normal app: 1 database for everything
 *   Enterprise app: Different databases for different concerns
 *     → PostgreSQL for users (relational, ACID transactions)
 *     → MySQL for audit logs (separate DB, won't slow down user queries)
 *
 * PSEUDOCODE — How Spring resolves which DB to use:
 *   1. Spring sees @EnableJpaRepositories(basePackages = "...repository.primary")
 *   2. Any Repository in that package → uses primaryEntityManagerFactory
 *   3. primaryEntityManagerFactory → uses primaryDataSource
 *   4. primaryDataSource → reads YAML: spring.datasource.primary.url
 *   5. That URL points to PostgreSQL: jdbc:postgresql://localhost:5432/userdb
 *
 * PSEUDOCODE — How @Primary works:
 *   When Spring has TWO DataSource beans and someone asks for "a DataSource"
 *   without specifying which one, @Primary says "use THIS one by default"
 *
 * PSEUDOCODE — HikariCP Connection Pool:
 *   Instead of: open DB connection → query → close (slow, every time)
 *   HikariCP:   keep 5-10 connections OPEN permanently
 *               thread needs DB? → borrow connection from pool (instant)
 *               thread done? → return connection to pool (reused)
 *   Result: 100x faster than opening fresh connections every time
 *
 * KEY ANNOTATIONS:
 *   @Primary → default bean when multiple beans of same type exist
 *   @ConfigurationProperties("spring.datasource.primary") → binds YAML to Java
 *   @Qualifier("primaryDataSource") → pick THIS specific bean by name
 *   @EnableJpaRepositories → tells Spring which repos use which datasource
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.learning.user_service.repository.primary",
    entityManagerFactoryRef = "primaryEntityManagerFactory",
    transactionManagerRef = "primaryTransactionManager"
)
public class PrimaryDataSourceConfig {

    @Primary
    @Bean(name = "primaryDataSourceProperties")
    @ConfigurationProperties("spring.datasource.primary")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "primaryDataSource")
    @ConfigurationProperties("spring.datasource.primary.hikari")
    public DataSource primaryDataSource(@Qualifier("primaryDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    /**
     * CONCEPT — WHY .properties() IS NEEDED HERE:
     *   When you define a custom EntityManagerFactory, Spring Boot's auto-config
     *   for spring.jpa.hibernate.ddl-auto is BYPASSED entirely.
     *   You MUST pass Hibernate properties manually via .properties(Map).
     *
     *   Without .properties():
     *     spring.jpa.hibernate.ddl-auto=update → IGNORED → tables NOT created!
     *
     *   With .properties():
     *     hibernate.hbm2ddl.auto=update → Hibernate creates/updates tables automatically
     *
     * PSEUDOCODE — What ddl-auto=update does on startup:
     *   1. Hibernate reads @Entity classes in "com.learning.user_service.entity"
     *   2. Compares entity fields with existing DB table columns
     *   3. Table doesn't exist? → CREATE TABLE users (...)
     *   4. New field added to entity? → ALTER TABLE users ADD COLUMN ...
     *   5. Existing column type changed? → usually ignores (safe mode)
     *   6. Field removed from entity? → column NOT dropped (data preserved)
     */
    @Primary
    @Bean(name = "primaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("primaryDataSource") DataSource dataSource) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");

        return builder
                .dataSource(dataSource)
                .packages("com.learning.user_service.entity")
                .persistenceUnit("primary")
                .properties(properties)
                .build();
    }

    @Primary
    @Bean(name = "primaryTransactionManager")
    public PlatformTransactionManager primaryTransactionManager(
            @Qualifier("primaryEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
