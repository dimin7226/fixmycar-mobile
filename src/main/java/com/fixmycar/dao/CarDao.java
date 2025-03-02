package com.fixmycar.dao;

import com.fixmycar.model.Car;
import java.util.Optional;

public interface CarDao {
    Optional<Car> findById(Long id);
}