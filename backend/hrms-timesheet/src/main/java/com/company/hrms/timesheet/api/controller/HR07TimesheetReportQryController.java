package com.company.hrms.timesheet.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.timesheet.api.request.GetProjectTimesheetSummaryRequest;
import com.company.hrms.timesheet.api.request.GetTimesheetSummaryRequest;
import com.company.hrms.timesheet.api.request.GetUnreportedEmployeesRequest;
import com.company.hrms.timesheet.api.response.GetProjectTimesheetSummaryResponse;
import com.company.hrms.timesheet.api.response.GetTimesheetSummaryResponse;
import com.company.hrms.timesheet.api.response.GetUnreportedEmployeesResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/timesheets")
@Tag(name = "HR07-工時報表", description = "工時管理 - 報表查詢 API")
public class HR07TimesheetReportQryController extends QueryBaseController {

    @Operation(summary = "查詢工時統計", operationId = "getTimesheetSummary", description = "查詢個人或部門的工時統計")
    @GetMapping("/summary")
    public ResponseEntity<GetTimesheetSummaryResponse> getTimesheetSummary(
            @ModelAttribute GetTimesheetSummaryRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    @Operation(summary = "查詢專案工時統計", operationId = "getProjectTimesheetSummary", description = "查詢專案的工時統計")
    @GetMapping("/project-summary")
    public ResponseEntity<GetProjectTimesheetSummaryResponse> getProjectTimesheetSummary(
            @ModelAttribute GetProjectTimesheetSummaryRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    @Operation(summary = "查詢未回報員工", operationId = "getUnreportedEmployees", description = "查詢指定期間內未回報工時的員工")
    @GetMapping("/unreported")
    public ResponseEntity<GetUnreportedEmployeesResponse> getUnreportedEmployees(
            @ModelAttribute GetUnreportedEmployeesRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser));
    }
}
