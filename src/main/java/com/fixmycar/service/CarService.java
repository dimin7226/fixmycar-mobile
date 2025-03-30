package com.fixmycar.service;

import com.fixmycar.cache.InMemoryCache;
import com.fixmycar.exception.ResourceNotFoundException;
import com.fixmycar.model.Car;
import com.fixmycar.model.Customer;
import com.fixmycar.repository.CarRepository;
import com.fixmycar.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;
    private final InMemoryCache<Long, Car> carCache;

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Optional<Car> getCarById(Long id) {

        Car cachedCar = carCache.get(id);
        if (cachedCar != null) {
            return Optional.of(cachedCar);
        }
        Optional<Car> car = carRepository.findById(id);
        car.ifPresent(acc -> carCache.put(id, acc));

        return car;
    }

    public Car saveOrUpdateCar(Car car) {
        if (car.getCustomer() == null || car.getCustomer().getId() == null) {
            throw new ResourceNotFoundException("User ID is required");
        }

        Customer customer = customerRepository.findById(car.getCustomer().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id "
                        + car.getCustomer().getId()));
        car.setCustomer(customer);

        Car savedCar = carRepository.save(car);

        carCache.put(savedCar.getId(), savedCar);
        return savedCar;
    }

    public void deleteCar(Long id) {
        carRepository.deleteById(id);

        carCache.evict(id);
    }
}