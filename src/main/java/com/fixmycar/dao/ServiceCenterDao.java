package com.fixmycar.dao;

import com.fixmycar.model.ServiceCenter;
import java.util.List;
import java.util.Optional;

public interface ServiceCenterDao {

    List<ServiceCenter> findAll();

    Optional<ServiceCenter> findById(Long id);

    List<ServiceCenter> findByName(String name);

    //List<ServiceCenter> findByCarsId(Long carId);

    ServiceCenter save(ServiceCenter serviceCenter);

    void delete(ServiceCenter serviceCenter);

    void deleteById(Long id);
}