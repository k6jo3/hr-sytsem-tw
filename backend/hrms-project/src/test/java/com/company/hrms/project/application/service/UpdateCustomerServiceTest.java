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
import com.company.hrms.project.api.request.UpdateCustomerRequest;
import com.company.hrms.project.api.response.UpdateCustomerResponse;
import com.company.hrms.project.domain.model.aggregate.Customer;
import com.company.hrms.project.domain.model.command.UpdateCustomerCommand;
import com.company.hrms.project.domain.model.valueobject.CustomerId;
import com.company.hrms.project.domain.repository.ICustomerRepository;

@ExtendWith(MockitoExtension.class)
public class UpdateCustomerServiceTest {

    @Mock
    private ICustomerRepository customerRepository;

    @InjectMocks
    private UpdateCustomerServiceImpl updateCustomerService;

    @Mock
    private Customer customer;

    private UpdateCustomerRequest request;
    private JWTModel currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new JWTModel();
        currentUser.setUserId("USER-001");

        request = new UpdateCustomerRequest();
        request.setCustomerId("CUST-001");
        request.setCustomerName("Updated Name");
        request.setTaxId("12345678");
        request.setIndustry("Technology");
        request.setEmail("updated@acme.com");
        request.setPhoneNumber("0987654321");
    }

    @Test
    void updateCustomer_ShouldSucceed() throws Exception {
        // Arrange
        when(customerRepository.findById(any(CustomerId.class))).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // Act
        UpdateCustomerResponse response = updateCustomerService.execCommand(request, currentUser);

        // Assert
        assertTrue(response.isSuccess());
        verify(customer).update(any(UpdateCustomerCommand.class));
        verify(customerRepository).save(customer);
    }
}
