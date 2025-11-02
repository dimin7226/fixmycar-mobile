package com.fixmycar.service;

import com.fixmycar.cache.InMemoryCache;
import com.fixmycar.exception.ResourceNotFoundException;
import com.fixmycar.model.Car;
import com.fixmycar.model.Customer;
import com.fixmycar.repository.CarRepository;
import com.fixmycar.repository.CustomerRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarServiceTest {

    @Mock private CarRepository carRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private InMemoryCache<Long, Car> carCache;

    @InjectMocks private CarService carService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllCars_shouldReturnCarList() {
        List<Car> cars = List.of(new Car(), new Car());
        when(carRepository.findAll()).thenReturn(cars);

        List<Car> result = carService.getAllCars();

        assertThat(result).hasSize(2);
        verify(carRepository).findAll();
    }

    @Test
    void getCarById_shouldReturnFromCacheIfExists() {
        Car car = new Car();
        car.setId(1L);
        when(carCache.get(1L)).thenReturn(car);

        Optional<Car> result = carService.getCarById(1L);

        assertThat(result).contains(car);
        verify(carCache).get(1L);
        verifyNoInteractions(carRepository);
    }

    @Test
    void testSaveOrUpdateCar_NoCustomer() {
        Car carWithoutCustomer = new Car();
        carWithoutCustomer.setCustomer(null);

        assertThrows(ResourceNotFoundException.class, () -> carService.saveOrUpdateCar(carWithoutCustomer));
    }

    @Test
    void testSaveOrUpdateCar_CustomerNotFound() {
        Car car = new Car();
        Customer customer = new Customer(1L);
        car.setCustomer(customer);

        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> carService.saveOrUpdateCar(car));
    }

    @Test
    void testSaveOrUpdateCar_Success() {
        Car car = new Car();
        Customer customer = new Customer(1L);
        car.setCustomer(customer);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(carRepository.save(car)).thenReturn(car);

        Car result = carService.saveOrUpdateCar(car);
        assertNotNull(result);
        assertEquals(customer, result.getCustomer());
        verify(carCache).put(result.getId(), result);
    }

    @Test
    void getCarById_shouldFetchFromRepositoryIfNotInCache() {
        Car car = new Car();
        car.setId(1L);
        when(carCache.get(1L)).thenReturn(null);
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        Optional<Car> result = carService.getCarById(1L);

        assertThat(result).contains(car);
        verify(carCache).put(1L, car);
    }

    @Test
    void saveOrUpdateCar_shouldThrowIfCustomerIdIsNull() {
        Car car = new Car();
        car.setCustomer(new Customer()); // ID is null

        assertThatThrownBy(() -> carService.saveOrUpdateCar(car))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User ID is required");
    }

    @Test
    void saveOrUpdateCar_shouldSaveAndPutInCache() {
        Customer customer = new Customer();
        customer.setId(1L);

        Car car = new Car();
        car.setCustomer(customer);

        Car savedCar = new Car();
        savedCar.setId(42L);
        savedCar.setCustomer(customer);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(carRepository.save(car)).thenReturn(savedCar);

        Car result = carService.saveOrUpdateCar(car);

        assertThat(result.getId()).isEqualTo(42L);
        verify(carCache).put(42L, savedCar);
    }

    @Test
    void existsByVinAndIdNot_shouldReturnCorrectResult() {
        when(carRepository.existsByVinAndIdNot("VIN123", 5L)).thenReturn(true);

        boolean exists = carService.existsByVinAndIdNot("VIN123", 5L);

        assertThat(exists).isTrue();
        verify(carRepository).existsByVinAndIdNot("VIN123", 5L);
    }

    @Test
    void customerExists_shouldCheckCustomerExistence() {
        when(customerRepository.existsById(1L)).thenReturn(true);

        boolean exists = carService.customerExists(1L);

        assertThat(exists).isTrue();
        verify(customerRepository).existsById(1L);
    }

    @Test
    void existsByVin_shouldReturnTrueIfExists() {
        when(carRepository.existsByVin("VIN999")).thenReturn(true);

        assertThat(carService.existsByVin("VIN999")).isTrue();
    }

    @Test
    void deleteCar_shouldDeleteFromRepoAndEvictFromCache() {
        carService.deleteCar(3L);

        verify(carRepository).deleteById(3L);
        verify(carCache).evict(3L);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}
