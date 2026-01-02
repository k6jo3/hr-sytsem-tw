package com.company.hrms.project.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.project.api.request.GetCustomerDetailRequest;
import com.company.hrms.project.api.response.GetCustomerDetailResponse;
import com.company.hrms.project.domain.model.aggregate.Customer;
import com.company.hrms.project.domain.model.valueobject.CustomerId;
import com.company.hrms.project.domain.repository.ICustomerRepository;

import lombok.RequiredArgsConstructor;

@Service("getCustomerDetailServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetCustomerDetailServiceImpl
        implements QueryApiService<GetCustomerDetailRequest, GetCustomerDetailResponse> {

    private final ICustomerRepository customerRepository;

    @Override
    public GetCustomerDetailResponse getResponse(GetCustomerDetailRequest req, JWTModel currentUser, String... args)
            throws Exception {
        CustomerId customerId = new CustomerId(req.getCustomerId());
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + req.getCustomerId()));

        return GetCustomerDetailResponse.builder()
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
    }
}
