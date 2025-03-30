package com.fixmycar.service;

import com.fixmycar.cache.InMemoryCache;
import com.fixmycar.exception.ResourceNotFoundException;
import com.fixmycar.model.Car;
import com.fixmycar.model.Customer;
import com.fixmycar.model.ServiceRequest;
import com.fixmycar.repository.CarRepository;
import com.fixmycar.repository.CustomerRepository;
import com.fixmycar.repository.ServiceRequestRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CarRepository carRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final InMemoryCache<Long, Customer> customerCache;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(Long id) {

        Customer cachedCustomer = customerCache.get(id);
        if (cachedCustomer != null) {
            return Optional.of(cachedCustomer);
        }
        Optional<Customer> customer = customerRepository.findById(id);
        customer.ifPresent(acc -> customerCache.put(id, acc));

        return customer;
    }

    public Customer saveOrUpdateCustomer(Customer customer) {
        Customer savedCustomer = customerRepository.save(customer);

        customerCache.put(savedCustomer.getId(), savedCustomer);
        return savedCustomer;
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);

        customerCache.evict(id);
    }
}