package com.fixmycar.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/api/home/auth")
public class AuthController {

    @Value("${mock.user.email}")
    private String mockEmail;

    @Value("${mock.user.password}")
    private String mockPassword;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        if (mockEmail.equals(email) && mockPassword.equals(password)) {
            return ResponseEntity.ok(Map.of(
                    "token", "mock-jwt-token",
                    "userId", 1,
                    "name", "Test User"
            ));
        }

        return ResponseEntity.status(401).body(Map.of(
                "error", "Неверный логин или пароль"
        ));
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> data) {
        // Можно просто вернуть успех
        return ResponseEntity.ok(Map.of(
                "message", "Регистрация успешно выполнена (заглушка)"
        ));
    }
}