package com.fixmycar.service;

import com.fixmycar.cache.InMemoryCache;
import com.fixmycar.exception.ResourceNotFoundException;
import com.fixmycar.model.Car;
import com.fixmycar.model.Customer;
import com.fixmycar.model.ServiceCenter;
import com.fixmycar.model.ServiceRequest;
import com.fixmycar.repository.CarRepository;
import com.fixmycar.repository.CustomerRepository;
import com.fixmycar.repository.ServiceCenterRepository;
import com.fixmycar.repository.ServiceRequestRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ServiceRequestService {
    private final ServiceRequestRepository requestRepository;
    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;
    private final ServiceCenterRepository serviceCenterRepository;
    private final InMemoryCache<Long, ServiceRequest> requestCache;

    private Car findCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Машина не найдена"));
    }

    private Customer findCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент не найден"));
    }

    private ServiceCenter findServiceCenterById(Long id) {
        return serviceCenterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Сервисный центр не найден"));
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

    public Optional<ServiceRequest> getRequestById(Long id) {
        ServiceRequest cachedRequest = requestCache.get(id);
        if (cachedRequest != null) {
            return Optional.of(cachedRequest);
        }
        Optional<ServiceRequest> request = requestRepository.findById(id);
        request.ifPresent(req -> requestCache.put(id, req));

        return request;
    }

    public ServiceRequest saveRequest(ServiceRequest request) {
        if (request.getCreatedAt() == null) {
            request.setCreatedAt(LocalDateTime.now());
        }

        if (request.getStatus() == null) {
            request.setStatus("PENDING");
        }

        updateEntityReferences(request, request);
        ServiceRequest savedRequest = requestRepository.save(request);

        requestCache.put(savedRequest.getId(), savedRequest);
        return savedRequest;
    }

    public ServiceRequest updateRequest(Long id, ServiceRequest requestDetails) {
        ServiceRequest request = getRequestById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Заявка не найдена с id " + id));

        request.setDescription(requestDetails.getDescription());
        request.setStatus(requestDetails.getStatus());

        updateEntityReferences(request, requestDetails);
        ServiceRequest updatedRequest = requestRepository.save(request);

        requestCache.put(updatedRequest.getId(), updatedRequest);
        return updatedRequest;
    }

    public void deleteRequest(Long id) {
        requestRepository.deleteById(id);
        requestCache.evict(id);
    }

    public List<ServiceRequest> getRequestsByCarAttributes(
            String brand, String model, Integer year) {
        return requestRepository.findByCarAttributes(brand, model, year);
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

    public ServiceRequest createServiceRequest(Long customerId, Long carId,
                                               Long serviceCenterId, String description) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id "
                        + customerId));

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id "
                        + carId));

        ServiceCenter serviceCenter = serviceCenterRepository.findById(serviceCenterId)
                .orElseThrow(() -> new ResourceNotFoundException("Service center not found with id "
                        + serviceCenterId));

        ServiceRequest request = ServiceRequest.builder()
                .customer(customer)
                .car(car)
                .serviceCenter(serviceCenter)
                .description(description)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        ServiceRequest savedRequest = requestRepository.save(request);
        requestCache.put(savedRequest.getId(), savedRequest);
        return savedRequest;
    }

    public ServiceRequest updateStatus(Long id, String status) {
        ServiceRequest request = getRequestById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Заявка не найдена с id " + id));
        request.setStatus(status);
        ServiceRequest updatedRequest = requestRepository.save(request);
        requestCache.put(updatedRequest.getId(), updatedRequest);
        return updatedRequest;
    }
}