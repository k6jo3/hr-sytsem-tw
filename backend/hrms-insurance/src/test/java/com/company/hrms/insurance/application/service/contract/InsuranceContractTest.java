package com.company.hrms.insurance.application.service.contract;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.test.contract.BaseContractTest;
import com.company.hrms.insurance.api.request.GetEnrollmentListRequest;
import com.company.hrms.insurance.application.service.query.assembler.EnrollmentQueryAssembler;

/**
 * HR05 保險管理服務合約測試
 *
 * <p>
 * 依據 contracts/insurance_contracts.md 定義的合約規格進行驗證
 * </p>
 *
 * <p>
 * 合約測試確保:
 * </p>
 * <ul>
 * <li>API 產出的查詢條件符合 SA 定義的業務規則</li>
 * <li>安全過濾（如 is_deleted = 0）正確套用</li>
 * <li>權限控制正確實施（員工只能查詢自己的資料）</li>
 * </ul>
 */
@DisplayName("HR05 保險管理服務合約測試")
public class InsuranceContractTest extends BaseContractTest {

    /**
     * 勞保查詢合約測試
     */
    @Nested
    @DisplayName("勞保投保紀錄查詢合約 (Labor Insurance Query Contract)")
    class LaborInsuranceQueryContractTests {

        private final EnrollmentQueryAssembler assembler = new EnrollmentQueryAssembler();

        @Test
        @DisplayName("INS_L001: 查詢員工勞保紀錄應包含員工ID與刪除標記過濾")
        void searchByEmployeeId_ShouldIncludeCorrectFilters() throws Exception {
            // 1. 載入合約
            String contract = loadContractSpec("insurance");

            // 2. 準備請求
            var request = GetEnrollmentListRequest.builder()
                    .employeeId("E001")
                    .build();

            // 3. 執行轉換
            var query = assembler.toLaborInsuranceQuery(request);

            // 4. 驗證合約
            assertContract(query, contract, "INS_L001");
        }

        @Test
        @DisplayName("INS_L002: 查詢有效勞保應包含狀態過濾")
        void searchActiveEnrollments_ShouldIncludeStatusFilter() throws Exception {
            String contract = loadContractSpec("insurance");
            var request = GetEnrollmentListRequest.builder()
                    .status("ACTIVE")
                    .build();

            var query = assembler.toLaborInsuranceQuery(request);

            assertContract(query, contract, "INS_L002");
        }

        @Test
        @DisplayName("INS_L003: 查詢退保紀錄應包含退保狀態過濾")
        void searchTerminatedEnrollments_ShouldIncludeStatusFilter() throws Exception {
            String contract = loadContractSpec("insurance");
            var request = GetEnrollmentListRequest.builder()
                    .status("TERMINATED")
                    .build();

            var query = assembler.toLaborInsuranceQuery(request);

            assertContract(query, contract, "INS_L003");
        }

        @Test
        @DisplayName("INS_L004: 依投保日期查詢應包含日期過濾")
        void searchByEnrollDate_ShouldIncludeDateFilter() throws Exception {
            String contract = loadContractSpec("insurance");
            var request = GetEnrollmentListRequest.builder()
                    .enrollDate("2025-01-01")
                    .build();

            var query = assembler.toLaborInsuranceQuery(request);

            assertContract(query, contract, "INS_L004");
        }

        @Test
        @DisplayName("INS_L005: 員工查詢自己勞保應包含當前使用者過濾")
        void searchOwnEnrollments_ShouldIncludeCurrentUserFilter() throws Exception {
            // 當設定 currentUserId 時，查詢必須包含 employee_id 過濾
            var request = GetEnrollmentListRequest.builder()
                    .currentUserId("E001")
                    .build();

            var query = assembler.toLaborInsuranceQuery(request);

            // 驗證包含 employee_id 和 is_deleted 過濾條件
            assertHasFilterForField(query, "employee_id");
            assertHasFilterForField(query, "is_deleted");
        }

        @Test
        @DisplayName("INS_L006: 依投保級距查詢應包含級距過濾")
        void searchBySalaryGrade_ShouldIncludeGradeFilter() throws Exception {
            String contract = loadContractSpec("insurance");
            var request = GetEnrollmentListRequest.builder()
                    .salaryGrade("45800")
                    .build();

            var query = assembler.toLaborInsuranceQuery(request);

            assertContract(query, contract, "INS_L006");
        }
    }

    /**
     * 健保查詢合約測試
     */
    @Nested
    @DisplayName("健保投保紀錄查詢合約 (Health Insurance Query Contract)")
    class HealthInsuranceQueryContractTests {

        private final EnrollmentQueryAssembler assembler = new EnrollmentQueryAssembler();

        @Test
        @DisplayName("INS_H001: 查詢員工健保紀錄應包含員工ID過濾")
        void searchByEmployeeId_ShouldIncludeCorrectFilters() throws Exception {
            String contract = loadContractSpec("insurance");
            var request = GetEnrollmentListRequest.builder()
                    .employeeId("E001")
                    .build();

            var query = assembler.toHealthInsuranceQuery(request);

            assertContract(query, contract, "INS_H001");
        }

        @Test
        @DisplayName("INS_H002: 查詢有效健保應包含狀態過濾")
        void searchActiveEnrollments_ShouldIncludeStatusFilter() throws Exception {
            String contract = loadContractSpec("insurance");
            var request = GetEnrollmentListRequest.builder()
                    .status("ACTIVE")
                    .build();

            var query = assembler.toHealthInsuranceQuery(request);

            assertContract(query, contract, "INS_H002");
        }

        @Test
        @DisplayName("INS_H003: 查詢含眷屬的健保應包含眷屬過濾")
        void searchWithDependents_ShouldIncludeDependentsFilter() throws Exception {
            String contract = loadContractSpec("insurance");
            var request = GetEnrollmentListRequest.builder()
                    .hasDependents(true)
                    .build();

            var query = assembler.toHealthInsuranceQuery(request);

            assertContract(query, contract, "INS_H003");
        }

        @Test
        @DisplayName("INS_H004: 員工查詢自己健保應包含當前使用者過濾")
        void searchOwnEnrollments_ShouldIncludeCurrentUserFilter() throws Exception {
            // 當設定 currentUserId 時，查詢必須包含 employee_id 過濾
            var request = GetEnrollmentListRequest.builder()
                    .currentUserId("E001")
                    .build();

            var query = assembler.toHealthInsuranceQuery(request);

            // 驗證包含 employee_id 和 is_deleted 過濾條件
            assertHasFilterForField(query, "employee_id");
            assertHasFilterForField(query, "is_deleted");
        }

        @Test
        @DisplayName("INS_H005: 依投保單位查詢應包含單位過濾")
        void searchByInsuranceUnit_ShouldIncludeUnitFilter() throws Exception {
            String contract = loadContractSpec("insurance");
            var request = GetEnrollmentListRequest.builder()
                    .insuranceUnit("U001")
                    .build();

            var query = assembler.toHealthInsuranceQuery(request);

            assertContract(query, contract, "INS_H005");
        }
    }

    /**
     * 勞退查詢合約測試
     */
    @Nested
    @DisplayName("勞退提撥紀錄查詢合約 (Pension Query Contract)")
    class PensionQueryContractTests {

        private final EnrollmentQueryAssembler assembler = new EnrollmentQueryAssembler();

        @Test
        @DisplayName("INS_P001: 查詢員工勞退紀錄應包含員工ID過濾")
        void searchByEmployeeId_ShouldIncludeCorrectFilters() throws Exception {
            String contract = loadContractSpec("insurance");
            var request = GetEnrollmentListRequest.builder()
                    .employeeId("E001")
                    .build();

            var query = assembler.toPensionQuery(request);

            assertContract(query, contract, "INS_P001");
        }

        @Test
        @DisplayName("INS_P005: 員工查詢自己勞退應包含當前使用者過濾")
        void searchOwnEnrollments_ShouldIncludeCurrentUserFilter() throws Exception {
            // 當設定 currentUserId 時，查詢必須包含 employee_id 過濾
            var request = GetEnrollmentListRequest.builder()
                    .currentUserId("E001")
                    .build();

            var query = assembler.toPensionQuery(request);

            // 驗證包含 employee_id 和 is_deleted 過濾條件
            assertHasFilterForField(query, "employee_id");
            assertHasFilterForField(query, "is_deleted");
        }
    }

    /**
     * 通用安全規則測試
     */
    @Nested
    @DisplayName("通用安全規則測試 (Security Rules)")
    class SecurityRulesTests {

        private final EnrollmentQueryAssembler assembler = new EnrollmentQueryAssembler();

        @Test
        @DisplayName("所有查詢必須包含 is_deleted = 0 過濾條件")
        void allQueries_ShouldIncludeDeleteFilter() throws Exception {
            // 空查詢也必須包含 is_deleted = 0
            var request = GetEnrollmentListRequest.builder().build();
            var query = assembler.toQueryGroup(request);

            assertHasFilterForField(query, "is_deleted");
        }

        @Test
        @DisplayName("員工角色只能查詢自己的資料")
        void employeeRole_ShouldOnlyQueryOwnData() throws Exception {
            // 當設定 currentUserId 時，查詢必須包含該使用者的過濾
            var request = GetEnrollmentListRequest.builder()
                    .currentUserId("E001")
                    .build();
            var query = assembler.toQueryGroup(request);

            assertHasFilterForField(query, "employee_id");
        }
    }
}
