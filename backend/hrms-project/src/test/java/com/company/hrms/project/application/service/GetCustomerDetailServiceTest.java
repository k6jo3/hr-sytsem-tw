package com.company.hrms.project.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.project.api.request.GetCustomerDetailRequest;
import com.company.hrms.project.api.response.GetCustomerDetailResponse;
import com.company.hrms.project.domain.model.aggregate.Customer;
import com.company.hrms.project.domain.model.valueobject.CustomerId;
import com.company.hrms.project.domain.model.valueobject.CustomerStatus;
import com.company.hrms.project.domain.repository.ICustomerRepository;

@ExtendWith(MockitoExtension.class)
public class GetCustomerDetailServiceTest {

    @Mock
    private ICustomerRepository customerRepository;

    @InjectMocks
    private GetCustomerDetailServiceImpl getCustomerDetailService;

    private GetCustomerDetailRequest request;
    private JWTModel currentUser;
    private static final String CUSTOMER_ID = "CUST-001";

    @BeforeEach
    void setUp() {
        currentUser = new JWTModel();
        currentUser.setUserId("USER-001");

        request = new GetCustomerDetailRequest();
        request.setCustomerId(CUSTOMER_ID);
    }

    @Test
    void getCustomerDetail_ShouldReturnData() throws Exception {
        // Arrange
        Customer customer = mock(Customer.class);
        when(customer.getId()).thenReturn(new CustomerId(CUSTOMER_ID));
        when(customer.getCustomerCode()).thenReturn("C001");
        when(customer.getCustomerName()).thenReturn("Tech Corp");
        when(customer.getTaxId()).thenReturn("12345678");
        when(customer.getIndustry()).thenReturn("IT");
        when(customer.getEmail()).thenReturn("info@tech.com");
        when(customer.getPhoneNumber()).thenReturn("02-1234-5678");
        when(customer.getStatus()).thenReturn(CustomerStatus.ACTIVE);

        when(customerRepository.findById(any(CustomerId.class))).thenReturn(Optional.of(customer));

        // Act
        GetCustomerDetailResponse response = getCustomerDetailService.getResponse(request, currentUser);

        // Assert
        assertNotNull(response);
        assertEquals(CUSTOMER_ID, response.getCustomerId());
        assertEquals("C001", response.getCustomerCode());
        assertEquals("Tech Corp", response.getCustomerName());
        assertEquals("ACTIVE", response.getStatus());

        verify(customerRepository).findById(any(CustomerId.class));
    }
}
