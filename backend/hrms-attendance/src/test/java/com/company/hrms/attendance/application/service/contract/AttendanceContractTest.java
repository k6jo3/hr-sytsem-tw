package com.company.hrms.attendance.application.service.contract;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.attendance.api.request.attendance.GetAttendanceListRequest;
import com.company.hrms.attendance.api.request.attendance.GetLeaveBalanceRequest;
import com.company.hrms.attendance.api.request.leave.GetLeaveListRequest;
import com.company.hrms.attendance.api.request.overtime.GetOvertimeListRequest;
import com.company.hrms.attendance.api.controller.shift.HR03ShiftQryController.ShiftQueryRequest;
import com.company.hrms.attendance.api.controller.leavetype.HR03LeaveTypeQryController.LeaveTypeQueryRequest;
import com.company.hrms.attendance.api.controller.attendance.HR03CheckInQryController.CorrectionQueryRequest;
import com.company.hrms.attendance.api.controller.report.HR03ReportQryController.MonthlyReportQueryRequest;
import com.company.hrms.attendance.api.controller.report.HR03ReportQryController.DailyReportQueryRequest;
import com.company.hrms.attendance.application.service.checkin.assembler.AttendanceQueryAssembler;
import com.company.hrms.attendance.application.service.checkin.assembler.LeaveBalanceQueryAssembler;
import com.company.hrms.attendance.application.service.leave.assembler.LeaveQueryAssembler;
import com.company.hrms.attendance.application.service.overtime.assembler.OvertimeQueryAssembler;
import com.company.hrms.attendance.application.service.shift.assembler.ShiftQueryAssembler;
import com.company.hrms.attendance.application.service.leavetype.assembler.LeaveTypeQueryAssembler;
import com.company.hrms.attendance.application.service.correction.assembler.CorrectionQueryAssembler;
import com.company.hrms.attendance.application.service.report.assembler.ReportQueryAssembler;
import com.company.hrms.common.test.contract.BaseContractTest;

/**
 * HR03 考勤服務 Assembler 單元合約測試
 *
 * <p>本測試類別驗證所有查詢 Assembler 產出的 QueryGroup 符合業務合約規格。
 * 合約規格定義於 resources/contracts/attendance_contracts.md
 *
 * <p><b>測試層級:</b> Assembler 單元測試
 * <p><b>測試範圍:</b> 僅驗證 Assembler.toQueryGroup() 的條件組裝邏輯
 * <p><b>使用基類:</b> BaseContractTest
 *
 * <p>注意：此為快速驗證測試，不涉及 Spring Context、MockMvc 或角色權限。
 * 完整的 API 合約測試請參考 {@link AttendanceApiContractTest}。
 *
 * @see AttendanceApiContractTest API 層級合約測試
 * @see BaseContractTest 合約測試基類
 */
@DisplayName("HR03 考勤服務 Assembler 單元合約測試")
public class AttendanceContractTest extends BaseContractTest {

    private static final String CONTRACT = "attendance";

    // ========================================================================
    // 1. 出勤紀錄查詢合約 (Attendance Record Query Contract)
    // ========================================================================
    @Nested
    @DisplayName("出勤紀錄查詢合約")
    class AttendanceRecordQueryContractTests {

        private final AttendanceQueryAssembler assembler = new AttendanceQueryAssembler();

        @Test
        @DisplayName("ATT_A001: 查詢員工當日出勤")
        void searchDailyAttendance_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetAttendanceListRequest.builder()
                    .employeeId("E001")
                    .date(LocalDate.parse("2025-01-15"))
                    .build();

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_A001");
        }

        @Test
        @DisplayName("ATT_A002: 查詢部門月出勤")
        void searchDeptMonthlyAttendance_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetAttendanceListRequest.builder()
                    .deptId("D001")
                    .month("2025-01")
                    .build();

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_A002");
        }

        @Test
        @DisplayName("ATT_A003: 查詢異常出勤")
        void searchAbnormalAttendance_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetAttendanceListRequest.builder()
                    .status("ABNORMAL")
                    .build();

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_A003");
        }

        @Test
        @DisplayName("ATT_A004: 查詢遲到紀錄")
        void searchLateAttendance_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetAttendanceListRequest.builder()
                    .lateFlag(true)
                    .build();

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_A004");
        }

        @Test
        @DisplayName("ATT_A005: 查詢早退紀錄")
        void searchEarlyLeaveAttendance_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetAttendanceListRequest.builder()
                    .earlyLeaveFlag(true)
                    .build();

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_A005");
        }
    }

    // ========================================================================
    // 2. 請假申請查詢合約 (Leave Request Query Contract)
    // ========================================================================
    @Nested
    @DisplayName("請假申請查詢合約")
    class LeaveRequestQueryContractTests {

        private final LeaveQueryAssembler assembler = new LeaveQueryAssembler();

        @Test
        @DisplayName("ATT_L001: 查詢待審核請假")
        void searchPendingLeave_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetLeaveListRequest.builder()
                    .status("PENDING")
                    .build();

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_L001");
        }

        @Test
        @DisplayName("ATT_L002: 查詢已核准請假")
        void searchApprovedLeave_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetLeaveListRequest.builder()
                    .status("APPROVED")
                    .build();

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_L002");
        }

        @Test
        @DisplayName("ATT_L003: 查詢已駁回請假")
        void searchRejectedLeave_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetLeaveListRequest.builder()
                    .status("REJECTED")
                    .build();

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_L003");
        }

        @Test
        @DisplayName("ATT_L004: 依請假類型查詢")
        void searchByLeaveType_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetLeaveListRequest.builder()
                    .leaveType("ANNUAL")
                    .build();

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_L004");
        }

        @Test
        @DisplayName("ATT_L005: 依員工查詢請假")
        void searchByEmployee_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetLeaveListRequest.builder()
                    .employeeId("E001")
                    .build();

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_L005");
        }

        @Test
        @DisplayName("ATT_L006: 依日期範圍查詢")
        void searchByDateRange_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetLeaveListRequest.builder()
                    .startDate(LocalDate.parse("2025-01-01"))
                    .endDate(LocalDate.parse("2025-01-31"))
                    .build();

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_L006");
        }

        @Test
        @DisplayName("ATT_L009: 查詢病假紀錄")
        void searchSickLeave_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetLeaveListRequest.builder()
                    .leaveType("SICK")
                    .build();

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_L009");
        }

        @Test
        @DisplayName("ATT_L010: 查詢特休假紀錄")
        void searchAnnualLeave_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetLeaveListRequest.builder()
                    .leaveType("ANNUAL")
                    .build();

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_L010");
        }
    }

    // ========================================================================
    // 3. 加班申請查詢合約 (Overtime Request Query Contract)
    // ========================================================================
    @Nested
    @DisplayName("加班申請查詢合約")
    class OvertimeRequestQueryContractTests {

        private final OvertimeQueryAssembler assembler = new OvertimeQueryAssembler();

        @Test
        @DisplayName("ATT_O001: 查詢待審核加班")
        void searchPendingOvertime_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetOvertimeListRequest.builder()
                    .status("PENDING")
                    .build();

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_O001");
        }

        @Test
        @DisplayName("ATT_O002: 查詢已核准加班")
        void searchApprovedOvertime_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetOvertimeListRequest.builder()
                    .status("APPROVED")
                    .build();

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_O002");
        }

        @Test
        @DisplayName("ATT_O003: 依員工查詢加班")
        void searchByEmployee_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetOvertimeListRequest.builder()
                    .employeeId("E001")
                    .build();

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_O003");
        }

        @Test
        @DisplayName("ATT_O004: 依加班類型查詢")
        void searchByOvertimeType_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetOvertimeListRequest.builder()
                    .overtimeType("WORKDAY")
                    .build();

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_O004");
        }

        @Test
        @DisplayName("ATT_O005: 查詢假日加班")
        void searchHolidayOvertime_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetOvertimeListRequest.builder()
                    .overtimeType("HOLIDAY")
                    .build();

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_O005");
        }

        @Test
        @DisplayName("ATT_O008: 依日期範圍查詢加班")
        void searchByDateRange_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetOvertimeListRequest.builder()
                    .startDate(LocalDate.parse("2025-01-01"))
                    .build();

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_O008");
        }
    }

    // ========================================================================
    // 4. 假別餘額查詢合約 (Leave Balance Query Contract)
    // ========================================================================
    @Nested
    @DisplayName("假別餘額查詢合約")
    class LeaveBalanceQueryContractTests {

        private final LeaveBalanceQueryAssembler assembler = new LeaveBalanceQueryAssembler();

        @Test
        @DisplayName("ATT_B001: 查詢員工假別餘額")
        void searchEmployeeBalance_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetLeaveBalanceRequest.builder()
                    .employeeId("E001")
                    .year(2025)
                    .build();

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_B001");
        }

        @Test
        @DisplayName("ATT_B002: 查詢特定假別餘額")
        void searchByLeaveType_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetLeaveBalanceRequest.builder()
                    .leaveType("ANNUAL")
                    .year(2025)
                    .build();

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_B002");
        }

        @Test
        @DisplayName("ATT_B004: 查詢部門假別餘額")
        void searchDeptBalance_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetLeaveBalanceRequest.builder()
                    .deptId("D001")
                    .year(2025)
                    .build();

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_B004");
        }
    }

    // ========================================================================
    // 5. 班別查詢合約 (Shift Query Contract)
    // ========================================================================
    @Nested
    @DisplayName("班別查詢合約")
    class ShiftQueryContractTests {

        private final ShiftQueryAssembler assembler = new ShiftQueryAssembler();

        @Test
        @DisplayName("ATT_S001: 查詢啟用中班別")
        void searchActiveShifts_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = new ShiftQueryRequest(null, null, true);

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_S001");
        }

        @Test
        @DisplayName("ATT_S002: 查詢停用班別")
        void searchInactiveShifts_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = new ShiftQueryRequest(null, null, false);

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_S002");
        }

        @Test
        @DisplayName("ATT_S003: 依組織查詢班別")
        void searchByOrganization_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = new ShiftQueryRequest("ORG001", null, null);

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_S003");
        }

        @Test
        @DisplayName("ATT_S004: 依班別類型查詢")
        void searchByShiftType_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = new ShiftQueryRequest(null, "NORMAL", null);

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_S004");
        }

        @Test
        @DisplayName("ATT_S005: 查詢彈性班別")
        void searchFlexibleShifts_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = new ShiftQueryRequest(null, "FLEXIBLE", null);

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_S005");
        }
    }

    // ========================================================================
    // 6. 假別查詢合約 (Leave Type Query Contract)
    // ========================================================================
    @Nested
    @DisplayName("假別查詢合約")
    class LeaveTypeQueryContractTests {

        private final LeaveTypeQueryAssembler assembler = new LeaveTypeQueryAssembler();

        @Test
        @DisplayName("ATT_T001: 查詢啟用中假別")
        void searchActiveLeaveTypes_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = new LeaveTypeQueryRequest(null, null, true);

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_T001");
        }

        @Test
        @DisplayName("ATT_T002: 查詢支薪假別")
        void searchPaidLeaveTypes_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = new LeaveTypeQueryRequest(null, true, null);

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_T002");
        }

        @Test
        @DisplayName("ATT_T003: 查詢無薪假別")
        void searchUnpaidLeaveTypes_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = new LeaveTypeQueryRequest(null, false, null);

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_T003");
        }

        @Test
        @DisplayName("ATT_T004: 依組織查詢假別")
        void searchByOrganization_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = new LeaveTypeQueryRequest("ORG001", null, null);

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_T004");
        }
    }

    // ========================================================================
    // 7. 補卡申請查詢合約 (Correction Query Contract)
    // ========================================================================
    @Nested
    @DisplayName("補卡申請查詢合約")
    class CorrectionQueryContractTests {

        private final CorrectionQueryAssembler assembler = new CorrectionQueryAssembler();

        @Test
        @DisplayName("ATT_C001: 查詢待審核補卡")
        void searchPendingCorrections_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = new CorrectionQueryRequest(null, "PENDING", null, null);

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_C001");
        }

        @Test
        @DisplayName("ATT_C002: 查詢已核准補卡")
        void searchApprovedCorrections_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = new CorrectionQueryRequest(null, "APPROVED", null, null);

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_C002");
        }

        @Test
        @DisplayName("ATT_C003: 依員工查詢補卡")
        void searchByEmployee_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = new CorrectionQueryRequest("E001", null, null, null);

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_C003");
        }

        @Test
        @DisplayName("ATT_C004: 依日期範圍查詢補卡")
        void searchByDateRange_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = new CorrectionQueryRequest(
                    null, null,
                    LocalDate.parse("2025-01-01"),
                    LocalDate.parse("2025-01-31"));

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_C004");
        }
    }

    // ========================================================================
    // 8. 報表查詢合約 (Report Query Contract)
    // ========================================================================
    @Nested
    @DisplayName("報表查詢合約")
    class ReportQueryContractTests {

        private final ReportQueryAssembler assembler = new ReportQueryAssembler();

        @Test
        @DisplayName("ATT_R001: 查詢月報表")
        void searchMonthlyReport_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = new MonthlyReportQueryRequest("ORG001", 2025, 1, null);

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_R001");
        }

        @Test
        @DisplayName("ATT_R002: 查詢部門月報表")
        void searchDeptMonthlyReport_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = new MonthlyReportQueryRequest("ORG001", 2025, 1, "D001");

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_R002");
        }

        @Test
        @DisplayName("ATT_R003: 查詢日報表")
        void searchDailyReport_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = new DailyReportQueryRequest(
                    "ORG001", LocalDate.parse("2025-01-15"), null);

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_R003");
        }

        @Test
        @DisplayName("ATT_R004: 查詢部門日報表")
        void searchDeptDailyReport_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = new DailyReportQueryRequest(
                    "ORG001", LocalDate.parse("2025-01-15"), "D001");

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_R004");
        }
    }
}
