package vttp.nus.miniproject2.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    final String path;
    final String origins;

    public CorsConfig(@Value("${cors.path}") String path, @Value("${cors.origins}") String origins) {
        this.path = path;
        this.origins = origins;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(path)
                .allowedOrigins(origins);
    }
}