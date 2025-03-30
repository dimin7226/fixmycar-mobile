package com.fixmycar.repository;

import com.fixmycar.model.Car;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    Optional<Car> findByVin(String vin);

    Optional<Car> findCarById(Long id);

    List<Car> findByCustomerId(Long customerId);

    List<Car> findByBrand(String brand);

    List<Car> findByModel(String model);

    List<Car> findByBrandAndModel(String brand, String model);

    @Query(value = "SELECT * FROM car c WHERE c.brand = :brand AND c.model = :model",
            nativeQuery = true)
    List<Car> findByBrandAndModelNative(@Param("brand") String brand, @Param("model") String model);

    @Query(value = "SELECT * FROM car c WHERE c.brand = :brand",
            nativeQuery = true)
    List<Car> findByBrandNative(@Param("brand") String brand);

    @Query(value = "SELECT * FROM car c WHERE c.model = :model",
            nativeQuery = true)
    List<Car> findByModelNative(@Param("model") String model);
}