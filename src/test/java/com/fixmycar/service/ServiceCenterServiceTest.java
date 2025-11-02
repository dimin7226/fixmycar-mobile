package com.fixmycar.service;

import com.fixmycar.cache.InMemoryCache;
import com.fixmycar.exception.ResourceNotFoundException;
import com.fixmycar.model.ServiceCenter;
import com.fixmycar.repository.ServiceCenterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceCenterServiceTest {

    @Mock
    private ServiceCenterRepository repository;

    @Mock
    private InMemoryCache<Long, ServiceCenter> cache;

    @InjectMocks
    private ServiceCenterService service;

    private final ServiceCenter sc = new ServiceCenter(1L, "FixIt", "Main St", "12345");

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllServiceCenters_returnsList() {
        when(repository.findAll()).thenReturn(List.of(sc));
        assertEquals(1, service.getAllServiceCenters().size());
    }

    @Test
    void getServiceCenterById_fromCache() {
        when(cache.get(1L)).thenReturn(sc);
        var result = service.getServiceCenterById(1L);
        assertTrue(result.isPresent());
        verify(repository, never()).findById(any());
    }

    @Test
    void getServiceCenterById_fromRepository() {
        when(cache.get(1L)).thenReturn(null);
        when(repository.findById(1L)).thenReturn(Optional.of(sc));

        var result = service.getServiceCenterById(1L);

        assertTrue(result.isPresent());
        //   verify(cache).put(eq(1L), any(ServiceCenter.class));
    }

    @Test
    void saveServiceCenter_savesAndCaches() {
        when(repository.save(sc)).thenReturn(sc);
        var result = service.saveServiceCenter(sc);
        assertEquals(sc, result);
        verify(cache).put(sc.getId(), sc);
    }

    @Test
    void updateServiceCenter_updatesFields() {
        ServiceCenter updated = new ServiceCenter(null, "New Name", "New Addr", "9999");
        when(repository.findById(1L)).thenReturn(Optional.of(sc));
        when(repository.save(any())).thenReturn(sc);
        var result = service.updateServiceCenter(1L, updated);
        assertEquals("New Name", result.getName());
        verify(cache).put(sc.getId(), sc);
    }

    @Test
    void updateServiceCenter_notFound() {
        when(repository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.updateServiceCenter(2L, sc));
    }

    @Test
    void deleteServiceCenter_removesAndEvicts() {
        service.deleteServiceCenter(1L);
        verify(repository).deleteById(1L);
        verify(cache).evict(1L);
    }
}
