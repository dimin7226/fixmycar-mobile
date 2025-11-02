package com.fixmycar.controller;

import com.fixmycar.exception.ResourceNotFoundException;
import com.fixmycar.model.Car;
import com.fixmycar.model.Customer;
import com.fixmycar.model.ServiceCenter;
import com.fixmycar.model.ServiceRequest;
import com.fixmycar.repository.ServiceRequestRepository;
import com.fixmycar.service.ServiceRequestService;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceRequestControllerTest {

    private ServiceRequestService requestService;
    private ServiceRequestController controller;
    @Mock
    private ServiceRequestRepository requestRepo;
    private final ServiceRequest mockRequest = new ServiceRequest();

    @BeforeEach
    void setUp() {
        requestService = mock(ServiceRequestService.class);
        controller = new ServiceRequestController(requestService);

        mockRequest.setId(1L);
        mockRequest.setDescription("Engine Repair");
        mockRequest.setStatus("Pending");
        // Setup other properties if needed
    }

    @Test
    void getAllRequests_ShouldReturnList() {
        List<ServiceRequest> list = List.of(mockRequest);
        when(requestService.getAllRequests()).thenReturn(list);

        List<ServiceRequest> result = controller.getAllRequests();

        assertEquals(1, result.size());
        assertEquals("Engine Repair", result.get(0).getDescription());
        verify(requestService, times(1)).getAllRequests();
    }

    @Test
    void getRequestById_ShouldReturnEntity() {
        when(requestService.getRequestById(1L))
                .thenReturn(Optional.of(mockRequest));

        ResponseEntity<ServiceRequest> response = controller.getRequestById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockRequest, response.getBody());
    }

    @Test
    void getRequestById_ShouldThrow_WhenNotFound() {
        when(requestService.getRequestById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                controller.getRequestById(99L));
    }

    @Test
    void createRequest_ShouldReturnCreatedEntity() {
        when(requestService.createServiceRequest(1L, 1L, 1L, "Engine Repair"))
                .thenReturn(mockRequest);

        ResponseEntity<ServiceRequest> response = controller.createRequest(1L, 1L, 1L, "Engine Repair");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockRequest, response.getBody());
        verify(requestService).createServiceRequest(1L, 1L, 1L, "Engine Repair");
    }

    @Test
    void createRequest_ShouldThrow_WhenValidationFails() {
        assertThrows(ValidationException.class, () ->
                controller.createRequest(null, 1L, 1L, "Engine Repair"));
    }

        @Test
    void updateRequest_ShouldUpdateAndReturnEntity() {
        ServiceRequest updatedRequest = new ServiceRequest();
        updatedRequest.setDescription("Updated Repair");
        updatedRequest.setStatus("In Progress");

        when(requestService.getRequestById(1L))
                .thenReturn(Optional.of(mockRequest));
        when(requestService.saveRequest(ArgumentMatchers.any(ServiceRequest.class)))
                .thenReturn(mockRequest);

        ResponseEntity<ServiceRequest> response = controller.updateRequest(1L, updatedRequest);

        assertEquals(200, response.getStatusCodeValue());
        verify(requestService).saveRequest(mockRequest);
        assertEquals("Updated Repair", mockRequest.getDescription());
    }

    @Test
    void updateRequest_ShouldThrow_WhenNotFound() {
        when(requestService.getRequestById(99L)).thenReturn(Optional.empty());

        ServiceRequest updatedDetails = new ServiceRequest();
        updatedDetails.setDescription("New Repair");

        assertThrows(ResourceNotFoundException.class, () ->
                controller.updateRequest(99L, updatedDetails));
    }

    @Test
    void updateStatus_ShouldReturnUpdatedEntity() {
        when(requestService.updateStatus(1L, "Completed"))
                .thenReturn(mockRequest);

        ResponseEntity<ServiceRequest> response = controller.updateStatus(1L, "Completed");

        assertEquals(200, response.getStatusCodeValue());
        verify(requestService).updateStatus(1L, "Completed");
    }

    @Test
    void deleteRequest_ShouldReturnNoContent() {
        doNothing().when(requestService).deleteRequest(1L);

        ResponseEntity<Void> response = controller.deleteRequest(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(requestService).deleteRequest(1L);
    }

    @Test
    void getRequestsByCustomerId_ShouldReturnList() {
        List<ServiceRequest> list = List.of(mockRequest);
        when(requestService.getRequestsByCustomerId(1L)).thenReturn(list);

        List<ServiceRequest> result = controller.getRequestsByCustomerId(1L);

        assertEquals(1, result.size());
        assertEquals("Engine Repair", result.get(0).getDescription());
        verify(requestService, times(1)).getRequestsByCustomerId(1L);
    }

    @Test
    void getRequestsByCarId_ShouldReturnList() {
        List<ServiceRequest> list = List.of(mockRequest);
        when(requestService.getRequestsByCarId(1L)).thenReturn(list);

        List<ServiceRequest> result = controller.getRequestsByCarId(1L);

        assertEquals(1, result.size());
        assertEquals("Engine Repair", result.get(0).getDescription());
        verify(requestService, times(1)).getRequestsByCarId(1L);
    }

    @Test
    void getRequestsByServiceCenterId_ShouldReturnList() {
        List<ServiceRequest> list = List.of(mockRequest);
        when(requestService.getRequestsByServiceCenterId(1L)).thenReturn(list);

        List<ServiceRequest> result = controller.getRequestsByServiceCenterId(1L);

        assertEquals(1, result.size());
        assertEquals("Engine Repair", result.get(0).getDescription());
        verify(requestService, times(1)).getRequestsByServiceCenterId(1L);
    }
}
