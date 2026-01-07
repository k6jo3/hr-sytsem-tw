package com.company.hrms.project.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.project.api.request.GetCustomerDetailRequest;
import com.company.hrms.project.api.response.GetCustomerDetailResponse;
import com.company.hrms.project.application.service.task.BuildCustomerDetailResponseTask;
import com.company.hrms.project.application.service.task.LoadCustomerTask;
import com.company.hrms.project.domain.model.aggregate.Customer;
import com.company.hrms.project.domain.model.valueobject.CustomerId;
import com.company.hrms.project.domain.model.valueobject.CustomerStatus;
import com.company.hrms.project.domain.repository.ICustomerRepository;

/**
 * GetCustomerDetailServiceImpl 測試 - Business Pipeline 版本
 * 
 * 採用 "Business Assembly Test" 策略：
 * 1. 使用真實的 Pipeline Mock 對象 (LoadCustomerTask, BuildCustomerDetailResponseTask)
 * 2. Mock 底層 Repository
 * 3. 驗證 Pipeline 流程組裝與數據流轉正確性
 */
@ExtendWith(MockitoExtension.class)
public class GetCustomerDetailServiceTest {

    @Mock
    private ICustomerRepository customerRepository;

    private GetCustomerDetailServiceImpl getCustomerDetailService;

    private GetCustomerDetailRequest request;
    private JWTModel currentUser;
    private static final String CUSTOMER_ID = "CUST-001";

    @BeforeEach
    void setUp() {
        // 手動組裝 Pipeline Components
        LoadCustomerTask loadCustomerTask = new LoadCustomerTask(customerRepository);
        BuildCustomerDetailResponseTask buildCustomerDetailResponseTask = new BuildCustomerDetailResponseTask();

        // 注入真實 Task 到 Service
        getCustomerDetailService = new GetCustomerDetailServiceImpl(loadCustomerTask, buildCustomerDetailResponseTask);

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

        // Verify Repository was called via LoadCustomerTask
        verify(customerRepository, times(1)).findById(any(CustomerId.class));
    }
}
