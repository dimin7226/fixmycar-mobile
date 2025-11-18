package com.fixmycar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Включаем CORS с нашей конфигурацией
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Отключаем CSRF, чтобы фронтенд мог делать POST/PUT/DELETE без токена
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Разрешаем все запросы (пока без авторизации, можно потом добавить JWT)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                // Заголовки, чтобы H2 console (если используется) работала
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 🔹 Разрешаем только твой фронтенд (замени на реальный URL фронтенда)
        configuration.setAllowedOrigins(List.of(
                 "http://192.168.10.100", // локальный фронтенд
                "https://fixmycar-frontend.onrender.com" // деплой на Render/Nginx
        ));

        // Методы, которые будут использоваться фронтендом
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));

        // Разрешаем любые заголовки
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // кэширование preflight-запросов на 1 час

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
