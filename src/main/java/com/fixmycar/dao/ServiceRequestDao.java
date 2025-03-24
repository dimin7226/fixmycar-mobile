package com.fixmycar.dao;

import com.fixmycar.model.ServiceRequest;
import java.util.List;
import java.util.Optional;

public interface ServiceRequestDao {

    List<ServiceRequest> findAll();

    Optional<ServiceRequest> findById(Long id);

    List<ServiceRequest> findByCustomerId(Long customerId);

    List<ServiceRequest> findByCarId(Long carId);

    List<ServiceRequest> findByServiceCenterId(Long serviceCenterId);

    ServiceRequest save(ServiceRequest serviceRequest);

    void delete(ServiceRequest serviceRequest);

    void deleteById(Long id);

    void deleteAll(List<ServiceRequest> serviceRequests);
}