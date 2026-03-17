package com.company.hrms.insurance.application.service.groupplan.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.insurance.api.request.CreateGroupInsurancePlanRequest;
import com.company.hrms.insurance.application.service.groupplan.context.CreateGroupPlanContext;
import com.company.hrms.insurance.domain.model.aggregate.GroupInsurancePlan;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.repository.IGroupInsurancePlanRepository;

/**
 * CreateAndSaveGroupPlanTask 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateAndSaveGroupPlanTask 測試")
class CreateAndSaveGroupPlanTaskTest {

    @Mock
    private IGroupInsurancePlanRepository planRepository;

    @InjectMocks
    private CreateAndSaveGroupPlanTask task;

    private CreateGroupPlanContext context;

    @BeforeEach
    void setUp() {
        CreateGroupInsurancePlanRequest request = CreateGroupInsurancePlanRequest.builder()
                .organizationId("ORG001")
                .planName("2026年團體壽險方案")
                .planCode("GL-2026-001")
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
        @DisplayName("成功建立並儲存方案時，應呼叫 repository.save 並設置 context.plan")
        void should_createAndSavePlan_when_requestIsValid() throws Exception {
            // Given
            doNothing().when(planRepository).save(any(GroupInsurancePlan.class));

            // When
            task.execute(context);

            // Then — 驗證 repository 被呼叫
            ArgumentCaptor<GroupInsurancePlan> captor = ArgumentCaptor.forClass(GroupInsurancePlan.class);
            verify(planRepository).save(captor.capture());

            GroupInsurancePlan savedPlan = captor.getValue();
            assertNotNull(savedPlan.getPlanId(), "planId 不應為 null");
            assertEquals("ORG001", savedPlan.getOrganizationId());
            assertEquals("2026年團體壽險方案", savedPlan.getPlanName());
            assertEquals("GL-2026-001", savedPlan.getPlanCode());
            assertEquals(InsuranceType.GROUP_LIFE, savedPlan.getInsuranceType());
            assertEquals("國泰人壽", savedPlan.getInsurerName());
            assertEquals("POL-20260101", savedPlan.getPolicyNumber());
            assertTrue(savedPlan.isActive(), "新建方案應為啟用狀態");
        }

        @Test
        @DisplayName("執行完成後，context 輸出應包含正確的 plan")
        void should_setPlanInContext_when_executionCompletes() throws Exception {
            // Given
            doNothing().when(planRepository).save(any(GroupInsurancePlan.class));

            // When
            task.execute(context);

            // Then
            GroupInsurancePlan plan = context.getPlan();
            assertNotNull(plan, "context.plan 不應為 null");
            assertEquals("GL-2026-001", plan.getPlanCode());
            assertEquals("2026年團體壽險方案", plan.getPlanName());
            assertEquals(InsuranceType.GROUP_LIFE, plan.getInsuranceType());
        }

        @Test
        @DisplayName("contractEndDate 為 null 時，應成功建立開放式合約方案")
        void should_createPlanWithNullEndDate_when_contractEndDateIsNull() throws Exception {
            // Given
            CreateGroupInsurancePlanRequest request = CreateGroupInsurancePlanRequest.builder()
                    .organizationId("ORG001")
                    .planName("開放式團體壽險方案")
                    .planCode("GL-OPEN-001")
                    .insuranceType("GROUP_LIFE")
                    .insurerName("國泰人壽")
                    .policyNumber("POL-OPEN")
                    .contractStartDate("2026-01-01")
                    .contractEndDate(null)
                    .build();
            CreateGroupPlanContext openContext = new CreateGroupPlanContext(request);
            doNothing().when(planRepository).save(any(GroupInsurancePlan.class));

            // When
            task.execute(openContext);

            // Then
            GroupInsurancePlan plan = openContext.getPlan();
            assertNotNull(plan);
            assertNull(plan.getContractEndDate(), "開放式合約的結束日應為 null");
        }
    }

    @Nested
    @DisplayName("getName 方法測試")
    class GetNameTests {

        @Test
        @DisplayName("應返回任務名稱")
        void should_returnTaskName() {
            assertEquals("建立並儲存團體保險方案", task.getName());
        }
    }
}
