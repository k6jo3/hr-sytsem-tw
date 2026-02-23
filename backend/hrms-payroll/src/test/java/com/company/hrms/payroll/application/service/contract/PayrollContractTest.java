package com.company.hrms.payroll.application.service.contract;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.test.contract.BaseContractTest;
import com.company.hrms.payroll.application.dto.request.GetBonusListRequest;
import com.company.hrms.payroll.application.dto.request.GetDeductionListRequest;
import com.company.hrms.payroll.application.dto.request.GetOvertimePayListRequest;
import com.company.hrms.payroll.application.dto.request.GetPayrollRunListRequest;
import com.company.hrms.payroll.application.dto.request.GetPayslipListRequest;
import com.company.hrms.payroll.application.dto.request.GetSalaryStructureListRequest;
import com.company.hrms.payroll.application.service.query.assembler.BonusQueryAssembler;
import com.company.hrms.payroll.application.service.query.assembler.DeductionQueryAssembler;
import com.company.hrms.payroll.application.service.query.assembler.OvertimePayQueryAssembler;
import com.company.hrms.payroll.application.service.query.assembler.PayrollRunQueryAssembler;
import com.company.hrms.payroll.application.service.query.assembler.PayslipQueryAssembler;
import com.company.hrms.payroll.application.service.query.assembler.SalaryStructureQueryAssembler;

/**
 * HR04 薪資管理服務業務合約測試 (重建版 v2.0)
 */
@DisplayName("HR04 薪資管理服務業務合約測試")
public class PayrollContractTest extends BaseContractTest {

    private static final String CONTRACT = "payroll_contracts";

    @Override
    protected String loadContractSpec(String serviceName) throws java.io.IOException {
        Path current = Paths.get("").toAbsolutePath();
        for (int i = 0; i < 6; i++) {
            Path candidate = current.resolve("contracts/" + serviceName + ".md");
            if (Files.exists(candidate)) {
                return Files.readString(candidate);
            }
            Path candidateSibling = current.resolve("../contracts/" + serviceName + ".md");
            if (Files.exists(candidateSibling)) {
                return Files.readString(candidateSibling);
            }
            current = current.getParent();
            if (current == null)
                break;
        }
        return super.loadContractSpec(serviceName);
    }

    // ========================================================================
    // 2.1 薪資結構查詢合約 (Salary Structure Query Contract)
    // ========================================================================
    @Nested
    @DisplayName("2.1 薪資結構查詢合約")
    class SalaryStructureQueryContractTests {
        private final SalaryStructureQueryAssembler assembler = new SalaryStructureQueryAssembler();

        @Test
        @DisplayName("PAY_QRY_S001: 查詢員工薪資結構")
        void PAY_QRY_S001() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetSalaryStructureListRequest.builder().employeeId("E001").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_S001");
        }

        @Test
        @DisplayName("PAY_QRY_S002: 查詢有效薪資結構")
        void PAY_QRY_S002() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetSalaryStructureListRequest.builder().isActive(true).build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_S002");
        }

        @Test
        @DisplayName("PAY_QRY_S003: 查詢月薪制結構")
        void PAY_QRY_S003() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetSalaryStructureListRequest.builder().payrollSystem("MONTHLY").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_S003");
        }

        @Test
        @DisplayName("PAY_QRY_S004: 查詢時薪制結構")
        void PAY_QRY_S004() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetSalaryStructureListRequest.builder().payrollSystem("HOURLY").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_S004");
        }

        @Test
        @DisplayName("PAY_QRY_S005: 組合條件查詢")
        void PAY_QRY_S005() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetSalaryStructureListRequest.builder().employeeId("E001").isActive(true).build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_S005");
        }

        @Test
        @DisplayName("PAY_QRY_S006: 查詢停用的薪資結構")
        void PAY_QRY_S006() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetSalaryStructureListRequest.builder().employeeId("E001").isActive(false).build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_S006");
        }

        @Test
        @DisplayName("PAY_QRY_S007: 查詢特定生效日期結構")
        void PAY_QRY_S007() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetSalaryStructureListRequest.builder().employeeId("E001")
                    .effectiveDate(LocalDate.parse("2025-01-01")).build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_S007");
        }
    }

    // ========================================================================
    // 2.2 薪資單查詢合約 (Payslip Query Contract)
    // ========================================================================
    @Nested
    @DisplayName("2.2 薪資單查詢合約")
    class PayslipQueryContractTests {
        private final PayslipQueryAssembler assembler = new PayslipQueryAssembler();

        @Test
        @DisplayName("PAY_QRY_P001: 查詢批次下薪資單")
        void PAY_QRY_P001() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetPayslipListRequest.builder().runId("RUN001").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_P001");
        }

        @Test
        @DisplayName("PAY_QRY_P002: 員工查詢自己薪資單")
        void PAY_QRY_P002() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetPayslipListRequest.builder().employeeId("{currentUserId}").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_P002");
        }

        @Test
        @DisplayName("PAY_QRY_P003: HR 查詢特定員工薪資單")
        void PAY_QRY_P003() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetPayslipListRequest.builder().employeeId("E001").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_P003");
        }

        @Test
        @DisplayName("PAY_QRY_P004: 組合條件查詢薪資單")
        void PAY_QRY_P004() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetPayslipListRequest.builder().runId("RUN001").employeeId("E001").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_P004");
        }

        @Test
        @DisplayName("PAY_QRY_P005: 員工查詢特定月份薪資單")
        void PAY_QRY_P005() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetPayslipListRequest.builder().employeeId("{currentUserId}").yearMonth("2025-01").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_P005");
        }

        @Test
        @DisplayName("PAY_QRY_P006: 員工查詢歷史薪資單")
        void PAY_QRY_P006() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetPayslipListRequest.builder().employeeId("{currentUserId}").status("SENT").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_P006");
        }

        @Test
        @DisplayName("PAY_QRY_P007: 依發放日期查詢")
        void PAY_QRY_P007() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetPayslipListRequest.builder().payDate(LocalDate.parse("2025-01-05")).build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_P007");
        }

        @Test
        @DisplayName("PAY_QRY_P008: 查詢草稿狀態薪資單")
        void PAY_QRY_P008() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetPayslipListRequest.builder().status("DRAFT").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_P008");
        }

        @Test
        @DisplayName("PAY_QRY_P009: 查詢已寄送薪資單")
        void PAY_QRY_P009() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetPayslipListRequest.builder().status("SENT").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_P009");
        }
    }

    // ========================================================================
    // 2.3 獎金查詢合約 (Bonus Query Contract)
    // ========================================================================
    @Nested
    @DisplayName("2.3 獎金查詢合約")
    class BonusQueryContractTests {
        private final BonusQueryAssembler assembler = new BonusQueryAssembler();

        @Test
        @DisplayName("PAY_QRY_B001: 查詢員工獎金")
        void PAY_QRY_B001() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetBonusListRequest.builder().employeeId("E001").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_B001");
        }

        @Test
        @DisplayName("PAY_QRY_B002: 依獎金類型查詢")
        void PAY_QRY_B002() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetBonusListRequest.builder().bonusType("PERFORMANCE").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_B002");
        }

        @Test
        @DisplayName("PAY_QRY_B003: 查詢年終獎金")
        void PAY_QRY_B003() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetBonusListRequest.builder().bonusType("YEAR_END").year(2025).build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_B003");
        }

        @Test
        @DisplayName("PAY_QRY_B004: 依發放狀態查詢")
        void PAY_QRY_B004() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetBonusListRequest.builder().status("PAID").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_B004");
        }

        @Test
        @DisplayName("PAY_QRY_B005: 員工查詢自己獎金")
        void PAY_QRY_B005() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetBonusListRequest.builder().employeeId("{currentUserId}").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_B005");
        }

        @Test
        @DisplayName("PAY_QRY_B006: 查詢待發放獎金")
        void PAY_QRY_B006() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetBonusListRequest.builder().status("APPROVED").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_B006");
        }
    }

    // ========================================================================
    // 2.4 扣款項目查詢合約 (Deduction Query Contract)
    // ========================================================================
    @Nested
    @DisplayName("2.4 扣款項目查詢合約")
    class DeductionQueryContractTests {
        private final DeductionQueryAssembler assembler = new DeductionQueryAssembler();

        @Test
        @DisplayName("PAY_QRY_D001: 查詢員工扣款項目")
        void PAY_QRY_D001() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetDeductionListRequest.builder().employeeId("E001").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_D001");
        }

        @Test
        @DisplayName("PAY_QRY_D002: 依扣款類型查詢")
        void PAY_QRY_D002() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetDeductionListRequest.builder().deductionType("LOAN").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_D002");
        }

        @Test
        @DisplayName("PAY_QRY_D003: 查詢進行中的扣款")
        void PAY_QRY_D003() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetDeductionListRequest.builder().status("ACTIVE").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_D003");
        }

        @Test
        @DisplayName("PAY_QRY_D004: 查詢已結清的扣款")
        void PAY_QRY_D004() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetDeductionListRequest.builder().status("COMPLETED").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_D004");
        }

        @Test
        @DisplayName("PAY_QRY_D005: 員工查詢自己扣款")
        void PAY_QRY_D005() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetDeductionListRequest.builder().employeeId("{currentUserId}").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_D005");
        }
    }

    // ========================================================================
    // 2.5 加班費計算查詢合約 (Overtime Pay Query Contract)
    // ========================================================================
    @Nested
    @DisplayName("2.5 加班費計算查詢合約")
    class OvertimePayQueryContractTests {
        private final OvertimePayQueryAssembler assembler = new OvertimePayQueryAssembler();

        @Test
        @DisplayName("PAY_QRY_O001: 查詢員工加班費")
        void PAY_QRY_O001() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetOvertimePayListRequest.builder().employeeId("E001").yearMonth("2025-01").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_O001");
        }

        @Test
        @DisplayName("PAY_QRY_O002: 查詢部門加班費")
        void PAY_QRY_O002() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetOvertimePayListRequest.builder().deptId("D001").yearMonth("2025-01").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_O002");
        }

        @Test
        @DisplayName("PAY_QRY_O003: 員工查詢自己加班費")
        void PAY_QRY_O003() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetOvertimePayListRequest.builder().employeeId("{currentUserId}").yearMonth("2025-01")
                    .build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_O003");
        }
    }

    // ========================================================================
    // 2.6 薪資批次查詢合約 (Payroll Run Query Contract)
    // ========================================================================
    @Nested
    @DisplayName("2.6 薪資批次查詢合約")
    class PayrollRunQueryContractTests {
        private final PayrollRunQueryAssembler assembler = new PayrollRunQueryAssembler();

        @Test
        @DisplayName("PAY_QRY_R001: HR 查詢特定組織薪資批次")
        void PAY_QRY_R001() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetPayrollRunListRequest.builder().organizationId("ORG001").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_R001");
        }

        @Test
        @DisplayName("PAY_QRY_R002: HR 查詢特定狀態薪資批次")
        void PAY_QRY_R002() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetPayrollRunListRequest.builder().status("SUBMITTED").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_R002");
        }

        @Test
        @DisplayName("PAY_QRY_R003: HR 查詢日期範圍內批次")
        void PAY_QRY_R003() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetPayrollRunListRequest.builder().startDate(LocalDate.parse("2025-01-01"))
                    .endDate(LocalDate.parse("2025-01-31")).build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_R003");
        }

        @Test
        @DisplayName("PAY_QRY_R004: 查詢草稿狀態批次")
        void PAY_QRY_R004() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetPayrollRunListRequest.builder().status("DRAFT").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_R004");
        }

        @Test
        @DisplayName("PAY_QRY_R005: 查詢已核准批次")
        void PAY_QRY_R005() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetPayrollRunListRequest.builder().status("APPROVED").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_R005");
        }

        @Test
        @DisplayName("PAY_QRY_R006: 查詢已發薪批次")
        void PAY_QRY_R006() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetPayrollRunListRequest.builder().status("PAID").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_R006");
        }

        @Test
        @DisplayName("PAY_QRY_R007: 排除已取消的批次")
        void PAY_QRY_R007() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetPayrollRunListRequest.builder().excludeCancelled(true).build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_R007");
        }

        @Test
        @DisplayName("PAY_QRY_R008: 查詢計算完成的批次")
        void PAY_QRY_R008() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetPayrollRunListRequest.builder().status("COMPLETED").build();
            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "PAY_QRY_R008");
        }
    }
}
