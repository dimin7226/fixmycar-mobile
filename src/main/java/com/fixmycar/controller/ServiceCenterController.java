package com.fixmycar.controller;

import com.fixmycar.model.ServiceCenter;
import com.fixmycar.service.ServiceCenterService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/home/service-centers")
@RequiredArgsConstructor
public class ServiceCenterController {
    private final ServiceCenterService serviceCenterService;

    @GetMapping
    public List<ServiceCenter> getAllServiceCenters() {
        return serviceCenterService.getAllServiceCenters();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceCenter> getServiceCenterById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceCenterService.getServiceCenterById(id));
    }

    @PostMapping
    public ResponseEntity<ServiceCenter> createServiceCenter(
            @RequestBody ServiceCenter serviceCenter) {
        return ResponseEntity.ok(serviceCenterService.saveServiceCenter(serviceCenter));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServiceCenter(@PathVariable Long id) {
        serviceCenterService.deleteServiceCenter(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/car/{carId}")
    public List<ServiceCenter> getServiceCentersByCarId(@PathVariable Long carId) {
        return serviceCenterService.getServiceCentersByCarId(carId);
    }
}