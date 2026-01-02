package com.company.hrms.project.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.project.application.service.context.CustomerDetailContext;
import com.company.hrms.project.domain.model.valueobject.CustomerId;
import com.company.hrms.project.domain.repository.ICustomerRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入客戶 Task
 */
@Component
@RequiredArgsConstructor
public class LoadCustomerTask implements PipelineTask<CustomerDetailContext> {

    private final ICustomerRepository customerRepository;

    @Override
    public void execute(CustomerDetailContext context) throws Exception {
        CustomerId customerId = new CustomerId(context.getCustomerId());
        var customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + context.getCustomerId()));

        context.setCustomer(customer);
    }
}
