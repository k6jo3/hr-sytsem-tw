package com.company.hrms.insurance.application.service.groupplan.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.common.exception.ResourceAlreadyExistsException;
import com.company.hrms.insurance.api.request.CreateGroupInsurancePlanRequest;
import com.company.hrms.insurance.application.service.groupplan.context.CreateGroupPlanContext;
import com.company.hrms.insurance.domain.model.aggregate.GroupInsurancePlan;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.repository.IGroupInsurancePlanRepository;

/**
 * ValidatePlanCodeUniqueTask 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ValidatePlanCodeUniqueTask 測試")
class ValidatePlanCodeUniqueTaskTest {

    @Mock
    private IGroupInsurancePlanRepository planRepository;

    @InjectMocks
    private ValidatePlanCodeUniqueTask task;

    private CreateGroupPlanContext context;
    private static final String PLAN_CODE = "GL-2026-001";

    @BeforeEach
    void setUp() {
        CreateGroupInsurancePlanRequest request = CreateGroupInsurancePlanRequest.builder()
                .organizationId("ORG001")
                .planName("2026年團體壽險方案")
                .planCode(PLAN_CODE)
                .insuranceType("GROUP_LIFE")
                .insurerName("國泰人壽")
                .policyNumber("POL-20260101")
                .contractStartDate("2026-01-01")
                .contractEndDate("2026-12-31")
                .build();
        context = new CreateGroupPlanContext(request);
    }

    @Nested
    @DisplayName("execute 方法測試")
    class ExecuteTests {

        @Test
        @DisplayName("方案代碼不存在時，驗證應通過")
        void should_pass_when_planCodeDoesNotExist() throws Exception {
            // Given
            when(planRepository.findByPlanCode(PLAN_CODE)).thenReturn(Optional.empty());

            // When & Then — 不應拋出異常
            assertDoesNotThrow(() -> task.execute(context));
            verify(planRepository).findByPlanCode(PLAN_CODE);
        }

        @Test
        @DisplayName("方案代碼已存在時，應拋出 ResourceAlreadyExistsException")
        void should_throwResourceAlreadyExistsException_when_planCodeAlreadyExists() {
            // Given
            GroupInsurancePlan existingPlan = GroupInsurancePlan.create(
                    "ORG001", "既有方案", PLAN_CODE,
                    InsuranceType.GROUP_LIFE, "國泰人壽", "POL-OLD",
                    java.time.LocalDate.of(2025, 1, 1),
                    java.time.LocalDate.of(2025, 12, 31));
            when(planRepository.findByPlanCode(PLAN_CODE)).thenReturn(Optional.of(existingPlan));

            // When & Then
            ResourceAlreadyExistsException ex = assertThrows(
                    ResourceAlreadyExistsException.class,
                    () -> task.execute(context));

            assertTrue(ex.getMessage().contains(PLAN_CODE), "錯誤訊息應包含方案代碼");
            verify(planRepository).findByPlanCode(PLAN_CODE);
        }
    }

    @Nested
    @DisplayName("getName 方法測試")
    class GetNameTests {

        @Test
        @DisplayName("應返回任務名稱")
        void should_returnTaskName() {
            assertEquals("驗證方案代碼唯一性", task.getName());
        }
    }
}
