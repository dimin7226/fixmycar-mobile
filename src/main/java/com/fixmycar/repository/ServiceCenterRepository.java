package com.fixmycar.repository;

import com.fixmycar.model.ServiceCenter;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceCenterRepository extends JpaRepository<ServiceCenter, Long> {
    List<ServiceCenter> findByName(String name);

    //List<ServiceCenter> findByCarsId(Long carId);
}