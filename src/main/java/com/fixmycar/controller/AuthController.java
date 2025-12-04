package com.fixmycar.controller;

import com.fixmycar.model.Customer;
import com.fixmycar.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/home/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CustomerService customerService;

    @Value("${mock.jwt.token:mock-jwt-token}")
    private String mockToken;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String phone = credentials.get("phone");
        String password = credentials.get("password");

        Optional<Customer> customerOpt = customerService.getAllCustomers().stream()
                .filter(c -> c.getPhone().equals(phone))
                .findFirst();

        if (customerOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Пользователь не найден"));
        }

        Customer customer = customerOpt.get();

        if (!customer.getPassword().equals(password)) {
            return ResponseEntity.status(401).body(Map.of("error", "Неверный пароль"));
        }

        return ResponseEntity.ok(Map.of(
                "token", mockToken,
                "userId", customer.getId(),
                "name", customer.getFirstName() + " " + customer.getLastName()
        ));
    }


    @PostMapping("/register/phone")
    public ResponseEntity<?> registerPhone(@RequestBody Map<String, String> data) {
        String phone = data.get("phone");

        if (customerService.existsByPhone(phone)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Этот номер уже используется"
            ));
        }

        Customer customer = new Customer();
        customer.setPhone(phone);

        customer = customerService.saveOrUpdateCustomer(customer);

        return ResponseEntity.ok(Map.of(
                "tempUserId", customer.getId()
        ));
    }

    @PostMapping("/register/profile")
    public ResponseEntity<?> registerProfile(@RequestBody Map<String, String> data) {
        Long userId = Long.valueOf(data.get("userId"));
        String email = data.get("email");
        String firstName = data.get("firstName");
        String lastName = data.get("lastName");

        Customer customer = customerService.getCustomerById(userId)
                .orElse(null);

        if (customer == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Пользователь не найден"));
        }

        if (customerService.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Email уже используется"
            ));
        }

        customer.setEmail(email);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);

        customerService.saveOrUpdateCustomer(customer);

        return ResponseEntity.ok(Map.of(
                "message", "Данные профиля сохранены"
        ));
    }


    @PostMapping("/register/password")
    public ResponseEntity<?> registerPassword(@RequestBody Map<String, String> data) {
        Long userId = Long.valueOf(data.get("userId"));
        String password = data.get("password");
        String repeatPassword = data.get("repeatPassword");

        if (!password.equals(repeatPassword)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Пароли не совпадают"
            ));
        }

        Customer customer = customerService.getCustomerById(userId)
                .orElse(null);

        if (customer == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Пользователь не найден"));
        }

        customer.setPassword(password);
        customerService.saveOrUpdateCustomer(customer);

        return ResponseEntity.ok(Map.of(
                "message", "Регистрация завершена",
                "token", mockToken,
                "userId", userId
        ));
    }
}
