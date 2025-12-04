package com.fixmycar.service;

import com.fixmycar.cache.InMemoryCache;
import com.fixmycar.exception.ResourceNotFoundException;
import com.fixmycar.model.ServiceCenter;
import com.fixmycar.repository.ServiceCenterRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ServiceCenterService {
    private final ServiceCenterRepository serviceCenterRepository;
    //private final InMemoryCache<Long, ServiceCenter> serviceCenterCache;

    public List<ServiceCenter> getAllServiceCenters() {
        return serviceCenterRepository.findAll();
    }

    public Optional<ServiceCenter> getServiceCenterById(Long id) {
        //ServiceCenter cachedServiceCenter = serviceCenterCache.get(id);
//        if (cachedServiceCenter != null) {
//            return Optional.of(cachedServiceCenter);
//        }
        Optional<ServiceCenter> serviceCenter = serviceCenterRepository.findById(id);
        //serviceCenter.ifPresent(sc -> serviceCenterCache.put(id, sc));

        return serviceCenter;
    }

    public ServiceCenter saveServiceCenter(ServiceCenter serviceCenter) {
        ServiceCenter savedServiceCenter = serviceCenterRepository.save(serviceCenter);
        //serviceCenterCache.put(savedServiceCenter.getId(), savedServiceCenter);
        return savedServiceCenter;
    }

    public ServiceCenter updateServiceCenter(Long id, ServiceCenter serviceCenterDetails) {
        ServiceCenter serviceCenter = getServiceCenterById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Сервисный центр не найден с id " + id));

        serviceCenter.setName(serviceCenterDetails.getName());
        serviceCenter.setAddress(serviceCenterDetails.getAddress());
        serviceCenter.setPhone(serviceCenterDetails.getPhone());

        ServiceCenter updatedServiceCenter = serviceCenterRepository.save(serviceCenter);
        //serviceCenterCache.put(updatedServiceCenter.getId(), updatedServiceCenter);
        return updatedServiceCenter;
    }

    public void deleteServiceCenter(Long id) {
        serviceCenterRepository.deleteById(id);
        //serviceCenterCache.evict(id);
    }
}