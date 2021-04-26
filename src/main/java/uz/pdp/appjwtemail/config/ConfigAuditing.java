package uz.pdp.appjwtemail.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.UUID;

@Configuration
@EnableJpaAuditing
public class ConfigAuditing {

    @Bean
    AuditorAware<UUID> auditorAware() {
        return new SecurityConfigAuditing();
    }
}
