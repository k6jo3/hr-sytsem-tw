package com.company.hrms.project.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.project.api.response.GetCustomerDetailResponse;
import com.company.hrms.project.application.service.context.CustomerDetailContext;

/**
 * 建構客戶詳情回應 Task
 */
@Component
public class BuildCustomerDetailResponseTask implements PipelineTask<CustomerDetailContext> {

    @Override
    public void execute(CustomerDetailContext context) throws Exception {
        var customer = context.getCustomer();

        GetCustomerDetailResponse response = GetCustomerDetailResponse.builder()
                .customerId(customer.getId().getValue())
                .customerCode(customer.getCustomerCode())
                .customerName(customer.getCustomerName())
                .taxId(customer.getTaxId())
                .industry(customer.getIndustry())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .status(customer.getStatus().name())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();

        context.setResponse(response);
    }
}
