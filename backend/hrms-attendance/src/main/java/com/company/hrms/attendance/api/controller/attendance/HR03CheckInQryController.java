package com.company.hrms.attendance.api.controller.attendance;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.attendance.api.request.attendance.GetAttendanceListRequest;
import com.company.hrms.attendance.api.request.attendance.GetCorrectionListRequest;
import com.company.hrms.attendance.api.response.checkin.AttendanceRecordDetailResponse;
import com.company.hrms.attendance.api.response.checkin.AttendanceRecordListResponse;
import com.company.hrms.attendance.api.response.checkin.CorrectionListResponse;
import com.company.hrms.attendance.api.response.checkin.TodayRecordResponse;
import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.api.response.PageResponse;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * HR03 - 打卡管理 Query Controller
 * 負責出勤記錄與補卡申請的查詢操作
 */
@RestController
@RequestMapping("/api/v1/attendance")
@Tag(name = "HR03-CheckIn-Query", description = "打卡管理查詢操作")
public class HR03CheckInQryController extends QueryBaseController {

        /**
         * 取得今日打卡資訊
         */
        @Operation(summary = "取得今日打卡資訊", operationId = "getTodayRecord")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "401", description = "未授權")
        })
        @GetMapping("/today")
        public ResponseEntity<TodayRecordResponse> getTodayRecord(
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(getResponse(new GetTodayRecordRequest(), currentUser));
        }

        /**
         * 查詢出勤記錄列表
         */
        @Operation(summary = "查詢出勤記錄列表", operationId = "getAttendanceRecords")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "401", description = "未授權")
        })
        @GetMapping("/records")
        public ResponseEntity<PageResponse<AttendanceRecordListResponse>> getAttendanceRecords(
                        @RequestParam(required = false) String employeeId,
                        @RequestParam(required = false) String departmentId,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                        @RequestParam(required = false) String status,
                        @RequestParam(defaultValue = "1") Integer page,
                        @RequestParam(defaultValue = "20") Integer size,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                GetAttendanceListRequest request = GetAttendanceListRequest.builder()
                                .employeeId(employeeId)
                                .deptId(departmentId)
                                .startDate(startDate)
                                .endDate(endDate)
                                .status(status)
                                .page(page)
                                .size(size)
                                .build();
                return ResponseEntity.ok(getResponse(request, currentUser));
        }

        /**
         * 查詢單筆出勤記錄
         */
        @Operation(summary = "查詢單筆出勤記錄", operationId = "getAttendanceRecord")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "401", description = "未授權"),
                        @ApiResponse(responseCode = "404", description = "記錄不存在")
        })
        @GetMapping("/records/{recordId}")
        public ResponseEntity<AttendanceRecordDetailResponse> getAttendanceRecord(
                        @PathVariable String recordId,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(getResponse(new GetAttendanceRecordRequest(), currentUser, recordId));
        }

        /**
         * 查詢補卡申請列表
         */
        @Operation(summary = "查詢補卡申請列表", operationId = "getCorrectionApplications")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "401", description = "未授權")
        })
        @GetMapping("/corrections")
        public ResponseEntity<PageResponse<CorrectionListResponse>> getCorrectionApplications(
                        @RequestParam(required = false) String employeeId,
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                        @RequestParam(defaultValue = "1") Integer page,
                        @RequestParam(defaultValue = "20") Integer size,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                GetCorrectionListRequest request = GetCorrectionListRequest.builder()
                                .employeeId(employeeId)
                                .status(status)
                                .startDate(startDate)
                                .endDate(endDate)
                                .page(page)
                                .size(size)
                                .build();
                return ResponseEntity.ok(getResponse(request, currentUser));
        }

        /**
         * 出勤記錄查詢請求 (內部類別)
         */
        public record AttendanceRecordQueryRequest(
                        String employeeId,
                        String departmentId,
                        LocalDate startDate,
                        LocalDate endDate,
                        String status) {
        }

        /**
         * 取得出勤記錄請求 (內部類別)
         */
        public record GetAttendanceRecordRequest() {
        }

        /**
         * 補卡申請查詢請求 (內部類別)
         */
        public record CorrectionQueryRequest(
                        String employeeId,
                        String status,
                        LocalDate startDate,
                        LocalDate endDate) {
        }

        /**
         * 取得今日打卡資訊請求
         */
        public record GetTodayRecordRequest() {
        }
}
