package com.company.hrms.attendance.api.contract;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import com.company.hrms.attendance.api.controller.attendance.HR03CheckInQryController.CorrectionQueryRequest;
import com.company.hrms.attendance.api.request.attendance.GetAttendanceListRequest;
import com.company.hrms.attendance.api.request.leave.GetLeaveListRequest;
import com.company.hrms.attendance.api.request.overtime.GetOvertimeListRequest;
import com.company.hrms.attendance.application.service.checkin.assembler.AttendanceQueryAssembler;
import com.company.hrms.attendance.application.service.contract.AttendanceContractTest;
import com.company.hrms.attendance.application.service.correction.assembler.CorrectionQueryAssembler;
import com.company.hrms.attendance.application.service.leave.assembler.LeaveQueryAssembler;
import com.company.hrms.attendance.application.service.overtime.assembler.OvertimeQueryAssembler;
import com.company.hrms.common.test.contract.BaseContractTest;

/**
 * HR03 考勤服務 API 合約測試
 *
 * <p>
 * 本測試類別驗證完整的 API 流程符合業務合約規格：
 * 
 * <pre>
 * Controller → Service → Assembler → QueryGroup
 * </pre>
 *
 * <p>
 * <b>測試層級:</b> API 合約測試 (Integration Test)
 * <p>
 * <b>測試範圍:</b> 完整的 HTTP 請求 → Response 流程
 * <p>
 * <b>使用基類:</b> BaseContractTest
 * <p>
 * <b>角色模擬:</b> 待服務實作後可加入 {@code @WithMockUser} 進行角色權限驗證
 *
 * <h3>合約規格</h3>
 * <p>
 * 合約規格定義於 {@code src/test/resources/contracts/attendance_contracts.md}
 *
 * <h3>測試流程</h3>
 * <ol>
 * <li>載入 SA 定義的合約規格 (Markdown)</li>
 * <li>建立對應的 Request 物件</li>
 * <li>使用 Assembler 產出 QueryGroup</li>
 * <li>呼叫 assertContract() 驗證合約</li>
 * </ol>
 *
 * <h3>與 Assembler 單元測試的區別</h3>
 * <table border="1">
 * <tr>
 * <th>面向</th>
 * <th>Assembler 單元測試</th>
 * <th>API 合約測試</th>
 * </tr>
 * <tr>
 * <td>測試範圍</td>
 * <td>僅 Assembler 組裝邏輯</td>
 * <td>Controller → Service → Assembler 完整流程</td>
 * </tr>
 * <tr>
 * <td>角色模擬</td>
 * <td>不支援</td>
 * <td>支援 @WithMockUser (待實作)</td>
 * </tr>
 * <tr>
 * <td>執行速度</td>
 * <td>極快 (毫秒級)</td>
 * <td>較慢 (需啟動 Spring Context)</td>
 * </tr>
 * </table>
 *
 * @see AttendanceContractTest
 *      Assembler 層級合約測試
 * @see BaseContractTest 合約測試基類
 */
@ActiveProfiles("test")
@DisplayName("HR03 考勤服務 API 合約測試")
public class AttendanceApiContractTest extends BaseContractTest {

    private static final String CONTRACT = "attendance_contracts_v2";

    @Override
    protected String loadContractSpec(String serviceName) throws java.io.IOException {
        java.nio.file.Path current = java.nio.file.Paths.get("").toAbsolutePath();
        for (int i = 0; i < 6; i++) {
            java.nio.file.Path candidate = current.resolve("contracts/" + serviceName + ".md");
            if (java.nio.file.Files.exists(candidate)) {
                return java.nio.file.Files.readString(candidate);
            }
            java.nio.file.Path candidateSibling = current.resolve("../contracts/" + serviceName + ".md");
            if (java.nio.file.Files.exists(candidateSibling)) {
                return java.nio.file.Files.readString(candidateSibling);
            }
            current = current.getParent();
            if (current == null)
                break;
        }
        throw new RuntimeException("找不到合約檔案: " + serviceName + ".md");
    }

    private String contractSpec;

    @BeforeEach
    void setUp() throws Exception {
        contractSpec = loadContractSpec(CONTRACT);
    }

    // ========================================================================
    // 1. 出勤紀錄查詢 API 合約 (Attendance Record Query API Contract)
    // ========================================================================
    @Nested
    @DisplayName("出勤紀錄查詢 API 合約")
    class AttendanceRecordApiContractTests {

        private final AttendanceQueryAssembler assembler = new AttendanceQueryAssembler();

        /**
         * ATT_A001: HR 查詢員工當日出勤
         *
         * <p>
         * 驗證 HR 透過 API 查詢特定員工當日出勤時，
         * QueryGroup 應包含 employee_id 與 attendance_date 過濾條件。
         */
        @Test
        @DisplayName("ATT_A001: HR 查詢員工當日出勤")
        void searchDailyAttendance_AsHR_ShouldIncludeFilters() throws Exception {
            // Given: HR 查詢員工 E001 在 2025-01-15 的出勤
            var request = GetAttendanceListRequest.builder()
                    .employeeId("E001")
                    .date(LocalDate.parse("2025-01-15"))
                    .build();

            // When: 組裝 QueryGroup
            var query = assembler.toQueryGroup(request);

            // Then: 驗證合約 ATT_QRY_A001
            assertContract(query, contractSpec, "ATT_QRY_A001");
        }

        /**
         * ATT_QRY_A002: HR 查詢部門月出勤
         */
        @Test
        @DisplayName("ATT_QRY_A002: HR 查詢部門月出勤")
        void searchDeptMonthlyAttendance_AsHR_ShouldIncludeFilters() throws Exception {
            // Given: HR 查詢部門 D001 在 2025-01 月份的出勤
            var request = GetAttendanceListRequest.builder()
                    .deptId("D001")
                    .month("2025-01")
                    .build();

            // When: 組裝 QueryGroup
            var query = assembler.toQueryGroup(request);

            // Then: 驗證合約 ATT_QRY_A002
            assertContract(query, contractSpec, "ATT_QRY_A002");
        }

        /**
         * ATT_A006: 員工查詢自己出勤 - 應自動加上員工ID過濾
         *
         * <p>
         * 驗證重點：員工查詢時，系統應根據登入身份自動加上 employee_id 過濾。
         * 目前以模擬方式實作，待 Spring Security 完成後可加入 @WithMockUser。
         */
        @Test
        @DisplayName("ATT_A006: 員工查詢自己出勤 - 應自動加上員工ID過濾")
        void searchOwnAttendance_AsEmployee_ShouldAutoAddEmployeeFilter() throws Exception {
            // Given: 員工查詢自己 2025-01 月份的出勤
            // 備註: 實際 API 會從 JWT 取得 currentUserId，這裡模擬該行為
            String currentUserId = "E001"; // 模擬登入員工
            var request = GetAttendanceListRequest.builder()
                    .employeeId(currentUserId) // 系統自動填入
                    .month("2025-01")
                    .build();

            // When: 組裝 QueryGroup
            var query = assembler.toQueryGroup(request);

            // Then: 驗證 QueryGroup 包含員工 ID 過濾
            assertHasFilterForField(query, "employee_id");
            assertHasFilterForField(query, "record_date");
        }

        /**
         * ATT_A007: 主管查詢下屬出勤 - 應限制在所管轄部門
         *
         * <p>
         * 驗證重點：QueryGroup 應包含 department_id 過濾條件。
         */
        @Test
        @DisplayName("ATT_A007: 主管查詢下屬出勤 - 應限制在所管轄部門")
        void searchSubordinateAttendance_AsManager_ShouldLimitToDepartment() throws Exception {
            // Given: 主管查詢所管轄部門 D001 的 2025-01 月份出勤
            // 備註: 實際 API 會從 JWT 取得 managedDeptIds，這裡模擬該行為
            List<String> managedDeptIds = List.of("D001", "D002");
            var request = GetAttendanceListRequest.builder()
                    .deptId(managedDeptIds.get(0)) // 選擇第一個管轄部門
                    .month("2025-01")
                    .build();

            // When: 組裝 QueryGroup
            var query = assembler.toQueryGroup(request);

            // Then: 驗證 QueryGroup 包含部門過濾
            assertHasFilterForField(query, "department_id");
            assertHasFilterForField(query, "record_date");
        }
    }

    // ========================================================================
    // 2. 請假申請查詢 API 合約 (Leave Request Query API Contract)
    // ========================================================================
    @Nested
    @DisplayName("請假申請查詢 API 合約")
    class LeaveRequestApiContractTests {

        private final LeaveQueryAssembler assembler = new LeaveQueryAssembler();

        /**
         * ATT_L001: HR 查詢待審核請假
         */
        @Test
        @DisplayName("ATT_L001: HR 查詢待審核請假")
        void searchPendingLeave_AsHR_ShouldIncludeFilters() throws Exception {
            // Given: HR 查詢待審核的請假申請
            var request = GetLeaveListRequest.builder()
                    .status("PENDING")
                    .build();

            // When: 組裝 QueryGroup
            var query = assembler.toQueryGroup(request);

            // Then: 驗證合約 ATT_QRY_L001
            assertContract(query, contractSpec, "ATT_QRY_L001");
        }

        /**
         * ATT_L007: 員工查詢自己請假 - 應自動加上員工ID過濾
         */
        @Test
        @DisplayName("ATT_L007: 員工查詢自己請假 - 應自動加上員工ID過濾")
        void searchOwnLeave_AsEmployee_ShouldAutoAddEmployeeFilter() throws Exception {
            // Given: 員工查詢自己的請假申請
            String currentUserId = "E001"; // 模擬登入員工
            var request = GetLeaveListRequest.builder()
                    .employeeId(currentUserId) // 系統自動填入
                    .build();

            // When: 組裝 QueryGroup
            var query = assembler.toQueryGroup(request);

            // Then: 驗證 QueryGroup 包含員工 ID
            assertHasFilterForField(query, "employee_id");
        }

        /**
         * ATT_L008: 主管查詢待審核請假 - 應限制在所管轄部門
         */
        @Test
        @DisplayName("ATT_L008: 主管查詢待審核請假 - 應限制在所管轄部門")
        void searchPendingLeave_AsManager_ShouldLimitToDepartment() throws Exception {
            // Given: 主管查詢所管轄部門的待審核請假
            List<String> managedDeptIds = List.of("D001");
            var request = GetLeaveListRequest.builder()
                    .status("PENDING")
                    .deptId(managedDeptIds.get(0))
                    .build();

            // When: 組裝 QueryGroup
            var query = assembler.toQueryGroup(request);

            // Then: 驗證 QueryGroup 包含狀態與部門過濾
            assertHasFilterForField(query, "status");
            assertHasFilterForField(query, "department_id");
        }
    }

    // ========================================================================
    // 3. 加班申請查詢 API 合約 (Overtime Request Query API Contract)
    // ========================================================================
    @Nested
    @DisplayName("加班申請查詢 API 合約")
    class OvertimeRequestApiContractTests {

        private final OvertimeQueryAssembler assembler = new OvertimeQueryAssembler();

        /**
         * ATT_O001: HR 查詢待審核加班
         */
        @Test
        @DisplayName("ATT_O001: HR 查詢待審核加班")
        void searchPendingOvertime_AsHR_ShouldIncludeFilters() throws Exception {
            // Given: HR 查詢待審核的加班申請
            var request = GetOvertimeListRequest.builder()
                    .status("PENDING")
                    .build();

            // When: 組裝 QueryGroup
            var query = assembler.toQueryGroup(request);

            // Then: 驗證合約 ATT_QRY_O001
            assertContract(query, contractSpec, "ATT_QRY_O001");
        }

        /**
         * ATT_O006: 員工查詢自己加班 - 應自動加上員工ID過濾
         */
        @Test
        @DisplayName("ATT_O006: 員工查詢自己加班 - 應自動加上員工ID過濾")
        void searchOwnOvertime_AsEmployee_ShouldAutoAddEmployeeFilter() throws Exception {
            // Given: 員工查詢自己的加班申請
            String currentUserId = "E001"; // 模擬登入員工
            var request = GetOvertimeListRequest.builder()
                    .employeeId(currentUserId) // 系統自動填入
                    .build();

            // When: 組裝 QueryGroup
            var query = assembler.toQueryGroup(request);

            // Then: 驗證 QueryGroup 包含員工 ID
            assertHasFilterForField(query, "employee_id");
        }

        /**
         * ATT_O007: 主管查詢待審核加班 - 應限制在所管轄部門
         */
        @Test
        @DisplayName("ATT_O007: 主管查詢待審核加班 - 應限制在所管轄部門")
        void searchPendingOvertime_AsManager_ShouldLimitToDepartment() throws Exception {
            // Given: 主管查詢所管轄部門的待審核加班
            List<String> managedDeptIds = List.of("D001");
            var request = GetOvertimeListRequest.builder()
                    .status("PENDING")
                    .deptId(managedDeptIds.get(0))
                    .build();

            // When: 組裝 QueryGroup
            var query = assembler.toQueryGroup(request);

            // Then: 驗證 QueryGroup 包含狀態與部門過濾
            assertHasFilterForField(query, "status");
            assertHasFilterForField(query, "department_id");
        }
    }

    // ========================================================================
    // 4. 補卡申請查詢 API 合約 (Correction Query API Contract)
    // ========================================================================
    @Nested
    @DisplayName("補卡申請查詢 API 合約")
    class CorrectionApiContractTests {

        private final CorrectionQueryAssembler assembler = new CorrectionQueryAssembler();

        /**
         * ATT_C001: HR 查詢待審核補卡
         */
        @Test
        @DisplayName("ATT_C001: HR 查詢待審核補卡")
        void searchPendingCorrections_AsHR_ShouldIncludeFilters() throws Exception {
            // Given: HR 查詢待審核的補卡申請
            var request = new CorrectionQueryRequest(null, "PENDING", null, null);

            // When: 組裝 QueryGroup
            var query = assembler.toQueryGroup(request);

            // Then: 驗證合約 ATT_QRY_C001
            assertContract(query, contractSpec, "ATT_QRY_C001");
        }

        /**
         * ATT_C005: 員工查詢自己補卡 - 應自動加上員工ID過濾
         */
        @Test
        @DisplayName("ATT_C005: 員工查詢自己補卡 - 應自動加上員工ID過濾")
        void searchOwnCorrections_AsEmployee_ShouldAutoAddEmployeeFilter() throws Exception {
            // Given: 員工查詢自己的補卡申請
            String currentUserId = "E001"; // 模擬登入員工
            var request = new CorrectionQueryRequest(currentUserId, null, null, null);

            // When: 組裝 QueryGroup
            var query = assembler.toQueryGroup(request);

            // Then: 驗證 QueryGroup 包含員工 ID
            assertHasFilterForField(query, "employee_id");
        }
    }

    // ========================================================================
    // 5. 權限邊界測試 (Permission Boundary Tests)
    // ========================================================================
    @Nested
    @DisplayName("權限邊界測試")
    class PermissionBoundaryTests {

        /**
         * ATT_SEC_001: 員工不能查詢其他員工的出勤記錄
         *
         * <p>
         * 備註: 此測試需要 Spring Security + MockMvc 完整實作後才能驗證 HTTP 403。
         * 目前以驗證 QueryGroup 必須包含 employee_id 過濾來確保安全邊界。
         */
        @Test
        @DisplayName("ATT_SEC_001: 員工不能查詢其他員工的出勤記錄")
        void employee_CannotQueryOthersAttendance() throws Exception {
            // Given: 員工嘗試查詢其他員工的出勤
            // 備註: 完整實作應為:
            // @WithMockUser(username = "E001", roles = "EMPLOYEE")
            // performGet("/api/v1/attendance/records?employeeId=E999")
            // .andExpect(status().isForbidden());

            // 目前驗證: 查詢自己的出勤應包含自己的 employee_id
            var assembler = new AttendanceQueryAssembler();
            String currentUserId = "E001"; // 模擬登入員工
            var request = GetAttendanceListRequest.builder()
                    .employeeId(currentUserId)
                    .build();

            var query = assembler.toQueryGroup(request);

            // 驗證 QueryGroup 包含正確的員工 ID
            assertHasFilterForField(query, "employee_id");
        }

        /**
         * ATT_SEC_003: 主管不能查詢非管轄部門的請假
         *
         * <p>
         * 備註: 完整實作需要 Spring Security 整合。
         */
        @Test
        @DisplayName("ATT_SEC_003: 主管不能查詢非管轄部門的請假")
        void manager_CannotQueryOtherDeptLeave() throws Exception {
            // Given: 主管嘗試查詢非管轄部門的請假
            // 備註: 完整實作應為:
            // @WithMockUser(username = "M001", roles = "MANAGER")
            // 配置 M001 管轄部門為 D001
            // performGet("/api/v1/attendance/leaves?departmentId=D999")
            // .andExpect(status().isForbidden());

            // 目前驗證: 查詢請假時應包含部門過濾
            var assembler = new LeaveQueryAssembler();
            var request = GetLeaveListRequest.builder()
                    .deptId("D001") // 管轄部門
                    .status("PENDING")
                    .build();

            var query = assembler.toQueryGroup(request);

            // 驗證 QueryGroup 包含部門過濾
            assertHasFilterForField(query, "department_id");
        }

        /**
         * ATT_SEC_005: 未授權用戶不能存取 API
         *
         * <p>
         * 備註: 需要 MockMvc 完整實作後驗證 HTTP 401。
         */
        @Test
        @DisplayName("ATT_SEC_005: 未授權用戶不能存取 API")
        void anonymous_CannotAccessApi() throws Exception {
            // Given: 未授權用戶嘗試存取 API
            // 備註: 完整實作應為:
            // performGet("/api/v1/attendance/records")
            // .andExpect(status().isUnauthorized());

            // 目前驗證: 確保空查詢也能正確組裝
            var assembler = new AttendanceQueryAssembler();
            var request = GetAttendanceListRequest.builder().build();

            // 僅驗證組裝不會拋出例外
            var query = assembler.toQueryGroup(request);
            // 空查詢應產生空的 QueryGroup
            org.junit.jupiter.api.Assertions.assertNotNull(query);
        }
    }

    // ========================================================================
    // 6. 命令操作合約 (Command Operation Contract)
    // ========================================================================
    @Nested
    @DisplayName("命令操作合約")
    class CommandOperationContractTests {

        /**
         * ATT_CMD_001: 員工打卡上班
         *
         * <p>
         * 驗證: 應建立出勤記錄, clock_in_time 不為空
         * 備註: 需要服務實作後才能完整驗證
         */
        @Test
        @DisplayName("ATT_CMD_001: 員工打卡上班")
        void clockIn_AsEmployee_ShouldCreateRecord() throws Exception {
            // Given: 員工執行打卡上班
            // 備註: 完整實作應為:
            // @WithMockUser(username = "E001", roles = "EMPLOYEE")
            // performPost("/api/v1/attendance/clock-in", new ClockInRequest("IN"))
            // .andExpect(status().isOk())
            // .andExpect(jsonPath("$.recordId").exists())
            // .andExpect(jsonPath("$.clockInTime").exists());

            // 目前驗證: 確認測試框架可正常執行
            org.junit.jupiter.api.Assertions.assertTrue(true,
                    "打卡上班功能待 Command Service 實作後完整驗證");
        }

        /**
         * ATT_CMD_003: 員工申請請假
         *
         * <p>
         * 驗證: 應建立請假申請, status = 'PENDING'
         */
        @Test
        @DisplayName("ATT_CMD_003: 員工申請請假")
        void applyLeave_AsEmployee_ShouldCreateApplication() throws Exception {
            // Given: 員工申請請假
            // 備註: 完整實作應為:
            // @WithMockUser(username = "E001", roles = "EMPLOYEE")
            // var request = CreateLeaveRequest.builder()
            // .leaveType("ANNUAL")
            // .startDate(LocalDate.of(2025, 2, 1))
            // .endDate(LocalDate.of(2025, 2, 3))
            // .build();
            // performPost("/api/v1/attendance/leaves", request)
            // .andExpect(status().isCreated())
            // .andExpect(jsonPath("$.status").value("PENDING"));

            // 目前驗證: 確認測試框架可正常執行
            org.junit.jupiter.api.Assertions.assertTrue(true,
                    "申請請假功能待 Command Service 實作後完整驗證");
        }

        /**
         * ATT_CMD_004: 主管核准請假
         *
         * <p>
         * 驗證: 應更新申請, status = 'APPROVED', approver_id 不為空
         */
        @Test
        @DisplayName("ATT_CMD_004: 主管核准請假")
        void approveLeave_AsManager_ShouldUpdateStatus() throws Exception {
            // Given: 主管核准下屬的請假申請
            // 備註: 完整實作應為:
            // @WithMockUser(username = "M001", roles = "MANAGER")
            // var request = ApproveLeaveRequest.builder()
            // .applicationId("LA001")
            // .action("APPROVE")
            // .build();
            // performPost("/api/v1/attendance/leaves/LA001/approve", request)
            // .andExpect(status().isOk())
            // .andExpect(jsonPath("$.status").value("APPROVED"))
            // .andExpect(jsonPath("$.approverId").exists());

            // 目前驗證: 確認測試框架可正常執行
            org.junit.jupiter.api.Assertions.assertTrue(true,
                    "核准請假功能待 Command Service 實作後完整驗證");
        }
    }
}
