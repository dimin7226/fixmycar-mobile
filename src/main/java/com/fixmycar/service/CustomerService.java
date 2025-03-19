package com.fixmycar.service;

import com.fixmycar.model.Customer;
import com.fixmycar.model.Car;
import com.fixmycar.model.ServiceRequest;
import com.fixmycar.repository.CustomerRepository;
import com.fixmycar.repository.CarRepository;
import com.fixmycar.repository.ServiceRequestRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CarRepository carRepository;
    private final ServiceRequestRepository serviceRequestRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));
    }

    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Long id, Customer customerDetails) {
        Customer customer = getCustomerById(id);

        customer.setFirstName(customerDetails.getFirstName());
        customer.setLastName(customerDetails.getLastName());
        customer.setEmail(customerDetails.getEmail());
        customer.setPhone(customerDetails.getPhone());

        return customerRepository.save(customer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<ServiceRequest> serviceRequests = customer.getServiceRequests();
        if (serviceRequests != null && !serviceRequests.isEmpty()) {
            serviceRequestRepository.deleteAll(serviceRequests);
        }

        List<Car> cars = customer.getCars();
        if (cars != null && !cars.isEmpty()) {
            for (Car car : cars) {
                // Удаляем все заявки, связанные с автомобилем
                List<ServiceRequest> carServiceRequests = car.getServiceRequests();
                if (carServiceRequests != null && !carServiceRequests.isEmpty()) {
                    serviceRequestRepository.deleteAll(carServiceRequests);
                }
                // Удаляем автомобиль
                carRepository.delete(car);
            }
        }
        customerRepository.delete(customer);
    }

    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));
    }
}