package com.company.hrms.organization.application.service.employee.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.organization.api.request.employee.CreateEmployeeRequest;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

/**
 * ValidateEmployeeTask 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ValidateEmployeeTask 測試")
class ValidateEmployeeTaskTest {

    @Mock
    private IEmployeeRepository employeeRepository;

    @Mock
    private IDepartmentRepository departmentRepository;

    @InjectMocks
    private ValidateEmployeeTask task;

    private EmployeeContext context;
    private CreateEmployeeRequest request;

    @BeforeEach
    void setUp() {
        request = new CreateEmployeeRequest();
        request.setEmployeeNumber("EMP-001");
        request.setFirstName("大明");
        request.setLastName("王");
        request.setNationalId("A123456789");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setCompanyEmail("wang@company.com");
        request.setDepartmentId("550e8400-e29b-41d4-a716-446655440000");

        context = new EmployeeContext();
        context.setCreateRequest(request);
    }

    @Nested
    @DisplayName("驗證成功")
    class SuccessTests {

        @Test
        @DisplayName("所有驗證通過應成功執行")
        void shouldPassWhenAllValidationsPass() throws Exception {
            // Given
            when(employeeRepository.existsByEmployeeNumber("EMP-001")).thenReturn(false);
            when(employeeRepository.existsByEmail("wang@company.com")).thenReturn(false);
            when(employeeRepository.existsByNationalId("A123456789")).thenReturn(false);
            when(departmentRepository.existsById(any(DepartmentId.class))).thenReturn(true);

            // When & Then
            assertDoesNotThrow(() -> task.execute(context));
        }
    }

    @Nested
    @DisplayName("驗證失敗")
    class FailureTests {

        @Test
        @DisplayName("員工編號重複應拋出例外")
        void shouldThrowExceptionWhenEmployeeNumberExists() {
            // Given
            when(employeeRepository.existsByEmployeeNumber("EMP-001")).thenReturn(true);

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> task.execute(context));
            assertTrue(exception.getMessage().contains("員工編號已存在"));
        }

        @Test
        @DisplayName("Email 重複應拋出例外")
        void shouldThrowExceptionWhenEmailExists() {
            // Given
            when(employeeRepository.existsByEmployeeNumber("EMP-001")).thenReturn(false);
            when(employeeRepository.existsByEmail("wang@company.com")).thenReturn(true);

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> task.execute(context));
            assertTrue(exception.getMessage().contains("Email 已存在"));
        }

        @Test
        @DisplayName("身分證號重複應拋出例外")
        void shouldThrowExceptionWhenNationalIdExists() {
            // Given
            when(employeeRepository.existsByEmployeeNumber("EMP-001")).thenReturn(false);
            when(employeeRepository.existsByEmail("wang@company.com")).thenReturn(false);
            when(employeeRepository.existsByNationalId("A123456789")).thenReturn(true);

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> task.execute(context));
            assertTrue(exception.getMessage().contains("身分證號已存在"));
        }

        @Test
        @DisplayName("部門不存在應拋出例外")
        void shouldThrowExceptionWhenDepartmentNotExists() {
            // Given
            when(employeeRepository.existsByEmployeeNumber("EMP-001")).thenReturn(false);
            when(employeeRepository.existsByEmail("wang@company.com")).thenReturn(false);
            when(employeeRepository.existsByNationalId("A123456789")).thenReturn(false);
            when(departmentRepository.existsById(any(DepartmentId.class))).thenReturn(false);

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> task.execute(context));
            assertTrue(exception.getMessage().contains("部門不存在"));
        }
    }

    @Test
    @DisplayName("shouldExecute 在有 CreateRequest 時應返回 true")
    void shouldExecuteWhenCreateRequestExists() {
        assertTrue(task.shouldExecute(context));
    }

    @Test
    @DisplayName("shouldExecute 在無 CreateRequest 時應返回 false")
    void shouldNotExecuteWhenCreateRequestNotExists() {
        EmployeeContext emptyContext = new EmployeeContext();
        assertFalse(task.shouldExecute(emptyContext));
    }

    @Test
    @DisplayName("getName 應返回 '驗證員工資料'")
    void shouldReturnCorrectName() {
        assertEquals("驗證員工資料", task.getName());
    }
}
