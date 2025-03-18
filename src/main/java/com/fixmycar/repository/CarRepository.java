package com.fixmycar.repository;

import com.fixmycar.model.Car;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    Optional<Car> findByVin(String vin);

    List<Car> findByCustomerId(Long customerId);

    List<Car> findByServiceCentersId(Long serviceCenterId);
}