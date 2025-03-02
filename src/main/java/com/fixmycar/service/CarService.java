package com.fixmycar.service;

import com.fixmycar.model.Car;
import java.util.List;
import java.util.Optional;

public interface CarService {
    Optional<Car> getCarById(Long id);

    List<Car> getAllCars();

    List<Car> getCarsByFilter(String brand, String model);
}