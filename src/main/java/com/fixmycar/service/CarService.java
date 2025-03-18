package com.fixmycar.service;

import com.fixmycar.model.Car;
import com.fixmycar.model.Customer;
import com.fixmycar.model.ServiceCenter;
import com.fixmycar.repository.CarRepository;
import com.fixmycar.repository.CustomerRepository;
import com.fixmycar.repository.ServiceCenterRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;
    private final ServiceCenterRepository serviceCenterRepository;

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Car getCarById(Long id) {
        return carRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Машина не найдена"));
    }

    public Car saveCar(Car car) {
        // Проверяем, есть ли у машины клиент
        if (car.getCustomer() != null && car.getCustomer().getId() != null) {
            Customer customer = customerRepository.findById(car.getCustomer().getId())
                    .orElseThrow(() -> new RuntimeException("Клиент не найден"));
            car.setCustomer(customer);
        }

        return carRepository.save(car);
    }

    public Car updateCar(Long id, Car carDetails) {
        Car car = getCarById(id);

        car.setBrand(carDetails.getBrand());
        car.setModel(carDetails.getModel());
        car.setVin(carDetails.getVin());
        car.setYear(carDetails.getYear());

        // Обновляем клиента, если он указан
        if (carDetails.getCustomer() != null && carDetails.getCustomer().getId() != null) {
            Customer customer = customerRepository.findById(carDetails.getCustomer().getId())
                    .orElseThrow(() -> new RuntimeException("Клиент не найден"));
            car.setCustomer(customer);
        }

        return carRepository.save(car);
    }

    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }

    public List<Car> getCarsByCustomerId(Long customerId) {
        return carRepository.findByCustomerId(customerId);
    }

    public List<Car> getCarsByServiceCenterId(Long serviceCenterId) {
        return carRepository.findByServiceCentersId(serviceCenterId);
    }

    public Car assignToCustomer(Car car, Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new RuntimeException("Customer not found with id: " + customerId);
        }

        Customer customer = customerRepository.findById(customerId).get();
        car.setCustomer(customer);
        return carRepository.save(car);
    }

    public Car addToServiceCenter(Long carId, Long serviceCenterId) {
        Car car = getCarById(carId);
        ServiceCenter serviceCenter = serviceCenterRepository.findById(serviceCenterId)
                .orElseThrow(() -> new RuntimeException("Сервисный центр не найден"));

        if (!car.getServiceCenters().contains(serviceCenter)) {
            car.getServiceCenters().add(serviceCenter);
        }

        return carRepository.save(car);
    }

    public Car removeFromServiceCenter(Long carId, Long serviceCenterId) {
        Car car = getCarById(carId);
        ServiceCenter serviceCenter = serviceCenterRepository.findById(serviceCenterId)
                .orElseThrow(() -> new RuntimeException("Сервисный центр не найден"));

        car.getServiceCenters().removeIf(sc -> sc.getId().equals(serviceCenterId));

        return carRepository.save(car);
    }
}