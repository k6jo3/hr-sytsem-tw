package com.company.hrms.project.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.project.api.request.UpdateCustomerRequest;
import com.company.hrms.project.api.response.UpdateCustomerResponse;
import com.company.hrms.project.domain.model.aggregate.Customer;
import com.company.hrms.project.domain.model.command.UpdateCustomerCommand;
import com.company.hrms.project.domain.model.valueobject.CustomerId;
import com.company.hrms.project.domain.repository.ICustomerRepository;

import lombok.RequiredArgsConstructor;

@Service("updateCustomerServiceImpl")
@RequiredArgsConstructor
@Transactional
public class UpdateCustomerServiceImpl implements CommandApiService<UpdateCustomerRequest, UpdateCustomerResponse> {

    private final ICustomerRepository customerRepository;

    @Override
    public UpdateCustomerResponse execCommand(UpdateCustomerRequest req, JWTModel currentUser, String... args)
            throws Exception {
        if (req.getCustomerId() == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }

        Customer customer = customerRepository.findById(new CustomerId(req.getCustomerId()))
                .orElseThrow(() -> new DomainException("Customer not found: " + req.getCustomerId()));

        UpdateCustomerCommand cmd = req.toCommand();
        customer.update(cmd);

        customerRepository.save(customer);

        return new UpdateCustomerResponse(true);
    }
}
