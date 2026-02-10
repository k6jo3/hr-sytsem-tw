package com.company.hrms.project.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.project.api.request.CreateCustomerRequest;
import com.company.hrms.project.api.response.CreateCustomerResponse;
import com.company.hrms.project.domain.model.aggregate.Customer;
import com.company.hrms.project.domain.repository.ICustomerRepository;

@ExtendWith(MockitoExtension.class)
public class CreateCustomerServiceTest {

    @Mock
    private ICustomerRepository customerRepository;

    @Mock
    private com.company.hrms.common.domain.event.EventPublisher eventPublisher;

    @InjectMocks
    private CreateCustomerServiceImpl createCustomerService;

    private CreateCustomerRequest request;
    private JWTModel currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new JWTModel();
        currentUser.setUserId("USER-001");
        currentUser.setUsername("Admin User");

        request = new CreateCustomerRequest();
        request.setCustomerCode("CUST-001");
        request.setCustomerName("Test Customer");
        request.setTaxId("12345678");
        request.setIndustry("Technology");
        request.setEmail("contact@test.com");
        request.setPhoneNumber("0912345678");
    }

    @Test
    void createCustomer_ShouldSucceed() throws Exception {
        // Arrange
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CreateCustomerResponse response = createCustomerService.execCommand(request, currentUser);

        // Assert
        assertNotNull(response.getCustomerId());
        verify(customerRepository).save(any(Customer.class));
    }
}
