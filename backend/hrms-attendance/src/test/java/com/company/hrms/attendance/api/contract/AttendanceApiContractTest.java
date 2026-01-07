package com.company.hrms.attendance.api.contract;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

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
 * <b>使用基類:</b> BaseApiContractTest (待服務實作後切換)
 * <p>
 * <b>角色模擬:</b> 支援 {@code @WithMockUser} 進行角色權限驗證
 *
 * <h3>合約規格</h3>
 * <p>
 * 合約規格定義於 {@code src/test/resources/contracts/attendance_contracts.md}
 *
 * <h3>測試流程</h3>
 * <ol>
 * <li>載入 SA 定義的合約規格 (Markdown)</li>
 * <li>模擬 HTTP 請求 (MockMvc)</li>
 * <li>攔截 Service 產出的 QueryGroup</li>
 * <li>驗證 QueryGroup 符合合約定義的過濾條件</li>
 * </ol>
 *
 * <h3>啟用條件</h3>
 * <p>
 * 此測試需要以下條件方可啟用：
 * <ul>
 * <li>Query Service 實作完成 (如 GetAttendanceListServiceImpl)</li>
 * <li>新增 spring-security-test 依賴以支援 @WithMockUser</li>
 * <li>Repository 實作繼承 BaseRepository 並提供 findPage 方法</li>
 * </ul>
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
 * <td>支援 @WithMockUser</td>
 * </tr>
 * <tr>
 * <td>執行速度</td>
 * <td>極快 (毫秒級)</td>
 * <td>較慢 (需啟動 Spring Context)</td>
 * </tr>
 * </table>
 *
 * @see com.company.hrms.attendance.application.service.contract.AttendanceContractTest
 *      Assembler 層級合約測試
 * @see BaseContractTest 合約測試基類
 */
@ActiveProfiles("test")
@DisplayName("HR03 考勤服務 API 合約測試")
@Disabled("等待 Query Service 與 Repository.findPage() 實作完成後啟用")
public class AttendanceApiContractTest extends BaseContractTest {

    private static final String CONTRACT = "attendance";
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

        /**
         * ATT_A001: HR 查詢員工當日出勤
         *
         * <p>
         * 實作步驟：
         * <ol>
         * <li>新增 @WithMockUser(roles = "HR") 註解</li>
         * <li>使用 MockMvc.performGet() 發送請求</li>
         * <li>使用 ArgumentCaptor 攔截 QueryGroup</li>
         * <li>呼叫 assertContract() 驗證合約</li>
         * </ol>
         */
        @Test
        @DisplayName("ATT_A001: HR 查詢員工當日出勤")
        void searchDailyAttendance_AsHR_ShouldIncludeFilters() throws Exception {
            // TODO: 當 Query Service 實作完成後，實作完整測試
            // 範例程式碼：
            // ArgumentCaptor<QueryGroup> captor = createQueryGroupCaptor();
            // performGet("/api/v1/attendance/records?employeeId=E001&startDate=2025-01-15&endDate=2025-01-15")
            // .andExpect(status().isOk());
            // verify(attendanceRecordRepository).findPage(captor.capture(), any());
            // assertContract(captor.getValue(), contractSpec, "ATT_A001");
        }

        @Test
        @DisplayName("ATT_A002: HR 查詢部門月出勤")
        void searchDeptMonthlyAttendance_AsHR_ShouldIncludeFilters() throws Exception {
            // TODO: 待實作
        }

        @Test
        @DisplayName("ATT_A006: 員工查詢自己出勤 - 應自動加上員工ID過濾")
        void searchOwnAttendance_AsEmployee_ShouldAutoAddEmployeeFilter() throws Exception {
            // TODO: 待實作
            // 驗證重點：員工不傳 employeeId，系統應自動根據登入身份加上過濾
        }

        @Test
        @DisplayName("ATT_A007: 主管查詢下屬出勤 - 應限制在所管轄部門")
        void searchSubordinateAttendance_AsManager_ShouldLimitToDepartment() throws Exception {
            // TODO: 待實作
            // 驗證重點：QueryGroup 包含 department_id IN (管轄部門)
        }
    }

    // ========================================================================
    // 2. 請假申請查詢 API 合約 (Leave Request Query API Contract)
    // ========================================================================
    @Nested
    @DisplayName("請假申請查詢 API 合約")
    class LeaveRequestApiContractTests {

        @Test
        @DisplayName("ATT_L001: HR 查詢待審核請假")
        void searchPendingLeave_AsHR_ShouldIncludeFilters() throws Exception {
            // TODO: 待實作
        }

        @Test
        @DisplayName("ATT_L007: 員工查詢自己請假 - 應自動加上員工ID過濾")
        void searchOwnLeave_AsEmployee_ShouldAutoAddEmployeeFilter() throws Exception {
            // TODO: 待實作
        }

        @Test
        @DisplayName("ATT_L008: 主管查詢待審核請假 - 應限制在所管轄部門")
        void searchPendingLeave_AsManager_ShouldLimitToDepartment() throws Exception {
            // TODO: 待實作
        }
    }

    // ========================================================================
    // 3. 加班申請查詢 API 合約 (Overtime Request Query API Contract)
    // ========================================================================
    @Nested
    @DisplayName("加班申請查詢 API 合約")
    class OvertimeRequestApiContractTests {

        @Test
        @DisplayName("ATT_O001: HR 查詢待審核加班")
        void searchPendingOvertime_AsHR_ShouldIncludeFilters() throws Exception {
            // TODO: 待實作
        }

        @Test
        @DisplayName("ATT_O006: 員工查詢自己加班 - 應自動加上員工ID過濾")
        void searchOwnOvertime_AsEmployee_ShouldAutoAddEmployeeFilter() throws Exception {
            // TODO: 待實作
        }

        @Test
        @DisplayName("ATT_O007: 主管查詢待審核加班 - 應限制在所管轄部門")
        void searchPendingOvertime_AsManager_ShouldLimitToDepartment() throws Exception {
            // TODO: 待實作
        }
    }

    // ========================================================================
    // 4. 補卡申請查詢 API 合約 (Correction Query API Contract)
    // ========================================================================
    @Nested
    @DisplayName("補卡申請查詢 API 合約")
    class CorrectionApiContractTests {

        @Test
        @DisplayName("ATT_C001: HR 查詢待審核補卡")
        void searchPendingCorrections_AsHR_ShouldIncludeFilters() throws Exception {
            // TODO: 待實作
        }

        @Test
        @DisplayName("ATT_C005: 員工查詢自己補卡 - 應自動加上員工ID過濾")
        void searchOwnCorrections_AsEmployee_ShouldAutoAddEmployeeFilter() throws Exception {
            // TODO: 待實作
        }
    }

    // ========================================================================
    // 5. 權限邊界測試 (Permission Boundary Tests)
    // ========================================================================
    @Nested
    @DisplayName("權限邊界測試")
    class PermissionBoundaryTests {

        @Test
        @DisplayName("ATT_SEC_001: 員工不能查詢其他員工的出勤記錄")
        void employee_CannotQueryOthersAttendance() throws Exception {
            // TODO: 待實作
            // 預期: HTTP 403 Forbidden
        }

        @Test
        @DisplayName("ATT_SEC_003: 主管不能查詢非管轄部門的請假")
        void manager_CannotQueryOtherDeptLeave() throws Exception {
            // TODO: 待實作
            // 預期: HTTP 403 Forbidden
        }

        @Test
        @DisplayName("ATT_SEC_005: 未授權用戶不能存取 API")
        void anonymous_CannotAccessApi() throws Exception {
            // TODO: 待實作
            // 預期: HTTP 401 Unauthorized
        }
    }

    // ========================================================================
    // 6. 命令操作合約 (Command Operation Contract)
    // ========================================================================
    @Nested
    @DisplayName("命令操作合約")
    class CommandOperationContractTests {

        @Test
        @DisplayName("ATT_CMD_001: 員工打卡上班")
        void clockIn_AsEmployee_ShouldCreateRecord() throws Exception {
            // TODO: 待實作
            // 驗證: 應建立出勤記錄, clock_in_time 不為空
        }

        @Test
        @DisplayName("ATT_CMD_003: 員工申請請假")
        void applyLeave_AsEmployee_ShouldCreateApplication() throws Exception {
            // TODO: 待實作
            // 驗證: 應建立請假申請, status = 'PENDING'
        }

        @Test
        @DisplayName("ATT_CMD_004: 主管核准請假")
        void approveLeave_AsManager_ShouldUpdateStatus() throws Exception {
            // TODO: 待實作
            // 驗證: 應更新申請, status = 'APPROVED', approver_id 不為空
        }
    }
}
