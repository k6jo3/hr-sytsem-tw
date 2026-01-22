package com.company.hrms.payroll.application.service.contract;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.test.contract.BaseContractTest;
import com.company.hrms.payroll.application.dto.request.GetPayrollRunListRequest;
import com.company.hrms.payroll.application.dto.request.GetPayslipListRequest;
import com.company.hrms.payroll.application.dto.request.GetSalaryStructureListRequest;
import com.company.hrms.payroll.application.service.query.assembler.PayrollRunQueryAssembler;
import com.company.hrms.payroll.application.service.query.assembler.PayslipQueryAssembler;
import com.company.hrms.payroll.application.service.query.assembler.SalaryStructureQueryAssembler;

/**
 * HR04 薪資管理服務合約測試
 *
 * <p>
 * 依據 resources/contracts/payroll_contracts.md 定義的合約規格進行驗證
 * </p>
 *
 * <p>
 * 合約測試確保:
 * </p>
 * <ul>
 * <li>API 產出的查詢條件符合 SA 定義的業務規則</li>
 * <li>安全過濾（如 is_deleted = 0）正確套用</li>
 * <li>權限控制正確實施</li>
 * </ul>
 */
@DisplayName("HR04 薪資管理服務合約測試")
public class PayrollContractTest extends BaseContractTest {

    private static final String CONTRACT = "payroll";

    // ========================================================================
    // 1. 薪資批次查詢合約 (PayrollRun Query Contract)
    // ========================================================================
    @Nested
    @DisplayName("薪資批次查詢合約 (PayrollRun Query Contract)")
    class PayrollRunQueryContractTests {

        private final PayrollRunQueryAssembler assembler = new PayrollRunQueryAssembler();

        @Test
        @DisplayName("PAY_R001: HR 查詢特定組織薪資批次")
        void searchByOrganization_ShouldIncludeOrgFilter() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            GetPayrollRunListRequest request = new GetPayrollRunListRequest();
            request.setOrganizationId("ORG001");

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PAY_R001");
        }

        @Test
        @DisplayName("PAY_R002: HR 查詢特定狀態薪資批次")
        void searchByStatus_ShouldIncludeStatusFilter() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            GetPayrollRunListRequest request = new GetPayrollRunListRequest();
            request.setStatus("PENDING_APPROVAL");

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PAY_R002");
        }

        @Test
        @DisplayName("PAY_R003: HR 查詢日期範圍內批次")
        void searchByDateRange_ShouldIncludeDateFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            GetPayrollRunListRequest request = new GetPayrollRunListRequest();
            request.setStartDate(LocalDate.parse("2025-01-01"));
            request.setEndDate(LocalDate.parse("2025-01-31"));

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PAY_R003");
        }

        @Test
        @DisplayName("PAY_R004: 查詢草稿狀態批次")
        void searchDraftStatus_ShouldIncludeStatusFilter() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            GetPayrollRunListRequest request = new GetPayrollRunListRequest();
            request.setStatus("DRAFT");

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PAY_R004");
        }

        @Test
        @DisplayName("PAY_R005: 查詢已核准批次")
        void searchApprovedStatus_ShouldIncludeStatusFilter() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            GetPayrollRunListRequest request = new GetPayrollRunListRequest();
            request.setStatus("APPROVED");

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PAY_R005");
        }

        @Test
        @DisplayName("PAY_R006: 查詢已發薪批次")
        void searchPaidStatus_ShouldIncludeStatusFilter() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            GetPayrollRunListRequest request = new GetPayrollRunListRequest();
            request.setStatus("PAID");

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PAY_R006");
        }

        @Test
        @DisplayName("空查詢應包含軟刪除過濾")
        void emptyQuery_ShouldIncludeDeleteFilter() throws Exception {
            GetPayrollRunListRequest request = new GetPayrollRunListRequest();

            var query = assembler.toQueryGroup(request);

            assertHasFilterForField(query, "is_deleted");
        }
    }

    // ========================================================================
    // 2. 薪資單查詢合約 (Payslip Query Contract)
    // ========================================================================
    @Nested
    @DisplayName("薪資單查詢合約 (Payslip Query Contract)")
    class PayslipQueryContractTests {

        private final PayslipQueryAssembler assembler = new PayslipQueryAssembler();

        @Test
        @DisplayName("PAY_P001: HR 查詢批次下薪資單")
        void searchByRunId_ShouldIncludeRunIdFilter() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            GetPayslipListRequest request = new GetPayslipListRequest();
            request.setRunId("RUN001");

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PAY_P001");
        }

        @Test
        @DisplayName("PAY_P002: 員工查詢自己薪資單")
        void searchByEmployeeId_ShouldIncludeEmployeeFilter() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            GetPayslipListRequest request = new GetPayslipListRequest();
            request.setEmployeeId("E001");

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PAY_P002");
        }

        @Test
        @DisplayName("PAY_P003: HR 查詢特定員工薪資單")
        void searchByEmployeeIdAsHR_ShouldIncludeEmployeeFilter() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            GetPayslipListRequest request = new GetPayslipListRequest();
            request.setEmployeeId("E001");

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PAY_P003");
        }

        @Test
        @DisplayName("PAY_P004: 組合條件查詢薪資單")
        void searchByCombinedConditions_ShouldIncludeAllFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            GetPayslipListRequest request = new GetPayslipListRequest();
            request.setRunId("RUN001");
            request.setEmployeeId("E001");

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PAY_P004");
        }

        @Test
        @DisplayName("空查詢應包含軟刪除過濾")
        void emptyQuery_ShouldIncludeDeleteFilter() throws Exception {
            GetPayslipListRequest request = new GetPayslipListRequest();

            var query = assembler.toQueryGroup(request);

            assertHasFilterForField(query, "is_deleted");
        }
    }

    // ========================================================================
    // 3. 薪資結構查詢合約 (SalaryStructure Query Contract)
    // ========================================================================
    @Nested
    @DisplayName("薪資結構查詢合約 (SalaryStructure Query Contract)")
    class SalaryStructureQueryContractTests {

        private final SalaryStructureQueryAssembler assembler = new SalaryStructureQueryAssembler();

        @Test
        @DisplayName("PAY_S001: HR 查詢員工薪資結構")
        void searchByEmployeeId_ShouldIncludeEmployeeFilter() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            GetSalaryStructureListRequest request = new GetSalaryStructureListRequest();
            request.setEmployeeId("E001");

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PAY_S001");
        }

        @Test
        @DisplayName("PAY_S002: HR 查詢有效薪資結構")
        void searchActiveStructures_ShouldIncludeActiveFilter() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            GetSalaryStructureListRequest request = new GetSalaryStructureListRequest();
            request.setActive(true);

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PAY_S002");
        }

        @Test
        @DisplayName("PAY_S003: HR 查詢月薪制結構")
        void searchMonthlyPayroll_ShouldIncludeSystemFilter() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            GetSalaryStructureListRequest request = new GetSalaryStructureListRequest();
            request.setPayrollSystem("MONTHLY");

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PAY_S003");
        }

        @Test
        @DisplayName("PAY_S004: HR 查詢時薪制結構")
        void searchHourlyPayroll_ShouldIncludeSystemFilter() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            GetSalaryStructureListRequest request = new GetSalaryStructureListRequest();
            request.setPayrollSystem("HOURLY");

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PAY_S004");
        }

        @Test
        @DisplayName("PAY_S005: 組合條件查詢")
        void searchByCombinedConditions_ShouldIncludeAllFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            GetSalaryStructureListRequest request = new GetSalaryStructureListRequest();
            request.setEmployeeId("E001");
            request.setActive(true);

            var query = assembler.toQueryGroup(request);

            assertContract(query, contract, "PAY_S005");
        }

        @Test
        @DisplayName("空查詢應包含軟刪除過濾")
        void emptyQuery_ShouldIncludeDeleteFilter() throws Exception {
            GetSalaryStructureListRequest request = new GetSalaryStructureListRequest();

            var query = assembler.toQueryGroup(request);

            assertHasFilterForField(query, "is_deleted");
        }
    }

    // ========================================================================
    // 4. 安全規則測試 (Security Rules)
    // ========================================================================
    @Nested
    @DisplayName("安全規則測試 (Security Rules)")
    class SecurityRulesTests {

        @Test
        @DisplayName("PAY_SEC_002: 所有薪資批次查詢必須包含軟刪除過濾")
        void payrollRunQuery_ShouldAlwaysIncludeDeleteFilter() throws Exception {
            var assembler = new PayrollRunQueryAssembler();
            var request = new GetPayrollRunListRequest();

            var query = assembler.toQueryGroup(request);

            assertHasFilterForField(query, "is_deleted");
        }

        @Test
        @DisplayName("PAY_SEC_002: 所有薪資單查詢必須包含軟刪除過濾")
        void payslipQuery_ShouldAlwaysIncludeDeleteFilter() throws Exception {
            var assembler = new PayslipQueryAssembler();
            var request = new GetPayslipListRequest();

            var query = assembler.toQueryGroup(request);

            assertHasFilterForField(query, "is_deleted");
        }

        @Test
        @DisplayName("PAY_SEC_002: 所有薪資結構查詢必須包含軟刪除過濾")
        void salaryStructureQuery_ShouldAlwaysIncludeDeleteFilter() throws Exception {
            var assembler = new SalaryStructureQueryAssembler();
            var request = new GetSalaryStructureListRequest();

            var query = assembler.toQueryGroup(request);

            assertHasFilterForField(query, "is_deleted");
        }
    }
}
