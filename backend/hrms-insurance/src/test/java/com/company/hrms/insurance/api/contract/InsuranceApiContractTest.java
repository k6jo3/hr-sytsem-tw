package com.company.hrms.insurance.api.contract;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceLevel;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceUnit;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.model.valueobject.LevelId;
import com.company.hrms.insurance.domain.model.valueobject.UnitId;
import com.company.hrms.insurance.domain.repository.IInsuranceEnrollmentRepository;
import com.company.hrms.insurance.domain.repository.IInsuranceLevelRepository;
import com.company.hrms.insurance.domain.repository.IInsuranceUnitRepository;

/**
 * HR05 保險管理服務 API 合約測試 (整合版本)
 * 驗證 Controller -> Service -> Repository 的完整流程
 * 同時涵蓋 Query 方法驗證與 Command 業務流程驗證
 *
 * <p>注意：HR05 Insurance 的 Repository 使用簡單方法 (findByEmployeeId, findAll 等)，
 * 不使用 QueryGroup 模式。因此本測試以 verify() 驗證正確的 Repository 方法被呼叫，
 * 而非使用 assertContract() 驗證 QueryGroup。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("HR05 保險管理服務 API 合約測試")
public class InsuranceApiContractTest extends BaseApiContractTest {

    private static final String CONTRACT = "insurance";
    private String contractSpec;

    // === 領域 Repository ===

    @MockBean
    private IInsuranceEnrollmentRepository enrollmentRepository;

    @MockBean
    private IInsuranceLevelRepository levelRepository;

    @MockBean
    private IInsuranceUnitRepository unitRepository;

    private JWTModel mockUser;

    /** 測試用投保單位 */
    private InsuranceUnit testUnit;

    /** 測試用投保級距 (勞保) */
    private InsuranceLevel testLaborLevel;

    @BeforeEach
    void setUp() throws Exception {
        contractSpec = loadContractSpec(CONTRACT);

        // 設定測試用模擬使用者
        mockUser = new JWTModel();
        mockUser.setUserId("00000000-0000-0000-0000-000000000001");
        mockUser.setUsername("test-user");
        mockUser.setRoles(Collections.singletonList("HR"));

        // 建立測試用投保單位
        testUnit = new InsuranceUnit(
                new UnitId("unit-001"),
                "org-001",
                "INS-UNIT-001",
                "ABC科技股份有限公司");

        // 建立測試用投保級距 (勞保第15級, 月薪 48,200)
        testLaborLevel = new InsuranceLevel(
                new LevelId("level-015"),
                InsuranceType.LABOR,
                15,
                new BigDecimal("48200"),
                LocalDate.of(2025, 1, 1));

        // 設定 Repository 的 lenient 預設行為
        lenient().when(enrollmentRepository.findAll()).thenReturn(Collections.emptyList());
        lenient().when(enrollmentRepository.findByEmployeeId(anyString())).thenReturn(Collections.emptyList());
        lenient().when(enrollmentRepository.findAllActiveByEmployeeId(anyString())).thenReturn(Collections.emptyList());
        lenient().when(enrollmentRepository.save(any())).thenReturn(null);

        lenient().when(unitRepository.findById(any())).thenReturn(Optional.of(testUnit));
        lenient().when(unitRepository.findByUnitCode(anyString())).thenReturn(Optional.of(testUnit));

        lenient().when(levelRepository.findByTypeAndActiveOn(any(), any()))
                .thenReturn(List.of(testLaborLevel));
        lenient().when(levelRepository.findById(any())).thenReturn(Optional.of(testLaborLevel));
    }

    // =========================================================================
    // 加退保記錄查詢 API 合約
    // =========================================================================

    @Nested
    @DisplayName("加退保記錄查詢 API 合約")
    class EnrollmentQueryApiContractTests {

        @Test
        @DisplayName("INS_QRY_E001: 查詢加退保記錄列表 - 無過濾條件時呼叫 findAll()")
        void getEnrollments_WithoutFilter_ShouldCallFindAll() throws Exception {
            // Arrange
            when(enrollmentRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            mockMvc.perform(get("/api/v1/insurance/enrollments")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // Assert - 驗證無 employeeId 時呼叫 findAll()
            verify(enrollmentRepository).findAll();
        }

        @Test
        @DisplayName("INS_QRY_E001b: 查詢加退保記錄列表 - 有 employeeId 時呼叫 findByEmployeeId()")
        void getEnrollments_WithEmployeeId_ShouldCallFindByEmployeeId() throws Exception {
            // Arrange
            when(enrollmentRepository.findByEmployeeId("E001")).thenReturn(Collections.emptyList());

            // Act
            mockMvc.perform(get("/api/v1/insurance/enrollments?employeeId=E001")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // Assert - 驗證有 employeeId 時呼叫 findByEmployeeId()
            verify(enrollmentRepository).findByEmployeeId("E001");
        }
    }

    // =========================================================================
    // 我的保險 (ESS) API 合約
    // =========================================================================

    @Nested
    @DisplayName("我的保險 (ESS) API 合約")
    class MyInsuranceApiContractTests {

        @Test
        @DisplayName("INS_QRY_MY001: 查詢我的保險資訊 - 驗證以當前使用者 ID 查詢")
        void getMyInsurance_ShouldCallFindByEmployeeIdWithCurrentUser() throws Exception {
            // Arrange
            String currentUserId = mockUser.getUserId();
            when(enrollmentRepository.findByEmployeeId(currentUserId)).thenReturn(Collections.emptyList());

            // Act
            mockMvc.perform(get("/api/v1/insurance/my")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // Assert - 驗證以當前使用者 ID 查詢加保記錄
            verify(enrollmentRepository).findByEmployeeId(currentUserId);
        }
    }

    // =========================================================================
    // 加保 Command API 合約
    // =========================================================================

    @Nested
    @DisplayName("加保 Command API 合約")
    class EnrollmentCommandApiContractTests {

        @Test
        @DisplayName("INS_CMD_E001: 員工加保 - 驗證完整加保流程：載入單位 -> 驗證 -> 查詢級距 -> 儲存")
        void enrollEmployee_ShouldExecuteFullEnrollmentPipeline() throws Exception {
            // Arrange
            String requestBody = """
                    {
                      "employeeId": "emp-001",
                      "insuranceUnitId": "unit-001",
                      "monthlySalary": 50000,
                      "enrollDate": "2025-01-01"
                    }
                    """;

            // Act
            mockMvc.perform(post("/api/v1/insurance/enrollments")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isOk());

            // Assert - 驗證完整加保流程
            // 1. 載入投保單位
            verify(unitRepository).findById(any(UnitId.class));
            // 2. 驗證是否已有有效加保 (重複檢查)
            verify(enrollmentRepository).findAllActiveByEmployeeId("emp-001");
            // 3. 查詢投保級距
            verify(levelRepository, atLeastOnce()).findByTypeAndActiveOn(any(InsuranceType.class), any(LocalDate.class));
            // 4. 儲存加保記錄 (勞保 + 健保 + 勞退 = 3 筆)
            verify(enrollmentRepository, times(3)).save(any(InsuranceEnrollment.class));
        }
    }
}
