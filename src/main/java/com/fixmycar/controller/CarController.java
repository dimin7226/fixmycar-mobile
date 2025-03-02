package com.fixmycar.controller;

import com.fixmycar.model.Car;
import com.fixmycar.service.CarService; // Убедитесь, что этот импорт добавлен
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/cars/{id}")
    public Car getCarById(@PathVariable Long id) {
        return carService.getCarById(id).orElseThrow(() -> new RuntimeException("Car not found"));
    }
}