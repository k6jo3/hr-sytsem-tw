package com.company.hrms.organization.application.service.employee.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.organization.api.request.employee.TerminateEmployeeRequest;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.domain.model.aggregate.Employee;

/**
 * TerminateEmployeeTask 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TerminateEmployeeTask 測試")
class TerminateEmployeeTaskTest {

    @InjectMocks
    private TerminateEmployeeTask task;

    private EmployeeContext context;
    private Employee mockEmployee;
    private TerminateEmployeeRequest request;

    @BeforeEach
    void setUp() {
        mockEmployee = mock(Employee.class);
        request = new TerminateEmployeeRequest();
        request.setTerminationDate(LocalDate.now());
        request.setReason("Personal Reason");

        context = new EmployeeContext();
        context.setEmployee(mockEmployee);
        context.setTerminateRequest(request);
    }

    @Test
    @DisplayName("應成功執行離職")
    void shouldTerminateEmployeeSuccessfully() throws Exception {
        // When
        task.execute(context);

        // Then
        verify(mockEmployee).terminate(request.getTerminationDate(), request.getReason());
    }

    @Test
    @DisplayName("getName 應返回 '執行離職'")
    void shouldReturnCorrectName() {
        assertEquals("執行離職", task.getName());
    }
}
