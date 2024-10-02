package com.io.github.amandotzip.touchgrass.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration for CORS to prevent CORS errors
 */
@Configuration
public class WebConfig implements WebMvcConfigurer{

    @Value("${frontend-url}")
    private String frontendUrl;
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        System.out.println(frontendUrl);

        registry.addMapping("/**")
                .allowedOrigins(frontendUrl)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);  // Cache pre-flight response for 1 hour
    }
    
}
