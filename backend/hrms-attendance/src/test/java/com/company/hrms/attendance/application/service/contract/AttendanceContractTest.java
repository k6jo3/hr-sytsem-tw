package com.company.hrms.attendance.application.service.contract;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.attendance.api.request.attendance.GetAttendanceListRequest;
import com.company.hrms.attendance.api.request.attendance.GetLeaveBalanceRequest;
import com.company.hrms.attendance.api.request.leave.GetLeaveListRequest;
import com.company.hrms.attendance.api.request.overtime.GetOvertimeListRequest;
import com.company.hrms.attendance.application.service.checkin.assembler.AttendanceQueryAssembler;
import com.company.hrms.attendance.application.service.checkin.assembler.LeaveBalanceQueryAssembler;
import com.company.hrms.attendance.application.service.leave.assembler.LeaveQueryAssembler;
import com.company.hrms.attendance.application.service.overtime.assembler.OvertimeQueryAssembler;
import com.company.hrms.common.test.contract.BaseContractTest;

/**
 * 考勤服務合約測試
 */
@DisplayName("考勤服務合約測試")
public class AttendanceContractTest extends BaseContractTest {

    @Nested
    @DisplayName("出勤紀錄查詢合約")
    class AttendanceRecordQueryContractTests {

        private final AttendanceQueryAssembler assembler = new AttendanceQueryAssembler();
        private final String CONTRACT = "attendance";

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

    @Nested
    @DisplayName("請假申請查詢合約")
    class LeaveRequestQueryContractTests {

        private final LeaveQueryAssembler assembler = new LeaveQueryAssembler();
        private final String CONTRACT = "attendance";

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
    }

    @Nested
    @DisplayName("加班申請查詢合約")
    class OvertimeRequestQueryContractTests {

        private final OvertimeQueryAssembler assembler = new OvertimeQueryAssembler();
        private final String CONTRACT = "attendance";

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
        @DisplayName("ATT_O004: 依加班類型查詢")
        void searchByOvertimeType_ShouldIncludeFilters() throws Exception {
            String contract = loadContractSpec(CONTRACT);
            var request = GetOvertimeListRequest.builder()
                    .overtimeType("WORKDAY")
                    .build();

            var query = assembler.toQueryGroup(request);
            assertContract(query, contract, "ATT_O004");
        }
    }

    @Nested
    @DisplayName("假別餘額查詢合約")
    class LeaveBalanceQueryContractTests {

        private final LeaveBalanceQueryAssembler assembler = new LeaveBalanceQueryAssembler();
        private final String CONTRACT = "attendance";

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
    }
}
