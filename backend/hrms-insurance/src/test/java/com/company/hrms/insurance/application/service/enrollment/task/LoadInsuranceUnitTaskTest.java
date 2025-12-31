package com.company.hrms.insurance.application.service.enrollment.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.insurance.api.request.EnrollEmployeeRequest;
import com.company.hrms.insurance.application.service.enrollment.context.EnrollmentContext;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceUnit;
import com.company.hrms.insurance.domain.model.valueobject.UnitId;
import com.company.hrms.insurance.domain.repository.IInsuranceUnitRepository;

/**
 * LoadInsuranceUnitTask 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LoadInsuranceUnitTask 測試")
class LoadInsuranceUnitTaskTest {

    @Mock
    private IInsuranceUnitRepository unitRepository;

    @InjectMocks
    private LoadInsuranceUnitTask task;

    private EnrollmentContext context;
    private static final String UNIT_ID = "unit-001";
    private static final String EMPLOYEE_ID = "EMP001";

    @BeforeEach
    void setUp() {
        EnrollEmployeeRequest request = EnrollEmployeeRequest.builder()
                .employeeId(EMPLOYEE_ID)
                .insuranceUnitId(UNIT_ID)
                .monthlySalary(new BigDecimal("48200"))
                .enrollDate("2025-01-01")
                .build();
        context = new EnrollmentContext(request, "default");
    }

    @Nested
    @DisplayName("execute 方法測試")
    class ExecuteTests {

        @Test
        @DisplayName("成功載入投保單位時，應設置到 Context")
        void execute_Success_ShouldSetUnitToContext() throws Exception {
            // Given
            InsuranceUnit unit = createActiveUnit();
            when(unitRepository.findById(any(UnitId.class))).thenReturn(Optional.of(unit));

            // When
            task.execute(context);

            // Then
            assertNotNull(context.getInsuranceUnit());
            assertEquals(unit, context.getInsuranceUnit());
            verify(unitRepository).findById(any(UnitId.class));
        }

        @Test
        @DisplayName("投保單位不存在時，應拋出 IllegalArgumentException")
        void execute_UnitNotFound_ShouldThrowException() {
            // Given
            when(unitRepository.findById(any(UnitId.class))).thenReturn(Optional.empty());

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> task.execute(context));
        }

        @Test
        @DisplayName("投保單位已停用時，應拋出 IllegalStateException")
        void execute_UnitInactive_ShouldThrowException() {
            // Given
            InsuranceUnit unit = createInactiveUnit();
            when(unitRepository.findById(any(UnitId.class))).thenReturn(Optional.of(unit));

            // When & Then
            assertThrows(IllegalStateException.class, () -> task.execute(context));
        }
    }

    @Nested
    @DisplayName("shouldExecute 方法測試")
    class ShouldExecuteTests {

        @Test
        @DisplayName("有 unitId 時，應返回 true")
        void shouldExecute_WithUnitId_ShouldReturnTrue() {
            assertTrue(task.shouldExecute(context));
        }

        @Test
        @DisplayName("無 unitId 時，應返回 false")
        void shouldExecute_WithoutUnitId_ShouldReturnFalse() {
            EnrollEmployeeRequest request = EnrollEmployeeRequest.builder()
                    .employeeId(EMPLOYEE_ID)
                    .monthlySalary(new BigDecimal("48200"))
                    .enrollDate("2025-01-01")
                    .build();
            EnrollmentContext ctx = new EnrollmentContext(request, "default");

            assertFalse(task.shouldExecute(ctx));
        }
    }

    // ==================== Helper Methods ====================

    private InsuranceUnit createActiveUnit() {
        InsuranceUnit unit = InsuranceUnit.create("ORG001", "UNIT001", "測試投保單位");
        return unit;
    }

    private InsuranceUnit createInactiveUnit() {
        InsuranceUnit unit = InsuranceUnit.create("ORG001", "UNIT001", "測試投保單位");
        unit.deactivate();
        return unit;
    }
}
