package com.fixmycar.dao;

import com.fixmycar.model.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerDao {

    List<Customer> findAll();

    Optional<Customer> findById(Long id);

    Optional<Customer> findByEmail(String email);

    Customer save(Customer customer);

    void delete(Customer customer);

    void deleteById(Long id);
}