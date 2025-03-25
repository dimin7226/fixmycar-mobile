package com.fixmycar.dao;

import com.fixmycar.model.ServiceCenter;
import com.fixmycar.repository.ServiceCenterRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ServiceCenterDaoImpl implements ServiceCenterDao {
    private final ServiceCenterRepository serviceCenterRepository;

    @Override
    public List<ServiceCenter> findAll() {
        return serviceCenterRepository.findAll();
    }

    @Override
    public Optional<ServiceCenter> findById(Long id) {
        return serviceCenterRepository.findById(id);
    }

    @Override
    public List<ServiceCenter> findByName(String name) {
        return serviceCenterRepository.findByName(name);
    }

    @Override
    public ServiceCenter save(ServiceCenter serviceCenter) {
        return serviceCenterRepository.save(serviceCenter);
    }

    @Override
    public void delete(ServiceCenter serviceCenter) {
        serviceCenterRepository.delete(serviceCenter);
    }

    @Override
    public void deleteById(Long id) {
        serviceCenterRepository.deleteById(id);
    }
}