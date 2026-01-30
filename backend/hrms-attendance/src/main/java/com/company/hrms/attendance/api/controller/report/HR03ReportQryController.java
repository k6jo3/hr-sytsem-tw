package com.company.hrms.attendance.api.controller.report;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.attendance.api.request.report.GetDailyReportRequest;
import com.company.hrms.attendance.api.request.report.GetMonthlyReportRequest;
import com.company.hrms.attendance.api.response.report.DailyReportResponse;
import com.company.hrms.attendance.api.response.report.MonthlyReportResponse;
import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * HR03 - 考勤報表 Query Controller
 * 負責考勤報表的查詢操作
 */
@RestController
@RequestMapping("/api/v1/attendance/reports")
@Tag(name = "HR03-Report-Query", description = "考勤報表查詢操作")
public class HR03ReportQryController extends QueryBaseController {

        /**
         * 查詢月報表
         */
        @Operation(summary = "查詢考勤月報表", operationId = "getMonthlyReport")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
                        @ApiResponse(responseCode = "401", description = "未授權")
        })
        @GetMapping("/monthly")
        public ResponseEntity<MonthlyReportResponse> getMonthlyReport(
                        GetMonthlyReportRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(getResponse(request, currentUser));
        }

        /**
         * 查詢日報表
         */
        @Operation(summary = "查詢考勤日報表", operationId = "getDailyReport")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
                        @ApiResponse(responseCode = "401", description = "未授權")
        })
        @GetMapping("/daily")
        public ResponseEntity<DailyReportResponse> getDailyReport(
                        GetDailyReportRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(getResponse(request, currentUser));
        }
}
