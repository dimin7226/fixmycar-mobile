package com.fixmycar.controller;

import com.fixmycar.exception.BadRequestException;
import com.fixmycar.exception.ResourceNotFoundException;
import com.fixmycar.model.Customer;
import com.fixmycar.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private Customer createCustomer() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john@example.com");
        customer.setPhone("+123456789");
        return customer;
    }

    @Test
    void getAllCustomers_ShouldReturnAll() {
        List<Customer> customers = Arrays.asList(new Customer(), new Customer());
        when(customerService.getAllCustomers()).thenReturn(customers);

        List<Customer> result = customerController.getAllCustomers();

        assertEquals(2, result.size());
    }

    @Test
    void getCustomerById_ShouldReturnCustomer() {
        Customer customer = createCustomer();
        when(customerService.getCustomerById(1L)).thenReturn(Optional.of(customer));

        ResponseEntity<Customer> response = customerController.getCustomerById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void getCustomerById_ShouldThrow_WhenNotFound() {
        when(customerService.getCustomerById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerController.getCustomerById(1L));
    }

    @Test
    void createCustomer_ShouldReturnCreatedCustomer() {
        Customer customer = createCustomer();

        when(customerService.existsByEmail("john@example.com")).thenReturn(false);
        when(customerService.existsByPhone("+123456789")).thenReturn(false);
        when(customerService.saveOrUpdateCustomer(customer)).thenReturn(customer);

        ResponseEntity<Customer> response = customerController.createCustomer(customer);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("john@example.com", response.getBody().getEmail());
    }

    @Test
    void createCustomer_ShouldThrow_WhenEmailExists() {
        Customer customer = createCustomer();
        when(customerService.existsByEmail("john@example.com")).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> customerController.createCustomer(customer));

        assertEquals("Email already exists", ex.getMessage());
    }

    @Test
    void createCustomer_ShouldThrow_WhenPhoneExists() {
        Customer customer = createCustomer();

        when(customerService.existsByEmail("john@example.com")).thenReturn(false);
        when(customerService.existsByPhone("+123456789")).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> customerController.createCustomer(customer));

        assertEquals("Phone number already exists", ex.getMessage());
    }

    @Test
    void updateCustomer_ShouldUpdateAndReturnCustomer() {
        Customer updateData = createCustomer();
        updateData.setEmail("new@example.com");
        updateData.setPhone("+987654321");

        Customer existing = createCustomer();

        when(customerService.getCustomerById(1L)).thenReturn(Optional.of(existing));
        when(customerService.existsByEmailAndIdNot("new@example.com", 1L)).thenReturn(false);
        when(customerService.existsByPhoneAndIdNot("+987654321", 1L)).thenReturn(false);
        when(customerService.saveOrUpdateCustomer(any(Customer.class))).thenReturn(existing);

        ResponseEntity<Customer> response = customerController.updateCustomer(1L, updateData);

        assertEquals(200, response.getStatusCodeValue());
        verify(customerService).saveOrUpdateCustomer(existing);
        assertEquals("new@example.com", existing.getEmail());
        assertEquals("+987654321", existing.getPhone());
    }

    @Test
    void updateCustomer_ShouldThrow_WhenCustomerNotFound() {
        Customer customer = createCustomer();

        when(customerService.getCustomerById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerController.updateCustomer(1L, customer));
    }

    @Test
    void updateCustomer_ShouldThrow_WhenEmailExists() {
        Customer customer = createCustomer();

        when(customerService.getCustomerById(1L)).thenReturn(Optional.of(customer));
        when(customerService.existsByEmailAndIdNot("john@example.com", 1L)).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> customerController.updateCustomer(1L, customer));

        assertEquals("Email already exists", ex.getMessage());
    }

    @Test
    void updateCustomer_ShouldThrow_WhenPhoneExists() {
        Customer customer = createCustomer();

        when(customerService.getCustomerById(1L)).thenReturn(Optional.of(customer));
        when(customerService.existsByEmailAndIdNot("john@example.com", 1L)).thenReturn(false);
        when(customerService.existsByPhoneAndIdNot("+123456789", 1L)).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> customerController.updateCustomer(1L, customer));

        assertEquals("Phone number already exists", ex.getMessage());
    }

    @Test
    void deleteCustomer_ShouldReturnNoContent() {
        doNothing().when(customerService).deleteCustomer(1L);

        ResponseEntity<Void> response = customerController.deleteCustomer(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(customerService).deleteCustomer(1L);
    }
}
