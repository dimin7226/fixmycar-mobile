package com.fixmycar.dao;

import com.fixmycar.model.Car;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CarDaoImpl implements CarDao {

    private final List<Car> cars = new ArrayList<>();

    public CarDaoImpl() {
        // Добавляем тестовые данные
        cars.add(new Car(1L, "Toyota", "Corolla"));
        cars.add(new Car(2L, "Honda", "Civic"));
        cars.add(new Car(3L, "Ford", "Focus"));
        cars.add(new Car(4L, "BMW", "3 Series"));
        cars.add(new Car(5L, "Audi", "A4"));
    }

    @Override
    public Optional<Car> findById(Long id) {
        return cars.stream().filter(car -> car.getId().equals(id)).findFirst();
    }

    @Override
    public List<Car> findAll() {
        return cars;
    }

    @Override
    public List<Car> findByFilter(String brand, String model) {
        return cars.stream()
                .filter(car -> (brand == null || car.getBrand().equalsIgnoreCase(brand)))
                .filter(car -> (model == null || car.getModel().equalsIgnoreCase(model)))
                .toList();
    }
}