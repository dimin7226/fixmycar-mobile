package com.fixmycar.dao;

import com.fixmycar.model.Car;
import java.util.List;
import java.util.Optional;

public interface CarDao {

    List<Car> findAll();

    Optional<Car> findById(Long id);

    Optional<Car> findByVin(String vin);

    List<Car> findByCustomerId(Long customerId);

    //List<Car> findByServiceCentersId(Long serviceCenterId);

    //List<Car> findByServiceCentersName(String serviceCenterName);

    List<Car> findByBrandAndModel(String brand, String model);

    List<Car> findByBrandAndModelNative(String brand, String model);

    //List<Car> findWithCustomerAndServiceCentersByServiceCentersId(Long serviceCenterId);

    Car save(Car car);

    void delete(Car car);

    void deleteById(Long id);
}