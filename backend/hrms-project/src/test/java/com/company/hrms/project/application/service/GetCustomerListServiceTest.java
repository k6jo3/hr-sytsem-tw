package com.company.hrms.project.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.project.api.request.GetCustomerListRequest;
import com.company.hrms.project.api.response.GetCustomerListResponse;
import com.company.hrms.project.domain.model.aggregate.Customer;
import com.company.hrms.project.domain.model.valueobject.CustomerId;
import com.company.hrms.project.domain.model.valueobject.CustomerStatus;
import com.company.hrms.project.domain.repository.ICustomerRepository;

@ExtendWith(MockitoExtension.class)
public class GetCustomerListServiceTest {

    @Mock
    private ICustomerRepository customerRepository;

    @InjectMocks
    private GetCustomerListServiceImpl getCustomerListService;

    private GetCustomerListRequest request;
    private JWTModel currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new JWTModel();
        currentUser.setUserId("USER-001");

        request = new GetCustomerListRequest();
        request.setPage(0);
        request.setSize(10);
        request.setKeyword("Tech");
    }

    @Test
    void getCustomerList_ShouldReturnData() throws Exception {
        // Arrange
        Customer customer = mock(Customer.class);
        when(customer.getId()).thenReturn(new CustomerId(UUID.randomUUID().toString()));
        when(customer.getCustomerCode()).thenReturn("C001");
        when(customer.getCustomerName()).thenReturn("Tech Corp");
        when(customer.getStatus()).thenReturn(CustomerStatus.ACTIVE);
        when(customer.getPhoneNumber()).thenReturn("123");

        Page<Customer> pageResult = new PageImpl<>(Collections.singletonList(customer));
        when(customerRepository.findCustomers(any(QueryGroup.class), any(Pageable.class))).thenReturn(pageResult);

        // Act
        GetCustomerListResponse response = getCustomerListService.getResponse(request, currentUser);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotal());
        assertEquals("C001", response.getItems().get(0).getCustomerCode());

        verify(customerRepository).findCustomers(any(QueryGroup.class), any(Pageable.class));
    }
}
