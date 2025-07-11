package com.lojatenis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.lojatenis.repository")
@EnableJpaAuditing
@EnableTransactionManagement
public class DatabaseConfig {
    // Configurações automáticas do Spring Boot
}
