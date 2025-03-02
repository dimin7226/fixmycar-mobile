package com.fixmycar.dao;

import com.fixmycar.model.Car;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CarDaoImpl implements CarDao {

    @Override
    public Optional<Car> findById(Long id) {
        // Заглушка: возвращаем тестовые данные
        return Optional.of(new Car(id, "Toyota", "Corolla"));
    }
}