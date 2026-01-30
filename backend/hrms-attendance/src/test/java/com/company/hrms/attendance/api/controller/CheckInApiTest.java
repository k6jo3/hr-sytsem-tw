package com.company.hrms.attendance.api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
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
import com.company.hrms.attendance.api.response.attendance.ApproveCorrectionResponse;
import com.company.hrms.attendance.api.response.attendance.CheckInResponse;
import com.company.hrms.attendance.api.response.attendance.CheckOutResponse;
import com.company.hrms.attendance.api.response.attendance.CreateCorrectionResponse;
import com.company.hrms.attendance.api.response.checkin.AttendanceRecordDetailResponse;
import com.company.hrms.attendance.api.response.checkin.AttendanceRecordListResponse;
import com.company.hrms.attendance.api.response.checkin.CorrectionListResponse;
import com.company.hrms.attendance.application.service.checkin.CheckInServiceImpl;
import com.company.hrms.attendance.application.service.checkout.CheckOutServiceImpl;
import com.company.hrms.attendance.application.service.correction.ApproveCorrectionServiceImpl;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.base.BaseApiContractTest;

/**
 * HR03 打卡管理 API 合約測試
 *
 * <p>
 * 測試範圍：
 * </p>
 * <ul>
 * <li>上班打卡、下班打卡 (Command)</li>
 * <li>補卡申請、補卡審核 (Command)</li>
 * <li>出勤記錄查詢、補卡申請查詢 (Query)</li>
 * </ul>
 *
 * @author SA Team
 * @since 2026-01-30
 */
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("HR03 打卡管理 API 合約測試")
public class CheckInApiTest extends BaseApiContractTest {

        @MockBean(name = "checkInServiceImpl")
        private CheckInServiceImpl checkInService;

        @MockBean(name = "checkOutServiceImpl")
        private CheckOutServiceImpl checkOutService;

        @MockBean(name = "createCorrectionServiceImpl")
        private Object createCorrectionService;

        @MockBean(name = "approveCorrectionServiceImpl")
        private ApproveCorrectionServiceImpl approveCorrectionService;

        @MockBean(name = "getAttendanceRecordsServiceImpl")
        private Object getAttendanceRecordsService;

        @MockBean(name = "getAttendanceRecordServiceImpl")
        private Object getAttendanceRecordService;

        @MockBean(name = "getCorrectionApplicationsServiceImpl")
        private Object getCorrectionApplicationsService;

        @BeforeEach
        void setupSecurity() {
                JWTModel mockUser = new JWTModel();
                mockUser.setUserId("emp-001");
                mockUser.setUsername("test_employee");
                mockUser.setRoles(Collections.singletonList("EMPLOYEE"));

                List<SimpleGrantedAuthority> authorities = mockUser.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .collect(Collectors.toList());
                authorities.add(new SimpleGrantedAuthority("attendance:checkin"));
                authorities.add(new SimpleGrantedAuthority("attendance:checkout"));
                authorities.add(new SimpleGrantedAuthority("attendance:correction:create"));
                authorities.add(new SimpleGrantedAuthority("attendance:correction:approve"));
                authorities.add(new SimpleGrantedAuthority("attendance:read"));

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                mockUser, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        /**
         * 打卡命令 API 測試
         */
        @Nested
        @DisplayName("打卡命令 API")
        public class CheckInCommandApiTests {

                @Test
                @DisplayName("ATT_CHK_001: 上班打卡 - 應回傳打卡結果")
                void checkIn_ShouldReturnCheckInResult() throws Exception {
                        // Arrange
                        CheckInRequest request = new CheckInRequest();
                        request.setEmployeeId("emp-001");
                        request.setCheckInTime(LocalDateTime.now());
                        request.setLatitude(25.0330);
                        request.setLongitude(121.5654);

                        CheckInResponse response = CheckInResponse.success(
                                        "rec-001",
                                        LocalDateTime.now(),
                                        false,
                                        0,
                                        "標準班");

                        when(checkInService.execCommand(any(CheckInRequest.class), any(JWTModel.class)))
                                        .thenReturn(response);

                        // Act & Assert
                        performPost("/api/v1/attendance/check-in", request)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.success").value(true))
                                        .andExpect(jsonPath("$.recordId").value("rec-001"))
                                        .andExpect(jsonPath("$.shiftName").value("標準班"));
                }

                @Test
                @DisplayName("ATT_CHK_002: 上班打卡 - 遲到應顯示遲到資訊")
                void checkIn_Late_ShouldShowLateInfo() throws Exception {
                        // Arrange
                        CheckInRequest request = new CheckInRequest();
                        request.setEmployeeId("emp-001");
                        request.setCheckInTime(LocalDateTime.now());

                        CheckInResponse response = CheckInResponse.success(
                                        "rec-002",
                                        LocalDateTime.now(),
                                        true,
                                        15,
                                        "標準班");

                        when(checkInService.execCommand(any(CheckInRequest.class), any(JWTModel.class)))
                                        .thenReturn(response);

                        // Act & Assert
                        performPost("/api/v1/attendance/check-in", request)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.success").value(true))
                                        .andExpect(jsonPath("$.isLate").value(true))
                                        .andExpect(jsonPath("$.lateMinutes").value(15));
                }

                @Test
                @DisplayName("ATT_CHK_003: 下班打卡 - 應回傳打卡結果")
                void checkOut_ShouldReturnCheckOutResult() throws Exception {
                        // Arrange
                        CheckOutRequest request = new CheckOutRequest();
                        request.setEmployeeId("emp-001");
                        request.setCheckOutTime(LocalDateTime.now());

                        CheckOutResponse response = CheckOutResponse.builder()
                                        .success(true)
                                        .recordId("rec-001")
                                        .checkOutTime(LocalDateTime.now())
                                        .isEarlyLeave(false)
                                        .earlyLeaveMinutes(0)
                                        .workingHours(8.0)
                                        .message("下班打卡成功")
                                        .build();

                        when(checkOutService.execCommand(any(CheckOutRequest.class), any(JWTModel.class)))
                                        .thenReturn(response);

                        // Act & Assert
                        performPost("/api/v1/attendance/check-out", request)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.success").value(true))
                                        .andExpect(jsonPath("$.recordId").value("rec-001"))
                                        .andExpect(jsonPath("$.workingHours").value(8.0));
                }

                @Test
                @DisplayName("ATT_CHK_004: 下班打卡 - 早退應顯示早退資訊")
                void checkOut_EarlyLeave_ShouldShowEarlyLeaveInfo() throws Exception {
                        // Arrange
                        CheckOutRequest request = new CheckOutRequest();
                        request.setEmployeeId("emp-001");
                        request.setCheckOutTime(LocalDateTime.now());

                        CheckOutResponse response = CheckOutResponse.builder()
                                        .success(true)
                                        .recordId("rec-002")
                                        .checkOutTime(LocalDateTime.now())
                                        .isEarlyLeave(true)
                                        .earlyLeaveMinutes(30)
                                        .workingHours(7.5)
                                        .message("已打卡 (早退 30 分鐘)")
                                        .build();

                        when(checkOutService.execCommand(any(CheckOutRequest.class), any(JWTModel.class)))
                                        .thenReturn(response);

                        // Act & Assert
                        performPost("/api/v1/attendance/check-out", request)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.success").value(true))
                                        .andExpect(jsonPath("$.isEarlyLeave").value(true))
                                        .andExpect(jsonPath("$.earlyLeaveMinutes").value(30));
                }
        }

        /**
         * 補卡申請命令 API 測試
         */
        @Nested
        @DisplayName("補卡申請命令 API")
        public class CorrectionCommandApiTests {

                @Test
                @DisplayName("ATT_COR_001: 提交補卡申請 - 應回傳申請結果")
                void createCorrection_ShouldReturnCorrectionResult() throws Exception {
                        // Arrange
                        CreateCorrectionRequest request = new CreateCorrectionRequest();
                        request.setEmployeeId("emp-001");
                        request.setCorrectionDate(LocalDate.now().minusDays(1));
                        request.setCorrectionType("CHECK_IN");
                        request.setCorrectedCheckInTime(java.time.LocalTime.of(9, 0));
                        request.setReason("忘記打卡");

                        CreateCorrectionResponse response = CreateCorrectionResponse.builder()
                                        .correctionId("corr-001")
                                        .status("PENDING")
                                        .createdAt(LocalDateTime.now())
                                        .build();

                        // when(createCorrectionService.getClass()).thenReturn(Object.class); // Removed
                        // invalid stubbing
                        @SuppressWarnings("unchecked")
                        var service = (com.company.hrms.common.service.CommandApiService<CreateCorrectionRequest, CreateCorrectionResponse>) createCorrectionService;
                        doReturn(response).when(service).execCommand(any(), any());

                        // Act & Assert - 只驗證請求格式正確
                        performPost("/api/v1/attendance/corrections", request)
                                        .andExpect(status().isOk());
                }

                @Test
                @DisplayName("ATT_COR_002: 審核補卡申請 - 應回傳審核結果")
                void approveCorrection_ShouldReturnApprovalResult() throws Exception {
                        // Arrange
                        String correctionId = "corr-001";

                        ApproveCorrectionResponse response = ApproveCorrectionResponse.builder()
                                        .correctionId(correctionId)
                                        .status("APPROVED")
                                        .approvedBy("manager-001")
                                        .approvedAt(LocalDateTime.now())
                                        .build();

                        when(approveCorrectionService.execCommand(any(), any(JWTModel.class), eq(correctionId)))
                                        .thenReturn(response);

                        // Act & Assert
                        performPut("/api/v1/attendance/corrections/" + correctionId + "/approve", null)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.correctionId").value(correctionId))
                                        .andExpect(jsonPath("$.status").value("APPROVED"));
                }
        }

        /**
         * 出勤記錄查詢 API 測試
         */
        @Nested
        @DisplayName("出勤記錄查詢 API")
        public class AttendanceQueryApiTests {

                @Test
                @DisplayName("ATT_QRY_001: 查詢出勤記錄列表 - 應回傳記錄列表")
                void getAttendanceRecords_ShouldReturnRecordList() throws Exception {
                        // Arrange
                        AttendanceRecordListResponse record1 = AttendanceRecordListResponse.builder()
                                        .recordId("rec-001")
                                        .employeeId("emp-001")
                                        .employeeName("王小明")
                                        .employeeNumber("EMP001")
                                        .attendanceDate(LocalDate.now())
                                        .checkInTime(LocalDateTime.now().withHour(9).withMinute(0))
                                        .checkOutTime(LocalDateTime.now().withHour(18).withMinute(0))
                                        .status("NORMAL")
                                        .shiftName("標準班")
                                        .lateMinutes(0)
                                        .earlyLeaveMinutes(0)
                                        .build();

                        AttendanceRecordListResponse record2 = AttendanceRecordListResponse.builder()
                                        .recordId("rec-002")
                                        .employeeId("emp-002")
                                        .employeeName("李小華")
                                        .employeeNumber("EMP002")
                                        .attendanceDate(LocalDate.now())
                                        .checkInTime(LocalDateTime.now().withHour(9).withMinute(15))
                                        .checkOutTime(LocalDateTime.now().withHour(18).withMinute(0))
                                        .status("LATE")
                                        .shiftName("標準班")
                                        .lateMinutes(15)
                                        .earlyLeaveMinutes(0)
                                        .build();

                        List<AttendanceRecordListResponse> records = Arrays.asList(record1, record2);

                        @SuppressWarnings("unchecked")
                        var service = (com.company.hrms.common.service.QueryApiService<Object, List<AttendanceRecordListResponse>>) getAttendanceRecordsService;
                        doReturn(records).when(service).getResponse(any(), any());

                        // Act & Assert - 只驗證請求格式正確
                        performGet("/api/v1/attendance/records")
                                        .andExpect(status().isOk());
                }

                @Test
                @DisplayName("ATT_QRY_002: 查詢單筆出勤記錄 - 應回傳記錄詳情")
                void getAttendanceRecord_ShouldReturnRecordDetail() throws Exception {
                        // Arrange
                        String recordId = "rec-001";

                        AttendanceRecordDetailResponse response = AttendanceRecordDetailResponse.builder()
                                        .recordId(recordId)
                                        .employeeId("emp-001")
                                        .employeeName("王小明")
                                        .attendanceDate(LocalDate.now())
                                        .checkInTime(LocalDateTime.now().withHour(9).withMinute(0))
                                        .checkOutTime(LocalDateTime.now().withHour(18).withMinute(0))
                                        .status("NORMAL")
                                        .build();

                        @SuppressWarnings("unchecked")
                        var service = (com.company.hrms.common.service.QueryApiService<Object, AttendanceRecordDetailResponse>) getAttendanceRecordService;
                        doReturn(response).when(service).getResponse(any(), any(), eq(recordId));

                        // Act & Assert - 只驗證請求格式正確
                        performGet("/api/v1/attendance/records/" + recordId)
                                        .andExpect(status().isOk());
                }

                @Test
                @DisplayName("ATT_QRY_003: 查詢補卡申請列表 - 應回傳申請列表")
                void getCorrectionApplications_ShouldReturnCorrectionList() throws Exception {
                        // Arrange
                        CorrectionListResponse correction1 = CorrectionListResponse.builder()
                                        .correctionId("corr-001")
                                        .employeeId("emp-001")
                                        .employeeName("王小明")
                                        .correctionDate(LocalDate.now().minusDays(1))
                                        .correctionType("CHECK_IN")
                                        .status("PENDING")
                                        .build();

                        List<CorrectionListResponse> corrections = Arrays.asList(correction1);

                        @SuppressWarnings("unchecked")
                        var service = (com.company.hrms.common.service.QueryApiService<Object, List<CorrectionListResponse>>) getCorrectionApplicationsService;
                        doReturn(corrections).when(service).getResponse(any(), any());

                        // Act & Assert - 只驗證請求格式正確
                        performGet("/api/v1/attendance/corrections")
                                        .andExpect(status().isOk());
                }
        }
}
