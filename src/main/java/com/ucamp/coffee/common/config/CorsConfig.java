package com.ucamp.coffee.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Value("${server.host}")
    private String host;

    @Value("${server.frontend-port}")
    private int frontPort;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                String origin;

                if (frontPort == 443) {
                    origin = String.format("%s", host);
                } else if (frontPort == 80) {
                    origin = String.format("%s", host);
                } else {
                    origin = String.format("%s:%d", host, frontPort);
                }

                registry.addMapping("/**")
                    .allowedOrigins(origin)
                    .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                    .allowCredentials(true);
            }
        };
    }
}