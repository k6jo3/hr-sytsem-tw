package com.company.hrms.timesheet.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.timesheet.api.request.GetMyTimesheetRequest;
import com.company.hrms.timesheet.api.request.GetPendingApprovalsRequest;
import com.company.hrms.timesheet.api.request.GetTimesheetDetailRequest;
import com.company.hrms.timesheet.api.response.GetMyTimesheetResponse;
import com.company.hrms.timesheet.api.response.GetPendingApprovalsResponse;
import com.company.hrms.timesheet.api.response.GetTimesheetDetailResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/timesheets")
@Tag(name = "HR07-工時查詢", description = "工時管理 - 查詢 API")
public class HR07TimesheetQryController extends QueryBaseController {

    @Operation(summary = "查詢我的工時表", operationId = "getMyTimesheet", description = "ESS - 查詢當前使用者的工時表列表")
    @GetMapping("/my")
    public ResponseEntity<GetMyTimesheetResponse> getMyTimesheet(
            @ModelAttribute GetMyTimesheetRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    @Operation(summary = "查詢待簽核工時表", operationId = "getPendingApprovals", description = "主管 - 查詢待簽核的工時表")
    @GetMapping("/approvals")
    public ResponseEntity<GetPendingApprovalsResponse> getPendingApprovals(
            @ModelAttribute GetPendingApprovalsRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    @Operation(summary = "查詢工時表詳情", operationId = "getTimesheet", description = "查詢指定工時表的詳細資訊")
    @GetMapping("/{id}")
    public ResponseEntity<GetTimesheetDetailResponse> getTimesheet(
            @PathVariable String id,
            @CurrentUser JWTModel currentUser) throws Exception {
        GetTimesheetDetailRequest request = new GetTimesheetDetailRequest();
        request.setTimesheetId(id);
        return ResponseEntity.ok(getResponse(request, currentUser));
    }
}
