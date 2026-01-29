package com.company.hrms.reporting.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.reporting.api.request.GetAttendanceStatisticsRequest;
import com.company.hrms.reporting.api.request.GetEmployeeRosterRequest;
import com.company.hrms.reporting.api.request.GetHeadcountReportRequest;
import com.company.hrms.reporting.api.request.GetPayrollSummaryRequest;
import com.company.hrms.reporting.api.request.GetProjectCostAnalysisRequest;
import com.company.hrms.reporting.api.response.AttendanceStatisticsResponse;
import com.company.hrms.reporting.api.response.EmployeeRosterResponse;
import com.company.hrms.reporting.api.response.HeadcountReportResponse;
import com.company.hrms.reporting.api.response.PayrollSummaryResponse;
import com.company.hrms.reporting.api.response.ProjectCostAnalysisResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * HR14 人力資源報表 Controller
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@RestController
@RequestMapping("/api/v1/reporting")
@Tag(name = "HR14-報表查詢", description = "人力資源、專案、財務報表查詢")
public class HR14ReportQryController extends QueryBaseController {

        @GetMapping("/hr/employee-roster")
        @Operation(summary = "查詢員工花名冊", operationId = "getEmployeeRoster")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "查詢成功"),
                        @ApiResponse(responseCode = "401", description = "未授權")
        })
        public ResponseEntity<EmployeeRosterResponse> getEmployeeRoster(
                        @ModelAttribute GetEmployeeRosterRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(getResponse(request, currentUser));
        }

        @GetMapping("/hr/attendance-statistics")
        @Operation(summary = "查詢差勤統計報表", operationId = "getAttendanceStatistics")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "查詢成功"),
                        @ApiResponse(responseCode = "401", description = "未授權")
        })
        public ResponseEntity<AttendanceStatisticsResponse> getAttendanceStatistics(
                        @ModelAttribute GetAttendanceStatisticsRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(getResponse(request, currentUser));
        }

        @GetMapping("/finance/payroll-summary")
        @Operation(summary = "查詢薪資匯總報表", operationId = "getPayrollSummary")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "查詢成功"),
                        @ApiResponse(responseCode = "401", description = "未授權")
        })
        public ResponseEntity<PayrollSummaryResponse> getPayrollSummary(
                        @ModelAttribute GetPayrollSummaryRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(getResponse(request, currentUser));
        }

        @GetMapping("/project/cost-analysis")
        @Operation(summary = "查詢專案成本分析", operationId = "getProjectCostAnalysis")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "查詢成功"),
                        @ApiResponse(responseCode = "401", description = "未授權")
        })
        public ResponseEntity<ProjectCostAnalysisResponse> getProjectCostAnalysis(
                        @ModelAttribute GetProjectCostAnalysisRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(getResponse(request, currentUser));
        }

        @GetMapping("/hr/headcount")
        @Operation(summary = "查詢人力盤點報表", operationId = "getHeadcountReport")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "查詢成功"),
                        @ApiResponse(responseCode = "401", description = "未授權")
        })
        public ResponseEntity<HeadcountReportResponse> getHeadcountReport(
                        @ModelAttribute GetHeadcountReportRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(getResponse(request, currentUser));
        }
}
