package com.fixmycar.repository;

import com.fixmycar.model.ServiceRequest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    List<ServiceRequest> findByCustomerId(Long customerId);

    List<ServiceRequest> findByCarId(Long carId);

    List<ServiceRequest> findByServiceCenterId(Long serviceCenterId);

    @Query("SELECT sr FROM ServiceRequest sr JOIN sr.car c WHERE "
            + "(:brand IS NULL OR c.brand = :brand) AND "
            + "(:model IS NULL OR c.model = :model) AND "
            + "(:year IS NULL OR c.year = :year)")
    List<ServiceRequest> findByCarAttributes(@Param("brand") String brand,
                                             @Param("model") String model,
                                             @Param("year") Integer year);
}