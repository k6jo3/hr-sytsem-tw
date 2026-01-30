package com.company.hrms.project.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.project.api.request.CreateCustomerRequest;
import com.company.hrms.project.api.response.CreateCustomerResponse;
import com.company.hrms.project.domain.model.aggregate.Customer;
import com.company.hrms.project.domain.model.command.CreateCustomerCommand;
import com.company.hrms.project.domain.repository.ICustomerRepository;

import lombok.RequiredArgsConstructor;

@Service("createCustomerServiceImpl")
@RequiredArgsConstructor
@Transactional
public class CreateCustomerServiceImpl implements CommandApiService<CreateCustomerRequest, CreateCustomerResponse> {

    private final ICustomerRepository customerRepository;

    @Override
    public CreateCustomerResponse execCommand(CreateCustomerRequest req, JWTModel currentUser, String... args)
            throws Exception {
        CreateCustomerCommand cmd = req.toCommand();

        Customer customer = Customer.create(cmd);
        customerRepository.save(customer);

        // TODO: 未實作邏輯
        // Customer domain model might publish event later but currently doesn't use
        // EventPublisher deeply yet
        // If we add EventPublisher, we should inject it here.

        return new CreateCustomerResponse(customer.getId().getValue());
    }
}
