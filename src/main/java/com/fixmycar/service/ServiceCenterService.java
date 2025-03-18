package com.fixmycar.service;

import com.fixmycar.model.ServiceCenter;
import com.fixmycar.repository.ServiceCenterRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceCenterService {
    private final ServiceCenterRepository serviceCenterRepository;

    public List<ServiceCenter> getAllServiceCenters() {
        return serviceCenterRepository.findAll();
    }

    public ServiceCenter getServiceCenterById(Long id) {
        return serviceCenterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Сервисный центр не найден"));
    }

    public ServiceCenter saveServiceCenter(ServiceCenter serviceCenter) {
        return serviceCenterRepository.save(serviceCenter);
    }

    public ServiceCenter updateServiceCenter(Long id, ServiceCenter serviceCenterDetails) {
        ServiceCenter serviceCenter = getServiceCenterById(id);

        serviceCenter.setName(serviceCenterDetails.getName());
        serviceCenter.setAddress(serviceCenterDetails.getAddress());
        serviceCenter.setPhone(serviceCenterDetails.getPhone());

        return serviceCenterRepository.save(serviceCenter);
    }

    public void deleteServiceCenter(Long id) {
        serviceCenterRepository.deleteById(id);
    }

    public List<ServiceCenter> getServiceCentersByCarId(Long carId) {
        return serviceCenterRepository.findByCarsId(carId);
    }
}