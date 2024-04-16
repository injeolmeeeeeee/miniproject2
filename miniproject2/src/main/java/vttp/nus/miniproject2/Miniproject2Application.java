package vttp.nus.miniproject2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import vttp.nus.miniproject2.configurations.CorsConfig;

@SpringBootApplication
public class Miniproject2Application {

    public static void main(String[] args) {
        SpringApplication.run(Miniproject2Application.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new CorsConfig("/api/*", "*");
    }
}