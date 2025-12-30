package com.company.hrms.organization.application.service.employee.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.EmploymentType;
import com.company.hrms.organization.domain.model.valueobject.Gender;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

/**
 * SaveEmployeeTask 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SaveEmployeeTask 測試")
class SaveEmployeeTaskTest {

    @Mock
    private IEmployeeRepository employeeRepository;

    @InjectMocks
    private SaveEmployeeTask task;

    private EmployeeContext context;
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        UUID orgId = UUID.randomUUID();
        UUID deptId = UUID.randomUUID();

        testEmployee = Employee.onboard(
                "EMP-001",
                "大明", "王", "A123456789",
                LocalDate.of(1990, 1, 1), Gender.MALE,
                "wang@company.com", "0912345678",
                orgId, deptId, "軟體工程師",
                EmploymentType.FULL_TIME, LocalDate.now(), 3);

        context = new EmployeeContext();
        context.setEmployee(testEmployee);
    }

    @Test
    @DisplayName("應成功儲存員工")
    void shouldSaveEmployeeSuccessfully() throws Exception {
        // When
        task.execute(context);

        // Then
        verify(employeeRepository).save(testEmployee);
    }

    @Test
    @DisplayName("getName 應返回 '儲存員工'")
    void shouldReturnCorrectName() {
        assertEquals("儲存員工", task.getName());
    }
}
