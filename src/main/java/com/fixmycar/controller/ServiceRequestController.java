package com.fixmycar.controller;

import com.fixmycar.exception.ResourceNotFoundException;
import com.fixmycar.model.ServiceRequest;
import com.fixmycar.service.ServiceRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
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
@Tag(name = "Service Request Controller", description = "API для управления заявками на ремонт")
public class ServiceRequestController {
    private final ServiceRequestService requestService;
    private static final String REQUEST_NOT_FOUND_ID = "Request not found with id ";

    @GetMapping
    public List<ServiceRequest> getAllRequests() {
        return requestService.getAllRequests();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить заявку по ID",
            description = "Возвращает заявку по указанному ID")
    @ApiResponse(responseCode = "200", description = "Заявка найдена")
    @ApiResponse(responseCode = "404", description = "Заявка не найдена")
    public ResponseEntity<ServiceRequest> getRequestById(@PathVariable Long id) {
        ServiceRequest request = requestService.getRequestById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(REQUEST_NOT_FOUND_ID + id));
        return ResponseEntity.ok(request);
    }

    @GetMapping("/by-car-attributes")
    public List<ServiceRequest> getRequestsByCarAttributes(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Integer year) {
        return requestService.getRequestsByCarAttributes(brand, model, year);
    }

    @PostMapping
    @Operation(summary = "Создать заявку", description = "Создает новую заявку")
    @ApiResponse(responseCode = "200", description = "Заявка успешно создана")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    public ResponseEntity<ServiceRequest> createRequest(
            @RequestParam Long customerId,
            @RequestParam Long carId,
            @RequestParam Long serviceCenterId,
            @RequestParam String description) {

        if (customerId == null || carId == null || serviceCenterId == null) {
            throw new ValidationException("Customer, car, and service center are required");
        }

        ServiceRequest serviceRequest = requestService.createServiceRequest(customerId,
                carId, serviceCenterId, description);
        return ResponseEntity.ok(serviceRequest);
    }

    @PostMapping("/full")
    @Operation(summary = "Создать заявку из объекта",
            description = "Создает новую заявку из полного объекта")
    @ApiResponse(responseCode = "200", description = "Заявка успешно создана")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    public ResponseEntity<ServiceRequest> createFullRequest(
            @Valid @RequestBody ServiceRequest request) {
        if (request.getCustomer() == null || request.getCar() == null
                || request.getServiceCenter() == null) {
            throw new ValidationException("Customer, car, and service center are required");
        }
        ServiceRequest savedRequest = requestService.saveRequest(request);
        return ResponseEntity.ok(savedRequest);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные заявки",
            description = "Обновляет данные заявки по ID")
    @ApiResponse(responseCode = "200", description = "Данные заявки обновлены")
    @ApiResponse(responseCode = "404", description = "Заявка не найдена")
    public ResponseEntity<ServiceRequest> updateRequest(
            @PathVariable Long id, @Valid @RequestBody ServiceRequest requestDetails) {
        ServiceRequest existingRequest = requestService.getRequestById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(REQUEST_NOT_FOUND_ID + id));

        existingRequest.setDescription(requestDetails.getDescription());
        existingRequest.setStatus(requestDetails.getStatus());

        if (requestDetails.getCar() != null && requestDetails.getCar().getId() != null) {
            existingRequest.setCar(requestDetails.getCar());
        }
        if (requestDetails.getCustomer() != null && requestDetails.getCustomer().getId() != null) {
            existingRequest.setCustomer(requestDetails.getCustomer());
        }
        if (requestDetails.getServiceCenter() != null
                && requestDetails.getServiceCenter().getId() != null) {
            existingRequest.setServiceCenter(requestDetails.getServiceCenter());
        }

        ServiceRequest updatedRequest = requestService.saveRequest(existingRequest);
        return ResponseEntity.ok(updatedRequest);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Обновить статус заявки",
            description = "Обновляет статус заявки по ID")
    @ApiResponse(responseCode = "200", description = "Статус заявки обновлен")
    @ApiResponse(responseCode = "404", description = "Заявка не найдена")
    public ResponseEntity<ServiceRequest> updateStatus(
            @PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(requestService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить заявку", description = "Удаляет заявку по ID")
    @ApiResponse(responseCode = "204", description = "Заявка успешно удалена")
    @ApiResponse(responseCode = "404", description = "Заявка не найдена")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        requestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Получить заявки клиента",
            description = "Возвращает заявки по ID клиента")
    @ApiResponse(responseCode = "200", description = "Заявки найдены")
    public List<ServiceRequest> getRequestsByCustomerId(@PathVariable Long customerId) {
        return requestService.getRequestsByCustomerId(customerId);
    }

    @GetMapping("/car/{carId}")
    @Operation(summary = "Получить заявки по машине",
            description = "Возвращает заявки по ID машины")
    @ApiResponse(responseCode = "200", description = "Заявки найдены")
    public List<ServiceRequest> getRequestsByCarId(@PathVariable Long carId) {
        return requestService.getRequestsByCarId(carId);
    }

    @GetMapping("/service-center/{serviceCenterId}")
    @Operation(summary = "Получить заявки сервисного центра",
            description = "Возвращает заявки по ID сервисного центра")
    @ApiResponse(responseCode = "200", description = "Заявки найдены")
    public List<ServiceRequest> getRequestsByServiceCenterId(@PathVariable Long serviceCenterId) {
        return requestService.getRequestsByServiceCenterId(serviceCenterId);
    }
}