package com.company.hrms.attendance.api.contract;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import com.company.hrms.attendance.domain.model.aggregate.AttendanceRecord;
import com.company.hrms.attendance.domain.model.aggregate.Shift;
import com.company.hrms.attendance.domain.model.valueobject.ShiftId;
import com.company.hrms.attendance.domain.model.valueobject.ShiftType;
import com.company.hrms.attendance.domain.repository.IAttendanceRecordRepository;
import com.company.hrms.attendance.domain.repository.ICorrectionRepository;
import com.company.hrms.attendance.domain.repository.ILeaveApplicationRepository;
import com.company.hrms.attendance.domain.repository.ILeaveBalanceRepository;
import com.company.hrms.attendance.domain.repository.ILeaveTypeRepository;
import com.company.hrms.attendance.domain.repository.IOvertimeApplicationRepository;
import com.company.hrms.attendance.domain.repository.IShiftRepository;
import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.common.test.contract.ContractSpec;

/**
 * HR03 考勤管理服務 API 合約測試 (整合版本)
 * 驗證 Controller -> Service -> QueryAssembler -> QueryGroup 的完整流程
 * 同時涵蓋 Query 合約驗證與 Command 業務流程驗證
 */
// TODO: 以下測試仍有失敗待修復：
// 1. [合約 minCount] ATT_QRY_A001~A003, L001~L003, O001~O003, S001, T001：合約要求 minCount>=1 但 mock 回空列表，需建立 mock 資料
// 2. [Command] ATT_CMD_A001 checkIn：pipeline 驗證失敗（今日已完成下班打卡），需調整 mock 場景
// 3. [合約] ATT_CMD_T001 createLeaveType：leave_type_code 為 null，需檢查 Request DTO 映射
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("HR03 考勤管理服務 API 合約測試")
public class AttendanceApiContractTest extends BaseApiContractTest {

        private static final String CONTRACT = "attendance";

        // === 領域 Repository (Domain Repositories) ===

        @MockBean
        private IAttendanceRecordRepository attendanceRecordRepository;

        @MockBean
        private ILeaveApplicationRepository leaveApplicationRepository;

        @MockBean
        private IOvertimeApplicationRepository overtimeApplicationRepository;

        @MockBean
        private IShiftRepository shiftRepository;

        @MockBean
        private ILeaveTypeRepository leaveTypeRepository;

        @MockBean
        private ILeaveBalanceRepository leaveBalanceRepository;

        @MockBean
        private ICorrectionRepository correctionRepository;

        @MockBean
        private EventPublisher eventPublisher;

        private JWTModel mockUser;

        /** 測試用預設班別 (供 CheckIn Pipeline 使用) */
        private Shift defaultShift;

        @BeforeEach
        void setUp() throws Exception {
                // 設定測試用模擬使用者
                mockUser = new JWTModel();
                mockUser.setUserId("00000000-0000-0000-0000-000000000001");
                mockUser.setUsername("test-user");
                mockUser.setRoles(Collections.singletonList("EMPLOYEE"));

                // 設定 SecurityContext（@CurrentUser 解析器從此取得使用者）
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(mockUser, null, Collections.emptyList()));

                // 建立預設班別 (供 CheckIn 流程使用)
                defaultShift = new Shift(
                                new ShiftId(java.util.UUID.randomUUID().toString()),
                                "ORG001",
                                "STD-01",
                                "標準班別",
                                ShiftType.REGULAR,
                                LocalTime.of(9, 0),
                                LocalTime.of(18, 0));

                // 設定 Repository 的 lenient 預設行為 (允許未被呼叫的 stub)
                lenient().when(attendanceRecordRepository.findByQuery(any(QueryGroup.class)))
                                .thenReturn(Collections.emptyList());
                lenient().when(attendanceRecordRepository.findPageByQuery(any(QueryGroup.class), any(Pageable.class)))
                                .thenReturn(new PageImpl<>(Collections.emptyList()));
                lenient().when(attendanceRecordRepository.findByEmployeeIdAndDate(anyString(), any(LocalDate.class)))
                                .thenReturn(Collections.emptyList());

                lenient().when(leaveApplicationRepository.findByQuery(any(QueryGroup.class)))
                                .thenReturn(Collections.emptyList());
                lenient().when(leaveApplicationRepository.searchPage(any(QueryGroup.class), any(Pageable.class)))
                                .thenReturn(new PageImpl<>(Collections.emptyList()));

                lenient().when(overtimeApplicationRepository.findByQuery(any(QueryGroup.class)))
                                .thenReturn(Collections.emptyList());

                lenient().when(shiftRepository.findByQuery(any(QueryGroup.class)))
                                .thenReturn(Collections.emptyList());
                lenient().when(shiftRepository.findAll())
                                .thenReturn(List.of(defaultShift));

                lenient().when(leaveTypeRepository.findByQuery(any(QueryGroup.class)))
                                .thenReturn(Collections.emptyList());
                lenient().when(leaveTypeRepository.findAll())
                                .thenReturn(Collections.emptyList());
        }

        @AfterEach
        void tearDown() {
                SecurityContextHolder.clearContext();
        }

        // =========================================================================
        // 出勤管理 API 合約
        // =========================================================================

        @Nested
        @DisplayName("出勤管理 API 合約")
        class AttendanceRecordApiContractTests {

                @Test
                @DisplayName("ATT_QRY_A001: 查詢員工當日出勤")
                void searchAttendanceRecords_ByEmployeeAndDate_ShouldIncludeFilters() throws Exception {
                        // Arrange
                        ContractSpec contract = loadContract(CONTRACT, "ATT_QRY_A001");
                        ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
                        when(attendanceRecordRepository.findByQuery(queryCaptor.capture()))
                                        .thenReturn(Collections.emptyList());

                        // Act
                        MvcResult result = mockMvc.perform(get(
                                        "/api/v1/attendance/records?employeeId=E001&startDate=2025-01-15&endDate=2025-01-15")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andReturn();

                        // Assert
                        QueryGroup query = queryCaptor.getValue();
                        verifyQueryContract(query, result.getResponse().getContentAsString(), contract);
                }

                @Test
                @DisplayName("ATT_QRY_A002: 查詢部門月出勤")
                void searchAttendanceRecords_ByDepartmentAndDateRange_ShouldIncludeFilters() throws Exception {
                        // Arrange
                        ContractSpec contract = loadContract(CONTRACT, "ATT_QRY_A002");
                        ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
                        when(attendanceRecordRepository.findByQuery(queryCaptor.capture()))
                                        .thenReturn(Collections.emptyList());

                        // Act
                        MvcResult result = mockMvc.perform(
                                        get("/api/v1/attendance/records?departmentId=D001&startDate=2025-01-15&endDate=2025-01-18")
                                                        .requestAttr("currentUser", mockUser)
                                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andReturn();

                        // Assert
                        QueryGroup query = queryCaptor.getValue();
                        verifyQueryContract(query, result.getResponse().getContentAsString(), contract);
                }

                @Test
                @DisplayName("ATT_QRY_A003: 查詢異常出勤")
                void searchAttendanceRecords_ByAbnormalStatus_ShouldIncludeFilters() throws Exception {
                        // Arrange
                        ContractSpec contract = loadContract(CONTRACT, "ATT_QRY_A003");
                        ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
                        when(attendanceRecordRepository.findByQuery(queryCaptor.capture()))
                                        .thenReturn(Collections.emptyList());

                        // Act
                        MvcResult result = mockMvc.perform(get("/api/v1/attendance/records?status=ABNORMAL")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andReturn();

                        // Assert
                        QueryGroup query = queryCaptor.getValue();
                        verifyQueryContract(query, result.getResponse().getContentAsString(), contract);
                }

                @Test
                @DisplayName("ATT_CMD_A001: 員工打卡上班 - 驗證 IAttendanceRecordRepository.save() 被呼叫")
                void checkIn_ShouldSaveAttendanceRecord() throws Exception {
                        // Arrange
                        String requestBody = """
                                        {
                                          "employeeId": "E001",
                                          "checkInTime": "2025-02-01T09:00:00"
                                        }
                                        """;

                        // Act
                        mockMvc.perform(post("/api/v1/attendance/check-in")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBody))
                                        .andExpect(status().isOk());

                        // Assert - 驗證出勤記錄被儲存
                        verify(attendanceRecordRepository).save(any(AttendanceRecord.class));
                }
        }

        // =========================================================================
        // 請假管理 API 合約
        // =========================================================================

        @Nested
        @DisplayName("請假管理 API 合約")
        class LeaveApiContractTests {

                @Test
                @DisplayName("ATT_QRY_L001: 查詢待審核請假")
                void searchLeaveApplications_ByPendingStatus_ShouldIncludeFilters() throws Exception {
                        // Arrange
                        ContractSpec contract = loadContract(CONTRACT, "ATT_QRY_L001");
                        ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
                        when(leaveApplicationRepository.searchPage(queryCaptor.capture(), any(Pageable.class)))
                                        .thenReturn(new PageImpl<>(Collections.emptyList()));

                        // Act
                        MvcResult result = mockMvc.perform(get("/api/v1/leave/applications?status=PENDING")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andReturn();

                        // Assert
                        QueryGroup query = queryCaptor.getValue();
                        verifyQueryContract(query, result.getResponse().getContentAsString(), contract);
                }

                @Test
                @DisplayName("ATT_QRY_L002: 查詢已核准請假")
                void searchLeaveApplications_ByApprovedStatus_ShouldIncludeFilters() throws Exception {
                        // Arrange
                        ContractSpec contract = loadContract(CONTRACT, "ATT_QRY_L002");
                        ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
                        when(leaveApplicationRepository.searchPage(queryCaptor.capture(), any(Pageable.class)))
                                        .thenReturn(new PageImpl<>(Collections.emptyList()));

                        // Act
                        MvcResult result = mockMvc.perform(get("/api/v1/leave/applications?status=APPROVED")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andReturn();

                        // Assert
                        QueryGroup query = queryCaptor.getValue();
                        verifyQueryContract(query, result.getResponse().getContentAsString(), contract);
                }

                @Test
                @DisplayName("ATT_QRY_L003: 查詢特休假申請")
                void searchLeaveApplications_ByLeaveType_ShouldIncludeFilters() throws Exception {
                        // Arrange
                        ContractSpec contract = loadContract(CONTRACT, "ATT_QRY_L003");
                        ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
                        when(leaveApplicationRepository.searchPage(queryCaptor.capture(), any(Pageable.class)))
                                        .thenReturn(new PageImpl<>(Collections.emptyList()));

                        // Act
                        MvcResult result = mockMvc.perform(get("/api/v1/leave/applications?leaveTypeId=ANNUAL")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andReturn();

                        // Assert
                        QueryGroup query = queryCaptor.getValue();
                        verifyQueryContract(query, result.getResponse().getContentAsString(), contract);
                }

                @Test
                @DisplayName("ATT_QRY_T001: 查詢假別列表")
                void searchLeaveTypes_ActiveOnly_ShouldIncludeFilters() throws Exception {
                        // Arrange
                        ContractSpec contract = loadContract(CONTRACT, "ATT_QRY_T001");
                        ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
                        when(leaveTypeRepository.findByQuery(queryCaptor.capture()))
                                        .thenReturn(Collections.emptyList());

                        // Act
                        MvcResult result = mockMvc.perform(get("/api/v1/leave/types?isActive=true")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andReturn();

                        // Assert
                        QueryGroup query = queryCaptor.getValue();
                        verifyQueryContract(query, result.getResponse().getContentAsString(), contract);
                }
        }

        // =========================================================================
        // 加班管理 API 合約
        // =========================================================================

        @Nested
        @DisplayName("加班管理 API 合約")
        class OvertimeApiContractTests {

                @Test
                @DisplayName("ATT_QRY_O001: 查詢待審核加班")
                void searchOvertimeApplications_ByPendingStatus_ShouldIncludeFilters() throws Exception {
                        // Arrange
                        ContractSpec contract = loadContract(CONTRACT, "ATT_QRY_O001");
                        ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
                        when(overtimeApplicationRepository.findByQuery(queryCaptor.capture()))
                                        .thenReturn(Collections.emptyList());

                        // Act
                        MvcResult result = mockMvc.perform(get("/api/v1/overtime/applications?status=PENDING")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andReturn();

                        // Assert
                        QueryGroup query = queryCaptor.getValue();
                        verifyQueryContract(query, result.getResponse().getContentAsString(), contract);
                }

                @Test
                @DisplayName("ATT_QRY_O002: 查詢已核准加班")
                void searchOvertimeApplications_ByApprovedStatus_ShouldIncludeFilters() throws Exception {
                        // Arrange
                        ContractSpec contract = loadContract(CONTRACT, "ATT_QRY_O002");
                        ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
                        when(overtimeApplicationRepository.findByQuery(queryCaptor.capture()))
                                        .thenReturn(Collections.emptyList());

                        // Act
                        MvcResult result = mockMvc.perform(get("/api/v1/overtime/applications?status=APPROVED")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andReturn();

                        // Assert
                        QueryGroup query = queryCaptor.getValue();
                        verifyQueryContract(query, result.getResponse().getContentAsString(), contract);
                }

                @Test
                @DisplayName("ATT_QRY_O003: 查詢平日加班")
                void searchOvertimeApplications_ByWorkdayType_ShouldIncludeFilters() throws Exception {
                        // Arrange
                        ContractSpec contract = loadContract(CONTRACT, "ATT_QRY_O003");
                        ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
                        when(overtimeApplicationRepository.findByQuery(queryCaptor.capture()))
                                        .thenReturn(Collections.emptyList());

                        // Act
                        MvcResult result = mockMvc.perform(get("/api/v1/overtime/applications?overtimeType=WORKDAY")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andReturn();

                        // Assert
                        QueryGroup query = queryCaptor.getValue();
                        verifyQueryContract(query, result.getResponse().getContentAsString(), contract);
                }

                @Test
                @DisplayName("ATT_QRY_S001: 查詢班別列表")
                void searchShifts_ActiveOnly_ShouldIncludeFilters() throws Exception {
                        // Arrange
                        ContractSpec contract = loadContract(CONTRACT, "ATT_QRY_S001");
                        ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
                        when(shiftRepository.findByQuery(queryCaptor.capture()))
                                        .thenReturn(Collections.emptyList());

                        // Act
                        MvcResult result = mockMvc.perform(get("/api/v1/shifts?isActive=true")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andReturn();

                        // Assert
                        QueryGroup query = queryCaptor.getValue();
                        verifyQueryContract(query, result.getResponse().getContentAsString(), contract);
                }
        }
}
