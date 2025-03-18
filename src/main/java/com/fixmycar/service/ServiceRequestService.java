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

        // Проверяем, есть ли у заявки машина, клиент и сервисный центр
        if (request.getCar() != null && request.getCar().getId() != null) {
            Car car = carRepository.findById(request.getCar().getId())
                    .orElseThrow(() -> new RuntimeException("Машина не найдена"));
            request.setCar(car);
        }

        if (request.getCustomer() != null && request.getCustomer().getId() != null) {
            Customer customer = customerRepository.findById(request.getCustomer().getId())
                    .orElseThrow(() -> new RuntimeException("Клиент не найден"));
            request.setCustomer(customer);
        }

        if (request.getServiceCenter() != null && request.getServiceCenter().getId() != null) {
            ServiceCenter serviceCenter = serviceCenterRepository
                    .findById(request.getServiceCenter().getId())
                    .orElseThrow(() -> new RuntimeException("Сервисный центр не найден"));
            request.setServiceCenter(serviceCenter);
        }

        return requestRepository.save(request);
    }

    public ServiceRequest updateRequest(Long id, ServiceRequest requestDetails) {
        ServiceRequest request = getRequestById(id);

        request.setDescription(requestDetails.getDescription());
        request.setStatus(requestDetails.getStatus());

        // Обновляем машину, клиента и сервисный центр, если они указаны
        if (requestDetails.getCar() != null && requestDetails.getCar().getId() != null) {
            Car car = carRepository.findById(requestDetails.getCar().getId())
                    .orElseThrow(() -> new RuntimeException("Машина не найдена"));
            request.setCar(car);
        }

        if (requestDetails.getCustomer() != null && requestDetails.getCustomer().getId() != null) {
            Customer customer = customerRepository.findById(requestDetails.getCustomer().getId())
                    .orElseThrow(() -> new RuntimeException("Клиент не найден"));
            request.setCustomer(customer);
        }

        if (requestDetails.getServiceCenter() != null
                && requestDetails.getServiceCenter().getId() != null) {
            ServiceCenter serviceCenter = serviceCenterRepository
                    .findById(requestDetails.getServiceCenter().getId())
                    .orElseThrow(() -> new RuntimeException("Сервисный центр не найден"));
            request.setServiceCenter(serviceCenter);
        }

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
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Машина не найдена"));

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));

        ServiceCenter serviceCenter = serviceCenterRepository.findById(serviceCenterId)
                .orElseThrow(() -> new RuntimeException("Сервисный центр не найден"));

        ServiceRequest request = new ServiceRequest();
        request.setCar(car);
        request.setCustomer(customer);
        request.setServiceCenter(serviceCenter);
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