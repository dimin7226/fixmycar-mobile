package com.fixmycar.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/home/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        // 🔹 Простая проверка-заглушка
        if ("user@example.com".equals(email) && "12345".equals(password)) {
            return ResponseEntity.ok(Map.of(
                    "token", "mock-jwt-token",
                    "userId", 1,
                    "name", "Test User"
            ));
        } else {
            return ResponseEntity.status(401).body(Map.of(
                    "error", "Неверный логин или пароль"
            ));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> data) {
        // Можно просто вернуть успех
        return ResponseEntity.ok(Map.of(
                "message", "Регистрация успешно выполнена (заглушка)"
        ));
    }
}