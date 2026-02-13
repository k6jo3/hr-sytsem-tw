package com.company.hrms.project.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.project.api.request.GetCustomerListRequest;
import com.company.hrms.project.api.response.CustomerListItemResponse;
import com.company.hrms.project.api.response.GetCustomerListResponse;
import com.company.hrms.project.application.service.assembler.CustomerQueryAssembler;
import com.company.hrms.project.domain.model.aggregate.Customer;
import com.company.hrms.project.domain.repository.ICustomerRepository;

import lombok.RequiredArgsConstructor;

@Service("getCustomerListServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetCustomerListServiceImpl implements QueryApiService<GetCustomerListRequest, GetCustomerListResponse> {

        private final ICustomerRepository customerRepository;
        private final CustomerQueryAssembler customerQueryAssembler = new CustomerQueryAssembler();

        @Override
        public GetCustomerListResponse getResponse(GetCustomerListRequest req, JWTModel currentUser, String... args)
                        throws Exception {
                // 使用 Assembler 統一查詢條件 (包含與合約測試一致的邏輯)
                QueryGroup query = customerQueryAssembler.toQueryGroup(req);

                // Build Pageable (Default sort by CustomerCode ASC)
                Pageable pageable = PageRequest.of(req.getPage(), req.getSize(),
                                Sort.by(Sort.Direction.ASC, "customerCode"));

                // Execute Query
                Page<Customer> pageResult = customerRepository.findCustomers(query, pageable);

                // Map to Response
                List<CustomerListItemResponse> items = pageResult.getContent().stream()
                                .map(this::toDto)
                                .collect(Collectors.toList());

                return GetCustomerListResponse.builder()
                                .items(items)
                                .total(pageResult.getTotalElements())
                                .page(pageResult.getNumber())
                                .size(pageResult.getSize())
                                .totalPages(pageResult.getTotalPages())
                                .build();
        }

        private CustomerListItemResponse toDto(Customer customer) {
                return CustomerListItemResponse.builder()
                                .customerId(customer.getId().getValue())
                                .customerCode(customer.getCustomerCode())
                                .customerName(customer.getCustomerName())
                                .taxId(customer.getTaxId())
                                .industry(customer.getIndustry())
                                .email(customer.getEmail())
                                .phoneNumber(customer.getPhoneNumber())
                                .status(customer.getStatus().name())
                                .projectCount(customer.getProjectCount())
                                .build();
        }
}
