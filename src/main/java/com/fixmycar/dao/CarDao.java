package com.fixmycar.dao;

import com.fixmycar.model.Car;
import java.util.List;
import java.util.Optional;

public interface CarDao {
    Optional<Car> findById(Long id);

    List<Car> findAll();

    List<Car> findByFilter(String brand, String model);
}