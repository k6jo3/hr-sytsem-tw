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
import com.company.hrms.project.domain.model.aggregate.Customer;
import com.company.hrms.project.domain.repository.ICustomerRepository;

import lombok.RequiredArgsConstructor;

@Service("getCustomerListServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetCustomerListServiceImpl implements QueryApiService<GetCustomerListRequest, GetCustomerListResponse> {

        private final ICustomerRepository customerRepository;

        @Override
        public GetCustomerListResponse getResponse(GetCustomerListRequest req, JWTModel currentUser, String... args)
                        throws Exception {
                // TODO: 未符合Fluent-Query-Engine
                // Build QueryGroup
                QueryGroup query = QueryGroup.and();

                if (req.getKeyword() != null && !req.getKeyword().isEmpty()) {
                        query.addSubGroup(
                                        QueryGroup.or()
                                                        .like("customerName", "%" + req.getKeyword() + "%")
                                                        .like("customerCode", "%" + req.getKeyword() + "%")
                                                        .like("taxId", "%" + req.getKeyword() + "%"));
                }

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
                                .build();
        }
}
