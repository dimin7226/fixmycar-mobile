package com.fixmycar.service;

import com.fixmycar.model.Car;
import com.fixmycar.model.Customer;
import com.fixmycar.model.ServiceCenter;
import com.fixmycar.model.ServiceRequest;
import com.fixmycar.repository.CarRepository;
import com.fixmycar.repository.CustomerRepository;
import com.fixmycar.repository.ServiceCenterRepository;
import com.fixmycar.repository.ServiceRequestRepository;
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
    private final ServiceRequestRepository serviceRequestRepository;

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

    public Car updateCarInfo(Long carId, Car updatedCar) {
        Car existingCar = getCarById(carId);

        existingCar.setBrand(updatedCar.getBrand());
        existingCar.setModel(updatedCar.getModel());
        existingCar.setVin(updatedCar.getVin());
        existingCar.setYear(updatedCar.getYear());

        return carRepository.save(existingCar);
    }

    public void deleteCar(Long id) {

        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found"));

        List<ServiceRequest> serviceRequests = car.getServiceRequests();
        if (serviceRequests != null && !serviceRequests.isEmpty()) {
            serviceRequestRepository.deleteAll(serviceRequests);
        }

        carRepository.delete(car);
    }

    @Transactional
    public Car transferOwnership(Long carId, Long newCustomerId) {
        Car car = getCarById(carId);
        Customer newOwner = customerRepository.findById(newCustomerId)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found with id: " + newCustomerId));

        // Если у автомобиля уже есть владелец, удаляем автомобиль из его списка
        if (car.getCustomer() != null) {
            car.getCustomer().getCars().remove(car);
        }

        // Устанавливаем нового владельца
        car.setCustomer(newOwner);

        // Добавляем автомобиль в список автомобилей нового владельца
        newOwner.getCars().add(car);

        return carRepository.save(car);
    }

    public List<Car> getCarsByCustomerId(Long customerId) {
        return carRepository.findByCustomerId(customerId);
    }

    public List<Car> getCarsByServiceCenterId(Long serviceCenterId) {
        return carRepository.findByServiceCentersId(serviceCenterId);
    }

    public Car assignToCustomer(Car car, Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found with id: " + customerId));

        car.setCustomer(customer);
        customer.getCars().add(car);

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