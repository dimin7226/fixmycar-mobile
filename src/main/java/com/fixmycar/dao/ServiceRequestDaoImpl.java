package com.fixmycar.dao;

import com.fixmycar.model.ServiceRequest;
import com.fixmycar.repository.ServiceRequestRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ServiceRequestDaoImpl implements ServiceRequestDao {
    private final ServiceRequestRepository serviceRequestRepository;

    @Override
    public List<ServiceRequest> findAll() {
        return serviceRequestRepository.findAll();
    }

    @Override
    public Optional<ServiceRequest> findById(Long id) {
        return serviceRequestRepository.findById(id);
    }

    @Override
    public List<ServiceRequest> findByCustomerId(Long customerId) {
        return serviceRequestRepository.findByCustomerId(customerId);
    }

    @Override
    public List<ServiceRequest> findByCarId(Long carId) {
        return serviceRequestRepository.findByCarId(carId);
    }

    @Override
    public List<ServiceRequest> findByServiceCenterId(Long serviceCenterId) {
        return serviceRequestRepository.findByServiceCenterId(serviceCenterId);
    }

    @Override
    public ServiceRequest save(ServiceRequest serviceRequest) {
        return serviceRequestRepository.save(serviceRequest);
    }

    @Override
    public void delete(ServiceRequest serviceRequest) {
        serviceRequestRepository.delete(serviceRequest);
    }

    @Override
    public void deleteById(Long id) {
        serviceRequestRepository.deleteById(id);
    }

    @Override
    public void deleteAll(List<ServiceRequest> serviceRequests) {
        serviceRequestRepository.deleteAll(serviceRequests);
    }
}