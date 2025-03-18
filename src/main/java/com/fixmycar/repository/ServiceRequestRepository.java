package com.fixmycar.repository;

import com.fixmycar.model.ServiceRequest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    List<ServiceRequest> findByCustomerId(Long customerId);

    List<ServiceRequest> findByCarId(Long carId);

    List<ServiceRequest> findByServiceCenterId(Long serviceCenterId);
}