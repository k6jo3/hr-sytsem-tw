package com.company.hrms.timesheet.api.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.timesheet.api.request.ApproveTimesheetRequest;
import com.company.hrms.timesheet.api.request.CreateEntryRequest;
import com.company.hrms.timesheet.api.request.LockTimesheetRequest;
import com.company.hrms.timesheet.api.request.RejectTimesheetRequest;
import com.company.hrms.timesheet.api.request.SubmitTimesheetRequest;
import com.company.hrms.timesheet.api.response.ApproveTimesheetResponse;
import com.company.hrms.timesheet.api.response.CreateEntryResponse;
import com.company.hrms.timesheet.api.response.LockTimesheetResponse;
import com.company.hrms.timesheet.api.response.RejectTimesheetResponse;
import com.company.hrms.timesheet.api.response.SubmitTimesheetResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/timesheets")
@Tag(name = "HR07-工時管理", description = "工時管理 - 命令 API")
public class HR07TimesheetCmdController extends CommandBaseController {

    @Operation(summary = "新增工時條目", operationId = "createEntry", description = "員工 - 新增一筆工時記錄")
    @PostMapping("/entry")
    public ResponseEntity<CreateEntryResponse> createEntry(
            @RequestBody CreateEntryRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "提交工時表", operationId = "submitTimesheet", description = "員工 - 提交整週工時表進行簽核")
    @PostMapping("/submit")
    public ResponseEntity<SubmitTimesheetResponse> submitTimesheet(
            @RequestBody SubmitTimesheetRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "簽核工時表", operationId = "approveTimesheet", description = "主管 - 簽核工時表")
    @PostMapping("/{id}/approve")
    public ResponseEntity<ApproveTimesheetResponse> approveTimesheet(
            @PathVariable String id,
            @CurrentUser JWTModel currentUser) throws Exception {
        ApproveTimesheetRequest request = new ApproveTimesheetRequest();
        request.setTimesheetId(UUID.fromString(id));
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "退回工時表", operationId = "rejectTimesheet", description = "主管 - 退回工時表")
    @PostMapping("/{id}/reject")
    public ResponseEntity<RejectTimesheetResponse> rejectTimesheet(
            @PathVariable String id,
            @RequestBody RejectTimesheetRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        request.setTimesheetId(UUID.fromString(id));
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "鎖定工時表", operationId = "lockTimesheet", description = "系統/管理員 - 鎖定工時表")
    @PostMapping("/{id}/lock")
    public ResponseEntity<LockTimesheetResponse> lockTimesheet(
            @PathVariable String id,
            @CurrentUser JWTModel currentUser) throws Exception {
        LockTimesheetRequest request = new LockTimesheetRequest();
        request.setTimesheetId(id);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }
}
