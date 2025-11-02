package com.fixmycar.controller;

import com.fixmycar.exception.ResourceNotFoundException;
import com.fixmycar.model.ServiceCenter;
import com.fixmycar.service.ServiceCenterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceCenterControllerTest {

    private ServiceCenterService serviceCenterService;
    private ServiceCenterController controller;

    private final ServiceCenter mockCenter = new ServiceCenter();

    @BeforeEach
    void setUp() {
        serviceCenterService = mock(ServiceCenterService.class);
        controller = new ServiceCenterController(serviceCenterService);

        mockCenter.setId(1L);
        mockCenter.setName("Test Center");
        mockCenter.setAddress("123 Street");
        mockCenter.setPhone("+123456789");
    }

    @Test
    void getAllServiceCenters_ShouldReturnList() {
        List<ServiceCenter> list = List.of(mockCenter);
        when(serviceCenterService.getAllServiceCenters()).thenReturn(list);

        List<ServiceCenter> result = controller.getAllServiceCenters();

        assertEquals(1, result.size());
        assertEquals("Test Center", result.get(0).getName());
        verify(serviceCenterService, times(1)).getAllServiceCenters();
    }

    @Test
    void getServiceCenterById_ShouldReturnEntity() {
        when(serviceCenterService.getServiceCenterById(1L))
                .thenReturn(Optional.of(mockCenter));

        ResponseEntity<ServiceCenter> response = controller.getServiceCenterById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockCenter, response.getBody());
    }

    @Test
    void getServiceCenterById_ShouldThrow_WhenNotFound() {
        when(serviceCenterService.getServiceCenterById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                controller.getServiceCenterById(99L));
    }

    @Test
    void createServiceCenter_ShouldReturnCreatedEntity() {
        when(serviceCenterService.saveServiceCenter(any(ServiceCenter.class)))
                .thenReturn(mockCenter);

        ResponseEntity<ServiceCenter> response = controller.createServiceCenter(mockCenter);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockCenter, response.getBody());
        verify(serviceCenterService).saveServiceCenter(mockCenter);
    }

    @Test
    void updateServiceCenter_ShouldUpdateAndReturnEntity() {
        ServiceCenter updatedDetails = new ServiceCenter();
        updatedDetails.setName("Updated Name");
        updatedDetails.setAddress("New Address");
        updatedDetails.setPhone("+987654321");

        when(serviceCenterService.getServiceCenterById(1L))
                .thenReturn(Optional.of(mockCenter));
        when(serviceCenterService.saveServiceCenter(any(ServiceCenter.class)))
                .thenReturn(mockCenter);

        ResponseEntity<ServiceCenter> response = controller.updateServiceCenter(1L, updatedDetails);

        assertEquals(200, response.getStatusCodeValue());
        verify(serviceCenterService).saveServiceCenter(mockCenter);
        assertEquals("Updated Name", mockCenter.getName());
    }

    @Test
    void updateServiceCenter_ShouldThrow_WhenNotFound() {
        when(serviceCenterService.getServiceCenterById(2L)).thenReturn(Optional.empty());

        ServiceCenter newDetails = new ServiceCenter();
        newDetails.setName("Nope");

        assertThrows(ResourceNotFoundException.class, () ->
                controller.updateServiceCenter(2L, newDetails));
    }

    @Test
    void deleteServiceCenter_ShouldReturnNoContent() {
        doNothing().when(serviceCenterService).deleteServiceCenter(1L);

        ResponseEntity<Void> response = controller.deleteServiceCenter(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(serviceCenterService).deleteServiceCenter(1L);
    }
}
