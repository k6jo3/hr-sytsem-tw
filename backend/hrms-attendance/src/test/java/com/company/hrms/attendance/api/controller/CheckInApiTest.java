package com.company.hrms.attendance.api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.company.hrms.attendance.api.request.attendance.CheckInRequest;
import com.company.hrms.attendance.api.request.attendance.CheckOutRequest;
import com.company.hrms.attendance.api.request.attendance.CreateCorrectionRequest;
import com.company.hrms.attendance.api.response.attendance.CheckInResponse;
import com.company.hrms.attendance.api.response.attendance.CheckOutResponse;
import com.company.hrms.attendance.api.response.attendance.CreateCorrectionResponse;
import com.company.hrms.attendance.api.response.checkin.AttendanceRecordDetailResponse;
import com.company.hrms.attendance.api.response.checkin.AttendanceRecordListResponse;
import com.company.hrms.attendance.api.response.checkin.CorrectionListResponse;
import com.company.hrms.attendance.application.service.checkin.CheckInServiceImpl;
import com.company.hrms.attendance.application.service.checkin.query.GetAttendanceRecordServiceImpl;
import com.company.hrms.attendance.application.service.checkin.query.GetAttendanceRecordsServiceImpl;
import com.company.hrms.attendance.application.service.checkin.query.GetCorrectionApplicationsServiceImpl;
import com.company.hrms.attendance.application.service.checkout.CheckOutServiceImpl;
import com.company.hrms.attendance.application.service.correction.CreateCorrectionServiceImpl;
import com.company.hrms.common.api.response.PageResponse;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.base.BaseApiContractTest;

/**
 * HR03 打卡管理 API 合約測試 (Mock Service 版)
 *
 * <p>
 * 驗證 Controller 層的請求/回應格式，透過 MockBean 隔離下層服務。
 * </p>
 */
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("HR03 打卡管理 API 合約測試")
class CheckInApiTest extends BaseApiContractTest {

    @MockBean(name = "checkInServiceImpl")
    private CheckInServiceImpl checkInService;

    @MockBean(name = "checkOutServiceImpl")
    private CheckOutServiceImpl checkOutService;

    @MockBean(name = "createCorrectionServiceImpl")
    private CreateCorrectionServiceImpl createCorrectionService;

    @MockBean(name = "getAttendanceRecordsServiceImpl")
    private GetAttendanceRecordsServiceImpl getAttendanceRecordsService;

    @MockBean(name = "getAttendanceRecordServiceImpl")
    private GetAttendanceRecordServiceImpl getAttendanceRecordService;

    @MockBean(name = "getCorrectionApplicationsServiceImpl")
    private GetCorrectionApplicationsServiceImpl getCorrectionApplicationsService;

    @BeforeEach
    void setupSecurity() {
        JWTModel mockUser = new JWTModel();
        mockUser.setUserId("test-emp-001");
        mockUser.setUsername("test_employee");
        mockUser.setRoles(Collections.singletonList("EMPLOYEE"));

        List<SimpleGrantedAuthority> authorities = mockUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("attendance:checkin"));
        authorities.add(new SimpleGrantedAuthority("attendance:checkout"));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                mockUser, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 打卡命令 API 測試
     */
    @Nested
    @DisplayName("打卡命令 API")
    class CheckInCommandApiTests {

        @Test
        @DisplayName("ATT_CHECKIN_001: 上班打卡 - 應返回打卡記錄")
        void checkIn_ShouldReturnRecord() throws Exception {
            // Arrange
            CheckInResponse response = CheckInResponse.success(
                    "record-001",
                    LocalDateTime.of(2026, 2, 1, 9, 0),
                    false,
                    0,
                    "標準班別");

            when(checkInService.execCommand(any(CheckInRequest.class), any(JWTModel.class)))
                    .thenReturn(response);

            CheckInRequest request = new CheckInRequest();
            request.setEmployeeId("test-emp-001");
            request.setCheckInTime(LocalDateTime.of(2026, 2, 1, 9, 0));

            // Act & Assert
            performPost("/api/v1/attendance/check-in", request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.recordId").value("record-001"))
                    .andExpect(jsonPath("$.checkInTime").exists())
                    .andExpect(jsonPath("$.shiftName").value("標準班別"));
        }

        @Test
        @DisplayName("ATT_CHECKIN_002: 上班遲到打卡 - 應顯示遲到資訊")
        void checkIn_Late_ShouldShowLateInfo() throws Exception {
            // Arrange
            CheckInResponse response = CheckInResponse.success(
                    "record-002",
                    LocalDateTime.of(2026, 2, 1, 9, 30),
                    true,
                    30,
                    "標準班別");

            when(checkInService.execCommand(any(CheckInRequest.class), any(JWTModel.class)))
                    .thenReturn(response);

            CheckInRequest request = new CheckInRequest();
            request.setEmployeeId("test-emp-001");
            request.setCheckInTime(LocalDateTime.of(2026, 2, 1, 9, 30));

            // Act & Assert
            // 注意：Lombok @Data 對 boolean isLate 產生 isLate() getter，
            // Jackson 序列化時會將 isXxx() 映射為 JSON 屬性 "late"（去掉 is 前綴）
            performPost("/api/v1/attendance/check-in", request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.late").value(true))
                    .andExpect(jsonPath("$.lateMinutes").value(30));
        }

        @Test
        @DisplayName("ATT_CHECKIN_003: 下班打卡 - 應返回工時資訊")
        void checkOut_ShouldReturnWorkingHours() throws Exception {
            // Arrange
            CheckOutResponse response = CheckOutResponse.success(
                    "record-001",
                    LocalDateTime.of(2026, 2, 1, 9, 0),
                    LocalDateTime.of(2026, 2, 1, 18, 0),
                    8.0,
                    false,
                    0);

            when(checkOutService.execCommand(any(CheckOutRequest.class), any(JWTModel.class)))
                    .thenReturn(response);

            CheckOutRequest request = new CheckOutRequest();
            request.setEmployeeId("test-emp-001");
            request.setCheckOutTime(LocalDateTime.of(2026, 2, 1, 18, 0));

            // Act & Assert
            performPost("/api/v1/attendance/check-out", request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.recordId").value("record-001"))
                    .andExpect(jsonPath("$.workingHours").value(8.0));
        }

        @Test
        @DisplayName("ATT_CHECKIN_004: 下班早退打卡 - 應顯示早退資訊")
        void checkOut_EarlyLeave_ShouldShowEarlyLeaveInfo() throws Exception {
            // Arrange
            CheckOutResponse response = CheckOutResponse.success(
                    "record-003",
                    LocalDateTime.of(2026, 2, 1, 9, 0),
                    LocalDateTime.of(2026, 2, 1, 16, 30),
                    7.5,
                    true,
                    90);

            when(checkOutService.execCommand(any(CheckOutRequest.class), any(JWTModel.class)))
                    .thenReturn(response);

            CheckOutRequest request = new CheckOutRequest();
            request.setEmployeeId("test-emp-001");
            request.setCheckOutTime(LocalDateTime.of(2026, 2, 1, 16, 30));

            // Act & Assert
            // 注意：Lombok @Data 對 boolean isEarlyLeave 產生 isEarlyLeave() getter，
            // Jackson 序列化時會將 isXxx() 映射為 JSON 屬性 "earlyLeave"（去掉 is 前綴）
            performPost("/api/v1/attendance/check-out", request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.earlyLeave").value(true))
                    .andExpect(jsonPath("$.earlyLeaveMinutes").value(90));
        }
    }

    /**
     * 補卡命令 API 測試
     */
    @Nested
    @DisplayName("補卡命令 API")
    class CorrectionCommandApiTests {

        @Test
        @DisplayName("ATT_CORR_001: 補卡申請 - 應返回補卡申請 ID")
        void createCorrection_ShouldReturnCorrectionResult() throws Exception {
            // Arrange
            CreateCorrectionResponse response = CreateCorrectionResponse.builder()
                    .correctionId("correction-001")
                    .status("PENDING")
                    .createdAt(LocalDateTime.of(2026, 2, 1, 10, 0))
                    .build();

            when(createCorrectionService.execCommand(any(CreateCorrectionRequest.class), any(JWTModel.class)))
                    .thenReturn(response);

            CreateCorrectionRequest request = new CreateCorrectionRequest();
            request.setEmployeeId("test-emp-001");
            request.setCorrectionDate(LocalDate.of(2026, 2, 1));
            request.setCorrectionType("FORGET_CHECK_IN");
            request.setCorrectedCheckInTime(java.time.LocalTime.of(9, 0));
            request.setReason("忘記打卡");

            // Act & Assert
            performPost("/api/v1/attendance/corrections", request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.correctionId").value("correction-001"))
                    .andExpect(jsonPath("$.status").value("PENDING"));
        }
    }

    /**
     * 出勤查詢 API 測試
     */
    @Nested
    @DisplayName("出勤查詢 API")
    class AttendanceQueryApiTests {

        @Test
        @DisplayName("ATT_QRY_001: 查詢出勤記錄列表 - 應返回分頁結果")
        void getAttendanceRecords_ShouldReturnRecordList() throws Exception {
            // Arrange
            AttendanceRecordListResponse item = AttendanceRecordListResponse.builder()
                    .recordId("record-001")
                    .employeeId("test-emp-001")
                    .employeeName("測試員工")
                    .attendanceDate(LocalDate.of(2026, 2, 1))
                    .status("NORMAL")
                    .build();

            PageResponse<AttendanceRecordListResponse> response = PageResponse.of(
                    List.of(item), 1, 20, 1L);

            when(getAttendanceRecordsService.getResponse(any(), any(JWTModel.class)))
                    .thenReturn(response);

            // Act & Assert
            performGet("/api/v1/attendance/records")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items").isArray())
                    .andExpect(jsonPath("$.items[0].recordId").value("record-001"));
        }

        @Test
        @DisplayName("ATT_QRY_002: 查詢出勤記錄詳情 - 應返回完整資訊")
        void getAttendanceRecord_ShouldReturnRecordDetail() throws Exception {
            // Arrange
            AttendanceRecordDetailResponse response = AttendanceRecordDetailResponse.builder()
                    .recordId("record-001")
                    .employeeId("test-emp-001")
                    .employeeName("測試員工")
                    .attendanceDate(LocalDate.of(2026, 2, 1))
                    .checkInTime(LocalDateTime.of(2026, 2, 1, 9, 0))
                    .status("NORMAL")
                    .shiftName("標準班別")
                    .build();

            when(getAttendanceRecordService.getResponse(any(), any(JWTModel.class), eq("record-001")))
                    .thenReturn(response);

            // Act & Assert
            performGet("/api/v1/attendance/records/record-001")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.recordId").value("record-001"))
                    .andExpect(jsonPath("$.employeeId").value("test-emp-001"));
        }

        @Test
        @DisplayName("ATT_QRY_003: 查詢補卡申請列表 - 應返回分頁結果")
        void getCorrectionApplications_ShouldReturnCorrectionList() throws Exception {
            // Arrange
            CorrectionListResponse item = CorrectionListResponse.builder()
                    .correctionId("correction-001")
                    .employeeId("test-emp-001")
                    .employeeName("測試員工")
                    .correctionDate(LocalDate.of(2026, 2, 1))
                    .correctionType("FORGET_CHECK_IN")
                    .status("PENDING")
                    .build();

            PageResponse<CorrectionListResponse> response = PageResponse.of(
                    List.of(item), 1, 20, 1L);

            when(getCorrectionApplicationsService.getResponse(any(), any(JWTModel.class)))
                    .thenReturn(response);

            // Act & Assert
            performGet("/api/v1/attendance/corrections")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items").isArray())
                    .andExpect(jsonPath("$.items[0].correctionId").value("correction-001"));
        }
    }
}
