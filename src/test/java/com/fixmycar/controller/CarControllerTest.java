package com.fixmycar.controller;

import com.fixmycar.exception.BadRequestException;
import com.fixmycar.exception.ResourceNotFoundException;
import com.fixmycar.model.Car;
import com.fixmycar.model.Customer;
import com.fixmycar.service.CarService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarControllerTest {

    @Mock
    private CarService carService;

    @InjectMocks
    private CarController carController;

    private Car createValidCar() {
        Customer customer = new Customer();
        customer.setId(1L);

        Car car = new Car();
        car.setId(1L);
        car.setVin("VIN123");
        car.setYear(2020);
        car.setBrand("Toyota");
        car.setModel("Corolla");
        car.setCustomer(customer);

        return car;
    }

    @Test
    void getAllCars_ShouldReturnAllCars() {
        List<Car> cars = Arrays.asList(new Car(), new Car());
        when(carService.getAllCars()).thenReturn(cars);

        List<Car> result = carController.getAllCars();

        assertEquals(2, result.size());
    }

    @Test
    void getCarById_ShouldReturnCar() {
        Car car = createValidCar();
        when(carService.getCarById(1L)).thenReturn(Optional.of(car));

        ResponseEntity<Car> response = carController.getCarById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void getCarById_ShouldThrow_WhenNotFound() {
        when(carService.getCarById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> carController.getCarById(1L));
    }

    @Test
    void createCar_ShouldReturnCreatedCar() {
        Car car = createValidCar();

        when(carService.existsByVin("VIN123")).thenReturn(false);
        when(carService.customerExists(1L)).thenReturn(true);
        when(carService.saveOrUpdateCar(car)).thenReturn(car);

        ResponseEntity<Car> response = carController.createCar(car);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("VIN123", response.getBody().getVin());
    }

    @Test
    void createCar_ShouldThrowBadRequest_WhenYearTooOld() {
        Car car = createValidCar();
        car.setYear(1975);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> carController.createCar(car));
        assertEquals("Year must be between 1980 and 2025", exception.getMessage());
    }

    @Test
    void createCar_ShouldThrowBadRequest_WhenYearTooNew() {
        Car car = createValidCar();
        car.setYear(2030);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> carController.createCar(car));
        assertEquals("Year must be between 1980 and 2025", exception.getMessage());
    }

    @Test
    void createCar_ShouldThrowBadRequest_WhenVinExists() {
        Car car = createValidCar();

        when(carService.existsByVin("VIN123")).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> carController.createCar(car));
        assertEquals("Car with this VIN already exists", exception.getMessage());
    }

    @Test
    void createCar_ShouldThrowBadRequest_WhenCustomerMissing() {
        Car car = createValidCar();
        car.setCustomer(null);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> carController.createCar(car));
        assertEquals("Customer with specified ID does not exist", exception.getMessage());
    }

    @Test
    void createCar_ShouldThrowBadRequest_WhenCustomerIdMissing() {
        Car car = createValidCar();
        car.getCustomer().setId(null);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> carController.createCar(car));
        assertEquals("Customer with specified ID does not exist", exception.getMessage());
    }

    @Test
    void createCar_ShouldThrowBadRequest_WhenCustomerNotExists() {
        Car car = createValidCar();

        when(carService.existsByVin("VIN123")).thenReturn(false);
        when(carService.customerExists(1L)).thenReturn(false);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> carController.createCar(car));
        assertEquals("Customer with specified ID does not exist", exception.getMessage());
    }

    @Test
    void updateCar_ShouldReturnUpdatedCar() {
        Car updatedDetails = createValidCar();

        Car existingCar = new Car();
        existingCar.setId(1L);

        when(carService.customerExists(1L)).thenReturn(true);
        when(carService.existsByVinAndIdNot("VIN123", 1L)).thenReturn(false);
        when(carService.getCarById(1L)).thenReturn(Optional.of(existingCar));
        when(carService.saveOrUpdateCar(existingCar)).thenReturn(existingCar);

        ResponseEntity<Car> response = carController.updateCar(1L, updatedDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(carService).saveOrUpdateCar(existingCar);
    }

    @Test
    void updateCar_ShouldThrowBadRequest_WhenYearInvalid() {
        Car updatedDetails = createValidCar();
        updatedDetails.setYear(2050);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> carController.updateCar(1L, updatedDetails));

        assertEquals("Year must be between 1980 and 2025", exception.getMessage());
    }

    @Test
    void updateCar_ShouldThrowBadRequest_WhenCustomerNotExists() {
        Car updatedDetails = createValidCar();

        when(carService.customerExists(1L)).thenReturn(false);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> carController.updateCar(1L, updatedDetails));

        assertEquals("Customer with specified ID does not exist", exception.getMessage());
    }

    @Test
    void updateCar_ShouldThrowBadRequest_WhenVinExistsInAnotherCar() {
        Car updatedDetails = createValidCar();

        when(carService.customerExists(1L)).thenReturn(true);
        when(carService.existsByVinAndIdNot("VIN123", 1L)).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> carController.updateCar(1L, updatedDetails));

        assertEquals("VIN already exists for another car", exception.getMessage());
    }

    @Test
    void updateCar_ShouldThrowResourceNotFound_WhenCarNotFound() {
        Car updatedDetails = createValidCar();

        when(carService.customerExists(1L)).thenReturn(true);
        when(carService.existsByVinAndIdNot("VIN123", 1L)).thenReturn(false);
        when(carService.getCarById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> carController.updateCar(1L, updatedDetails));
    }

    @Test
    void deleteCar_ShouldReturnNoContent() {
        doNothing().when(carService).deleteCar(1L);

        ResponseEntity<Void> response = carController.deleteCar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(carService).deleteCar(1L);
    }
}
