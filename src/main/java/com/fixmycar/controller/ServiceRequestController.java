package com.fixmycar.controller;

import com.fixmycar.model.ServiceRequest;
import com.fixmycar.service.ServiceRequestService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home/requests")
@RequiredArgsConstructor
public class ServiceRequestController {
    private final ServiceRequestService requestService;

    @GetMapping
    public List<ServiceRequest> getAllRequests() {
        return requestService.getAllRequests();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceRequest> getRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(requestService.getRequestById(id));
    }

    @PostMapping
    public ResponseEntity<ServiceRequest> createRequest(@RequestBody ServiceRequest request) {
        return ResponseEntity.ok(requestService.saveRequest(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        requestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/customer/{customerId}")
    public List<ServiceRequest> getRequestsByCustomerId(@PathVariable Long customerId) {
        return requestService.getRequestsByCustomerId(customerId);
    }

    @GetMapping("/car/{carId}")
    public List<ServiceRequest> getRequestsByCarId(@PathVariable Long carId) {
        return requestService.getRequestsByCarId(carId);
    }

    @GetMapping("/service-center/{serviceCenterId}")
    public List<ServiceRequest> getRequestsByServiceCenterId(@PathVariable Long serviceCenterId) {
        return requestService.getRequestsByServiceCenterId(serviceCenterId);
    }

    @PostMapping("/create")
    public ResponseEntity<ServiceRequest> createCompleteRequest(
            @RequestParam Long carId,
            @RequestParam Long customerId,
            @RequestParam Long serviceCenterId,
            @RequestParam String description) {
        return ResponseEntity.ok(requestService.createRequest(
                carId, customerId, serviceCenterId, description));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ServiceRequest> updateStatus(
            @PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(requestService.updateStatus(id, status));
    }
}