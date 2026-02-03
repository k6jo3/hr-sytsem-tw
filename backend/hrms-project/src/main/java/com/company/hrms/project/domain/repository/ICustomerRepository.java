package com.company.hrms.project.domain.repository;

import java.util.List;
import java.util.Optional;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.project.domain.model.aggregate.Customer;
import com.company.hrms.project.domain.model.valueobject.CustomerId;

public interface ICustomerRepository {
    Customer save(Customer customer);

    Optional<Customer> findById(CustomerId id);

    List<Customer> findAll();

    org.springframework.data.domain.Page<Customer> findCustomers(QueryGroup query,
            org.springframework.data.domain.Pageable pageable);

    boolean existsByCustomerCode(String customerCode);

    boolean existsByTaxId(String taxId);
}
