package com.boilerplate.common;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.time.Duration;

public abstract class AbstractContainerTest {

    private static final Logger log = LoggerFactory.getLogger(AbstractContainerTest.class);

    private static final PostgreSQLContainer<?> postgreSQLContainer;

    static {
        System.out.println("AbstractContainerTest static initializer block executed.");
        
        postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13"))
                .withDatabaseName("testdb")
                .withUsername("testuser")
                .withPassword("testpass")
                .withExposedPorts(PostgreSQLContainer.POSTGRESQL_PORT)
                .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*\\n", 1)
                        .withStartupTimeout(Duration.ofSeconds(180)))
                .withLogConsumer(new Slf4jLogConsumer(log))
                .withReuse(true);
        
        postgreSQLContainer.start();
        
        System.out.println("PostgreSQL container started successfully.");
        System.out.println("JDBC URL: " + postgreSQLContainer.getJdbcUrl());
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        String jdbcUrl = postgreSQLContainer.getJdbcUrl();

        System.out.println("Testcontainers PostgreSQL JDBC URL (from getJdbcUrl()): " + jdbcUrl);
        System.out.println("Testcontainers PostgreSQL Host: " + postgreSQLContainer.getHost());
        System.out.println("Testcontainers PostgreSQL Mapped Port: " + postgreSQLContainer.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT));

        registry.add("spring.datasource.url", () -> jdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.open-in-view", () -> "false");
        registry.add("spring.datasource.hikari.connection-timeout", () -> "60000");
        registry.add("spring.datasource.hikari.maximum-pool-size", () -> "10");
    }
}
