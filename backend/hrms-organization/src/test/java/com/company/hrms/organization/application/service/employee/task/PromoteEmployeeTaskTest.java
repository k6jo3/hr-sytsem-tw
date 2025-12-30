package com.company.hrms.organization.application.service.employee.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.organization.api.request.employee.PromoteEmployeeRequest;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.domain.model.aggregate.Employee;

/**
 * PromoteEmployeeTask 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PromoteEmployeeTask 測試")
class PromoteEmployeeTaskTest {

    @InjectMocks
    private PromoteEmployeeTask task;

    private EmployeeContext context;
    private Employee mockEmployee;
    private PromoteEmployeeRequest request;

    @BeforeEach
    void setUp() {
        mockEmployee = mock(Employee.class);
        request = new PromoteEmployeeRequest();
        request.setNewJobTitle("Senior Engineer");
        request.setNewJobLevel("4");

        context = new EmployeeContext();
        context.setEmployee(mockEmployee);
        context.setPromoteRequest(request);
    }

    @Test
    @DisplayName("應成功執行升遷並記錄舊屬性")
    void shouldPromoteEmployeeSuccessfully() throws Exception {
        // Given
        when(mockEmployee.getJobTitle()).thenReturn("Engineer");
        when(mockEmployee.getJobLevel()).thenReturn("3");

        // When
        task.execute(context);

        // Then
        verify(mockEmployee).promote(request.getNewJobTitle(), request.getNewJobLevel());
        assertEquals("Engineer", context.getAttribute("oldJobTitle"));
        assertEquals("3", context.getAttribute("oldJobLevel"));
    }

    @Test
    @DisplayName("getName 應返回 '執行升遷'")
    void shouldReturnCorrectName() {
        assertEquals("執行升遷", task.getName());
    }
}
