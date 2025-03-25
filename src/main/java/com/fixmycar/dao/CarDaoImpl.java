package com.fixmycar.dao;

import com.fixmycar.model.Car;
import com.fixmycar.repository.CarRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CarDaoImpl implements CarDao {
    private final CarRepository carRepository;

    @Override
    public List<Car> findAll() {
        return carRepository.findAll();
    }

    @Override
    public Optional<Car> findById(Long id) {
        return carRepository.findById(id);
    }

    @Override
    public Optional<Car> findByVin(String vin) {
        return carRepository.findByVin(vin);
    }

    @Override
    public List<Car> findByCustomerId(Long customerId) {
        return carRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Car> findByBrandAndModel(String brand, String model) {
        return carRepository.findByBrandAndModel(brand, model);
    }

    @Override
    public List<Car> findByBrandAndModelNative(String brand, String model) {
        return carRepository.findByBrandAndModelNative(brand, model);
    }

    @Override
    public Car save(Car car) {
        return carRepository.save(car);
    }

    @Override
    public void delete(Car car) {
        carRepository.delete(car);
    }

    @Override
    public void deleteById(Long id) {
        carRepository.deleteById(id);
    }
}