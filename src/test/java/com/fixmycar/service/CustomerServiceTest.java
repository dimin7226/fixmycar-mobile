package com.fixmycar.service;

import com.fixmycar.cache.InMemoryCache;
import com.fixmycar.model.Customer;
import com.fixmycar.repository.CarRepository;
import com.fixmycar.repository.CustomerRepository;
import com.fixmycar.repository.ServiceRequestRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock private CustomerRepository customerRepository;
    @Mock private CarRepository carRepository;
    @Mock private ServiceRequestRepository serviceRequestRepository;
    @Mock private InMemoryCache<Long, Customer> customerCache;

    @InjectMocks private CustomerService customerService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllCustomers_shouldReturnList() {
        List<Customer> customers = List.of(new Customer(), new Customer());
        when(customerRepository.findAll()).thenReturn(customers);

        List<Customer> result = customerService.getAllCustomers();

        assertThat(result).hasSize(2);
        verify(customerRepository).findAll();
    }

    @Test
    void getCustomerById_shouldReturnFromCacheIfExists() {
        Customer customer = new Customer();
        customer.setId(1L);
        when(customerCache.get(1L)).thenReturn(customer);

        Optional<Customer> result = customerService.getCustomerById(1L);

        assertThat(result).contains(customer);
        verify(customerCache).get(1L);
        verifyNoInteractions(customerRepository);
    }

    @Test
    void getCustomerById_shouldReturnFromRepositoryIfNotInCache() {
        Customer customer = new Customer();
        customer.setId(1L);
        when(customerCache.get(1L)).thenReturn(null);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Optional<Customer> result = customerService.getCustomerById(1L);

        assertThat(result).contains(customer);
        verify(customerRepository).findById(1L);
        verify(customerCache).put(1L, customer);
    }

    @Test
    void saveOrUpdateCustomer_shouldSaveAndCache() {
        Customer customer = new Customer();
        customer.setId(7L);
        when(customerRepository.save(customer)).thenReturn(customer);

        Customer result = customerService.saveOrUpdateCustomer(customer);

        assertThat(result).isEqualTo(customer);
        verify(customerRepository).save(customer);
        verify(customerCache).put(7L, customer);
    }

    @Test
    void existsByEmail_shouldReturnTrueIfExists() {
        when(customerRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean result = customerService.existsByEmail("test@example.com");

        assertThat(result).isTrue();
        verify(customerRepository).existsByEmail("test@example.com");
    }

    @Test
    void existsByPhone_shouldReturnTrueIfExists() {
        when(customerRepository.existsByPhone("+1234567890")).thenReturn(true);

        boolean result = customerService.existsByPhone("+1234567890");

        assertThat(result).isTrue();
        verify(customerRepository).existsByPhone("+1234567890");
    }

    @Test
    void existsByEmailAndIdNot_shouldReturnTrueIfExists() {
        when(customerRepository.existsByEmailAndIdNot("abc@test.com", 3L)).thenReturn(true);

        boolean result = customerService.existsByEmailAndIdNot("abc@test.com", 3L);

        assertThat(result).isTrue();
        verify(customerRepository).existsByEmailAndIdNot("abc@test.com", 3L);
    }

    @Test
    void existsByPhoneAndIdNot_shouldReturnTrueIfExists() {
        when(customerRepository.existsByPhoneAndIdNot("+111222333", 5L)).thenReturn(true);

        boolean result = customerService.existsByPhoneAndIdNot("+111222333", 5L);

        assertThat(result).isTrue();
        verify(customerRepository).existsByPhoneAndIdNot("+111222333", 5L);
    }

    @Test
    void deleteCustomer_shouldDeleteFromRepoAndEvictCache() {
        customerService.deleteCustomer(9L);

        verify(customerRepository).deleteById(9L);
        verify(customerCache).evict(9L);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}
