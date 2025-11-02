package com.fixmycar.config;

import com.fixmycar.service.VisitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Разрешаем CORS для всех путей backend
                registry.addMapping("/**")
                        // Разрешаем доступ с фронтенда
                        // Если фронтенд на Nginx локально: "http://192.168.10.100"
                        // Если фронтенд на другом домене, укажи его URL
                        .allowedOrigins("http://192.168.10.100", "https://fixmycar-frontend.onrender.com")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowCredentials(true);
            }
        };
    }
}