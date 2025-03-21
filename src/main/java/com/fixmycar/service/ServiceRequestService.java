package com.fixmycar.service;

import com.fixmycar.model.Car;
import com.fixmycar.model.Customer;
import com.fixmycar.model.ServiceCenter;
import com.fixmycar.model.ServiceRequest;
import com.fixmycar.repository.CarRepository;
import com.fixmycar.repository.CustomerRepository;
import com.fixmycar.repository.ServiceCenterRepository;
import com.fixmycar.repository.ServiceRequestRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceRequestService {
    private final ServiceRequestRepository requestRepository;
    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;
    private final ServiceCenterRepository serviceCenterRepository;

    private Car findCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Машина не найдена"));
    }

    private Customer findCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));
    }

    private ServiceCenter findServiceCenterById(Long id) {
        return serviceCenterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Сервисный центр не найден"));
    }

    private void updateEntityReferences(ServiceRequest request, ServiceRequest requestDetails) {
        if (requestDetails.getCar() != null && requestDetails.getCar().getId() != null) {
            request.setCar(findCarById(requestDetails.getCar().getId()));
        }

        if (requestDetails.getCustomer() != null && requestDetails.getCustomer().getId() != null) {
            request.setCustomer(findCustomerById(requestDetails.getCustomer().getId()));
        }

        if (requestDetails.getServiceCenter() != null
                && requestDetails.getServiceCenter().getId() != null) {
            request.setServiceCenter(findServiceCenterById(requestDetails
                    .getServiceCenter().getId()));
        }
    }

    public List<ServiceRequest> getAllRequests() {
        return requestRepository.findAll();
    }

    public ServiceRequest getRequestById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заявка не найдена"));
    }

    public ServiceRequest saveRequest(ServiceRequest request) {
        if (request.getCreatedAt() == null) {
            request.setCreatedAt(LocalDateTime.now());
        }

        updateEntityReferences(request, request);
        return requestRepository.save(request);
    }

    public ServiceRequest updateRequest(Long id, ServiceRequest requestDetails) {
        ServiceRequest request = getRequestById(id);

        request.setDescription(requestDetails.getDescription());
        request.setStatus(requestDetails.getStatus());

        updateEntityReferences(request, requestDetails);
        return requestRepository.save(request);
    }

    public void deleteRequest(Long id) {
        requestRepository.deleteById(id);
    }

    public List<ServiceRequest> getRequestsByCustomerId(Long customerId) {
        return requestRepository.findByCustomerId(customerId);
    }

    public List<ServiceRequest> getRequestsByCarId(Long carId) {
        return requestRepository.findByCarId(carId);
    }

    public List<ServiceRequest> getRequestsByServiceCenterId(Long serviceCenterId) {
        return requestRepository.findByServiceCenterId(serviceCenterId);
    }

    public ServiceRequest createRequest(Long carId, Long customerId,
                                        Long serviceCenterId, String description) {
        ServiceRequest request = new ServiceRequest();
        request.setCar(findCarById(carId));
        request.setCustomer(findCustomerById(customerId));
        request.setServiceCenter(findServiceCenterById(serviceCenterId));
        request.setDescription(description);
        request.setStatus("PENDING");
        request.setCreatedAt(LocalDateTime.now());

        return requestRepository.save(request);
    }

    public ServiceRequest updateStatus(Long id, String status) {
        ServiceRequest request = getRequestById(id);
        request.setStatus(status);
        return requestRepository.save(request);
    }
}