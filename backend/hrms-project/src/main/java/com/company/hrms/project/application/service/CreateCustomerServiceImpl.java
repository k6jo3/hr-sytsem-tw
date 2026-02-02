package com.company.hrms.project.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.project.api.request.CreateCustomerRequest;
import com.company.hrms.project.api.response.CreateCustomerResponse;
import com.company.hrms.project.domain.model.aggregate.Customer;
import com.company.hrms.project.domain.model.command.CreateCustomerCommand;
import com.company.hrms.project.domain.repository.ICustomerRepository;

import lombok.RequiredArgsConstructor;

/**
 * 建立客戶服務實作
 */
@Service("createCustomerServiceImpl")
@RequiredArgsConstructor
@Transactional
public class CreateCustomerServiceImpl implements CommandApiService<CreateCustomerRequest, CreateCustomerResponse> {

    private final ICustomerRepository customerRepository;
    private final EventPublisher eventPublisher;

    @Override
    public CreateCustomerResponse execCommand(CreateCustomerRequest req, JWTModel currentUser, String... args)
            throws Exception {

        // 1. 唯一性檢查
        if (customerRepository.existsByCustomerCode(req.getCustomerCode())) {
            throw new IllegalArgumentException("客戶代碼已存在: " + req.getCustomerCode());
        }

        if (req.getTaxId() != null && !req.getTaxId().isBlank()) {
            if (customerRepository.existsByTaxId(req.getTaxId())) {
                throw new IllegalArgumentException("統一編號已存在: " + req.getTaxId());
            }
        }

        // 2. 建立領域對象
        CreateCustomerCommand cmd = req.toCommand();
        Customer customer = Customer.create(cmd);

        // 3. 持久化
        customerRepository.save(customer);

        // 4. 發布事件
        eventPublisher.publishAll(customer.getDomainEvents());
        customer.clearDomainEvents();

        return CreateCustomerResponse.builder()
                .customerId(customer.getId().getValue())
                .build();
    }
}
