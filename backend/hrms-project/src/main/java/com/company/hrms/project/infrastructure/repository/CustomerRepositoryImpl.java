package com.company.hrms.project.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.CommandBatchBaseRepository;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.project.domain.model.aggregate.Customer;
import com.company.hrms.project.domain.model.valueobject.CustomerId;
import com.company.hrms.project.domain.repository.ICustomerRepository;
import com.company.hrms.project.infrastructure.entity.CustomerEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class CustomerRepositoryImpl extends CommandBatchBaseRepository<CustomerEntity, String>
        implements ICustomerRepository {

    public CustomerRepositoryImpl(JPAQueryFactory factory) {
        super(factory, CustomerEntity.class);
    }

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = toEntity(customer);
        super.save(entity);
        return customer;
    }

    @Override
    public Optional<Customer> findById(CustomerId id) {
        return super.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public List<Customer> findAll() {
        return super.findAll(new QueryGroup()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Customer> findCustomers(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable).map(this::toDomain);
    }

    // ================= Mapper =================

    private CustomerEntity toEntity(Customer domain) {
        return CustomerEntity.builder()
                .customerId(domain.getId().getValue())
                .customerCode(domain.getCustomerCode())
                .customerName(domain.getCustomerName())
                .taxId(domain.getTaxId())
                .industry(domain.getIndustry())
                .email(domain.getEmail())
                .phoneNumber(domain.getPhoneNumber())
                .status(domain.getStatus())
                .version(domain.getVersion())
                .build();
    }

    private Customer toDomain(CustomerEntity entity) {
        return Customer.reconstitute(
                new CustomerId(entity.getCustomerId()),
                entity.getCustomerCode(),
                entity.getCustomerName(),
                entity.getTaxId(),
                entity.getIndustry(),
                entity.getEmail(),
                entity.getPhoneNumber(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion());
    }
}
