package com.company.hrms.organization.application.service.employee.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

/**
 * LoadEmployeeTask 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LoadEmployeeTask 測試")
class LoadEmployeeTaskTest {

    @Mock
    private IEmployeeRepository employeeRepository;

    @InjectMocks
    private LoadEmployeeTask task;

    private EmployeeContext context;

    @BeforeEach
    void setUp() {
        context = new EmployeeContext();
        context.setEmployeeId(UUID.randomUUID().toString());
    }

    @Nested
    @DisplayName("載入成功")
    class SuccessTests {

        @Test
        @DisplayName("應成功載入員工並設置到 Context")
        void shouldLoadEmployeeSuccessfully() throws Exception {
            // Given
            Employee mockEmployee = mock(Employee.class);
            when(employeeRepository.findById(any(EmployeeId.class))).thenReturn(Optional.of(mockEmployee));

            // When
            task.execute(context);

            // Then
            assertNotNull(context.getEmployee());
            assertEquals(mockEmployee, context.getEmployee());
        }
    }

    @Nested
    @DisplayName("載入失敗")
    class FailureTests {

        @Test
        @DisplayName("員工不存在應拋出例外")
        void shouldThrowExceptionWhenEmployeeNotFound() {
            // Given
            when(employeeRepository.findById(any(EmployeeId.class))).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> task.execute(context));
            assertTrue(exception.getMessage().contains("員工不存在"));
        }
    }

    @Test
    @DisplayName("getName 應返回 '載入員工資料'")
    void shouldReturnCorrectName() {
        assertEquals("載入員工資料", task.getName());
    }
}
