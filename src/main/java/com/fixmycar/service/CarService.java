package com.fixmycar.service;

import com.fixmycar.model.Car;
import java.util.Optional;

public interface CarService {
    Optional<Car> getCarById(Long id);
}