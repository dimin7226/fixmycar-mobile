package com.fixmycar.controller;

import com.fixmycar.exception.ResourceNotFoundException;
import com.fixmycar.model.Car;
import com.fixmycar.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @GetMapping
    public List<Car> getAllCars() {
        return carService.getAllCars();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить машину по ID",
            description = "Возвращает машины по указанному ID")
    @ApiResponse(responseCode = "200", description = "Машина найдена")
    @ApiResponse(responseCode = "404", description = "Машина не найдена")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        Car car = carService.getCarById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found with id " + id));
        return ResponseEntity.ok(car);
    }

    @PostMapping
    @Operation(summary = "Создать машину", description = "Создает новую машину")
    @ApiResponse(responseCode = "200", description = "Машина успешно создана")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    public ResponseEntity<Car> createCar(@Valid @RequestBody Car car) {
        if (car.getCustomer() == null || car.getCustomer().getId() == null) {
            throw new ValidationException("User ID is required");
        }
        Car createdAccount = carService.saveOrUpdateCar(car);
        return ResponseEntity.ok(createdAccount);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные о машине",
            description = "Обновляет данные о машине по ID")
    @ApiResponse(responseCode = "200", description = "Данные о машине обновлены")
    @ApiResponse(responseCode = "404", description = "Данные о машине не найдены")
    public ResponseEntity<Car> updateCar(@PathVariable Long id,
                                         @Valid @RequestBody Car carDetails) {
        Car car = carService.getCarById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id " + id));
        car.setBrand(carDetails.getBrand());
        car.setModel(carDetails.getModel());
        car.setVin(carDetails.getVin());
        car.setYear(carDetails.getYear());
        if (carDetails.getCustomer() != null && carDetails.getCustomer().getId() != null) {
            car.setCustomer(carDetails.getCustomer());
        }
        Car updatedCar = carService.saveOrUpdateCar(car);
        return ResponseEntity.ok(updatedCar);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить машину", description = "Удаляет машину по ID")
    @ApiResponse(responseCode = "204", description = "Машина успешно удалена")
    @ApiResponse(responseCode = "404", description = "Машина не найдена")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }
}