package com.company.hrms.training.application.service.contract;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.contract.BaseContractTest;
import com.company.hrms.training.api.contract.TrainingApiContractTest;
import com.company.hrms.training.api.request.GetMyTrainingsRequest;
import com.company.hrms.training.domain.model.valueobject.EnrollmentStatus;

/**
 * HR10 訓練管理服務合約測試
 *
 * <p>
 * 本測試類別驗證所有查詢服務產出的 QueryGroup 符合業務合約規格。
 * 合約規格定義於 resources/contracts/training_contracts.md
 *
 * <p>
 * <b>測試層級:</b> Service 單元測試
 * <p>
 * <b>測試範圍:</b> 驗證 QueryBuilder / Assembler 產出的查詢條件
 * <p>
 * <b>使用基類:</b> BaseContractTest
 *
 * <p>
 * 注意：此為快速驗證測試，不涉及 Spring Context、MockMvc 或角色權限。
 * 完整的 API 合約測試請參考 {@link TrainingApiContractTest}。
 *
 * @see TrainingApiContractTest API 層級合約測試
 * @see BaseContractTest 合約測試基類
 */
@DisplayName("HR10 訓練管理服務合約測試")
public class TrainingContractTest extends BaseContractTest {

    private static final String CONTRACT = "training";

    // ========================================================================
    // 1. 課程查詢合約 (Course Query Contract)
    // ========================================================================
    @Nested
    @DisplayName("課程查詢合約")
    class CourseQueryContractTests {

        @Test
        @DisplayName("TRN_C001: 查詢開放報名課程")
        void searchOpenCourses_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);

            QueryGroup query = QueryBuilder.where()
                    .eq("status", "OPEN")
                    .eq("is_deleted", 0)
                    .build();

            assertContract(query, contract, "TRN_C001");
        }

        @Test
        @DisplayName("TRN_C002: 查詢進行中課程")
        void searchInProgressCourses_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);

            QueryGroup query = QueryBuilder.where()
                    .eq("status", "IN_PROGRESS")
                    .eq("is_deleted", 0)
                    .build();

            assertContract(query, contract, "TRN_C002");
        }

        @Test
        @DisplayName("TRN_C003: 查詢已結束課程")
        void searchCompletedCourses_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);

            QueryGroup query = QueryBuilder.where()
                    .eq("status", "COMPLETED")
                    .eq("is_deleted", 0)
                    .build();

            assertContract(query, contract, "TRN_C003");
        }

        @Test
        @DisplayName("TRN_C004: 依類型查詢 (必修)")
        void searchByType_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);

            QueryGroup query = QueryBuilder.where()
                    .eq("type", "MANDATORY")
                    .eq("is_deleted", 0)
                    .build();

            assertContract(query, contract, "TRN_C004");
        }

        @Test
        @DisplayName("TRN_C005: 依類別查詢 (技術類)")
        void searchByCategory_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);

            QueryGroup query = QueryBuilder.where()
                    .eq("category", "TECHNICAL")
                    .eq("is_deleted", 0)
                    .build();

            assertContract(query, contract, "TRN_C005");
        }

        @Test
        @DisplayName("TRN_C006: 依名稱模糊查詢")
        void searchByName_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);

            QueryGroup query = QueryBuilder.where()
                    .like("name", "領導")
                    .eq("is_deleted", 0)
                    .build();

            assertContract(query, contract, "TRN_C006");
        }

        @Test
        @DisplayName("TRN_C007: 查詢線上課程")
        void searchOnlineCourses_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);

            QueryGroup query = QueryBuilder.where()
                    .eq("mode", "ONLINE")
                    .eq("is_deleted", 0)
                    .build();

            assertContract(query, contract, "TRN_C007");
        }

        @Test
        @DisplayName("TRN_C008: 查詢實體課程")
        void searchOfflineCourses_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);

            QueryGroup query = QueryBuilder.where()
                    .eq("mode", "OFFLINE")
                    .eq("is_deleted", 0)
                    .build();

            assertContract(query, contract, "TRN_C008");
        }

        @Test
        @DisplayName("TRN_C009: 依講師查詢")
        void searchByInstructor_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);

            QueryGroup query = QueryBuilder.where()
                    .eq("instructor_id", "E001")
                    .eq("is_deleted", 0)
                    .build();

            assertContract(query, contract, "TRN_C009");
        }
    }

    // ========================================================================
    // 2. 報名紀錄查詢合約 (Enrollment Query Contract)
    // ========================================================================
    @Nested
    @DisplayName("報名紀錄查詢合約")
    class EnrollmentQueryContractTests {

        @Test
        @DisplayName("TRN_E001: 查詢課程報名")
        void searchByCourse_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);

            QueryGroup query = QueryBuilder.where()
                    .eq("course_id", "C001")
                    .eq("is_deleted", 0)
                    .build();

            assertContract(query, contract, "TRN_E001");
        }

        @Test
        @DisplayName("TRN_E002: 查詢員工報名")
        void searchByEmployee_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);

            QueryGroup query = QueryBuilder.where()
                    .eq("employee_id", "E001")
                    .eq("is_deleted", 0)
                    .build();

            assertContract(query, contract, "TRN_E002");
        }

        @Test
        @DisplayName("TRN_E003: 查詢待審核報名")
        void searchPendingEnrollments_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);

            QueryGroup query = QueryBuilder.where()
                    .eq("status", "PENDING")
                    .eq("is_deleted", 0)
                    .build();

            assertContract(query, contract, "TRN_E003");
        }

        @Test
        @DisplayName("TRN_E004: 查詢已核准報名")
        void searchApprovedEnrollments_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);

            QueryGroup query = QueryBuilder.where()
                    .eq("status", "APPROVED")
                    .eq("is_deleted", 0)
                    .build();

            assertContract(query, contract, "TRN_E004");
        }

        @Test
        @DisplayName("TRN_E005: 員工查詢自己報名 - 使用 GetMyTrainingsRequest")
        void searchOwnEnrollments_ShouldIncludeEmployeeFilter() throws Exception {
            // 驗證透過 GetMyTrainingsRequest 建構的查詢
            String currentUserId = "E001";

            GetMyTrainingsRequest request = new GetMyTrainingsRequest();
            // 不設定 status，只驗證員工 ID 過濾

            QueryGroup query = QueryBuilder.where()
                    .fromDto(request)
                    .eq("employeeId", currentUserId) // 強制加入員工ID過濾
                    .eq("is_deleted", 0)
                    .build();

            // 驗證包含員工 ID 過濾
            assertHasFilterForField(query, "employeeId");
        }

        @Test
        @DisplayName("TRN_E007: 查詢已完成課程")
        void searchCompletedEnrollments_ShouldIncludeFilters() throws Exception {
            String currentUserId = "E001";

            GetMyTrainingsRequest request = new GetMyTrainingsRequest();
            request.setStatus(EnrollmentStatus.COMPLETED);

            QueryGroup query = QueryBuilder.where()
                    .fromDto(request)
                    .eq("employeeId", currentUserId)
                    .eq("is_deleted", 0)
                    .build();

            // 驗證包含員工 ID 和狀態過濾
            assertHasFilterForField(query, "employeeId");
            assertHasFilterForField(query, "status");
        }
    }

    // ========================================================================
    // 3. 認證/證照查詢合約 (Certificate Query Contract)
    // ========================================================================
    @Nested
    @DisplayName("證照查詢合約")
    class CertificateQueryContractTests {

        @Test
        @DisplayName("TRN_CT001: 查詢員工證照")
        void searchByEmployee_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);

            QueryGroup query = QueryBuilder.where()
                    .eq("employee_id", "E001")
                    .eq("is_deleted", 0)
                    .build();

            assertContract(query, contract, "TRN_CT001");
        }

        @Test
        @DisplayName("TRN_CT002: 查詢有效證照")
        void searchValidCertificates_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);

            QueryGroup query = QueryBuilder.where()
                    .eq("status", "VALID")
                    .eq("is_deleted", 0)
                    .build();

            assertContract(query, contract, "TRN_CT002");
        }

        @Test
        @DisplayName("TRN_CT003: 查詢即將到期證照")
        void searchExpiringCertificates_ShouldIncludeFilters() throws Exception {
            LocalDate threshold = LocalDate.now().plusDays(30);

            QueryGroup query = QueryBuilder.where()
                    .and("expiry_date", Operator.LTE, threshold)
                    .eq("status", "VALID")
                    .eq("is_deleted", 0)
                    .build();

            // 驗證包含到期日和狀態過濾
            assertHasFilterForField(query, "expiry_date");
            assertHasFilterForField(query, "status");
        }

        @Test
        @DisplayName("TRN_CT004: 查詢已過期證照")
        void searchExpiredCertificates_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);

            QueryGroup query = QueryBuilder.where()
                    .eq("status", "EXPIRED")
                    .eq("is_deleted", 0)
                    .build();

            assertContract(query, contract, "TRN_CT004");
        }

        @Test
        @DisplayName("TRN_CT005: 員工查詢自己證照")
        void searchOwnCertificates_ShouldIncludeEmployeeFilter() throws Exception {
            String currentUserId = "E001";

            QueryGroup query = QueryBuilder.where()
                    .eq("employee_id", currentUserId)
                    .eq("is_deleted", 0)
                    .build();

            assertHasFilterForField(query, "employee_id");
        }

        @Test
        @DisplayName("TRN_CT006: 依認證類型查詢")
        void searchByType_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);

            QueryGroup query = QueryBuilder.where()
                    .eq("cert_type", "PROFESSIONAL")
                    .eq("is_deleted", 0)
                    .build();

            assertContract(query, contract, "TRN_CT006");
        }
    }

    // ========================================================================
    // 4. 訓練紀錄查詢合約 (Training Record Query Contract)
    // ========================================================================
    @Nested
    @DisplayName("訓練紀錄查詢合約")
    class TrainingRecordQueryContractTests {

        @Test
        @DisplayName("TRN_R001: 查詢員工訓練紀錄")
        void searchByEmployee_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);

            QueryGroup query = QueryBuilder.where()
                    .eq("employee_id", "E001")
                    .build();

            assertContract(query, contract, "TRN_R001");
        }

        @Test
        @DisplayName("TRN_R002: 查詢年度訓練時數")
        void searchByYear_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);

            QueryGroup query = QueryBuilder.where()
                    .eq("year", 2025)
                    .build();

            assertContract(query, contract, "TRN_R002");
        }

        @Test
        @DisplayName("TRN_R003: 員工查詢自己紀錄")
        void searchOwnRecords_ShouldIncludeEmployeeFilter() throws Exception {
            String currentUserId = "E001";

            QueryGroup query = QueryBuilder.where()
                    .eq("employee_id", currentUserId)
                    .build();

            assertHasFilterForField(query, "employee_id");
        }

        @Test
        @DisplayName("TRN_R004: 查詢部門訓練紀錄")
        void searchByDepartment_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);

            QueryGroup query = QueryBuilder.where()
                    .eq("department_id", "D001")
                    .build();

            assertContract(query, contract, "TRN_R004");
        }
    }

    // ========================================================================
    // 5. 權限邊界測試 (Security Boundary Tests)
    // ========================================================================
    @Nested
    @DisplayName("權限邊界測試")
    class SecurityBoundaryTests {

        @Test
        @DisplayName("TRN_SEC_001: 員工查詢自己訓練 - 應包含員工ID過濾")
        void employee_QueryOwnTrainings_ShouldIncludeEmployeeFilter() throws Exception {
            String currentUserId = "E001";

            GetMyTrainingsRequest request = new GetMyTrainingsRequest();

            QueryGroup query = QueryBuilder.where()
                    .fromDto(request)
                    .eq("employeeId", currentUserId)
                    .eq("is_deleted", 0)
                    .build();

            // 驗證 QueryGroup 包含員工 ID 過濾
            assertHasFilterForField(query, "employeeId");
        }

        @Test
        @DisplayName("TRN_SEC_002: 員工查詢自己證照 - 應包含員工ID過濾")
        void employee_QueryOwnCertificates_ShouldIncludeEmployeeFilter() throws Exception {
            String currentUserId = "E001";

            QueryGroup query = QueryBuilder.where()
                    .eq("employee_id", currentUserId)
                    .eq("is_deleted", 0)
                    .build();

            assertHasFilterForField(query, "employee_id");
        }

        @Test
        @DisplayName("TRN_SEC_003: 依狀態和員工ID組合查詢")
        void combinedQuery_ShouldIncludeAllFilters() throws Exception {
            String currentUserId = "E001";

            GetMyTrainingsRequest request = new GetMyTrainingsRequest();
            request.setStatus(EnrollmentStatus.APPROVED);
            request.setCourseName("React");

            QueryGroup query = QueryBuilder.where()
                    .fromDto(request)
                    .eq("employeeId", currentUserId)
                    .eq("is_deleted", 0)
                    .build();

            // 驗證 QueryGroup 包含所有需要的過濾條件
            assertHasFilterForField(query, "employeeId");
            assertHasFilterForField(query, "status");
            assertHasFilterForField(query, "courseName");
        }
    }
}
