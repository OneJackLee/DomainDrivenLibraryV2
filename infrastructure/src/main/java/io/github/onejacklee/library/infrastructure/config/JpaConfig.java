package io.github.onejacklee.library.infrastructure.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "io.github.onejacklee.library.infrastructure.persistence.entity")
@EnableJpaRepositories(basePackages = "io.github.onejacklee.library.infrastructure.persistence.repository")
public class JpaConfig {
}
