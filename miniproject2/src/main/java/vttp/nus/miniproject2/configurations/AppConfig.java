package vttp.nus.miniproject2.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public String corsPath() {
        return "/api/*";
    }

    @Bean
    public String corsOrigins() {
        return "*";
    }
}