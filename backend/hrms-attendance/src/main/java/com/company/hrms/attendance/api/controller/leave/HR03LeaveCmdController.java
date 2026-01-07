package com.company.hrms.attendance.api.controller.leave;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.attendance.api.request.leave.ApplyLeaveRequest;
import com.company.hrms.attendance.api.request.leave.ApproveLeaveRequest;
import com.company.hrms.attendance.api.request.leave.RejectLeaveRequest;
import com.company.hrms.attendance.api.response.leave.ApplyLeaveResponse;
import com.company.hrms.attendance.api.response.leave.ApproveLeaveResponse;
import com.company.hrms.attendance.api.response.leave.RejectLeaveResponse;
import com.company.hrms.attendance.api.response.leave.CancelLeaveResponse;
import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * HR03 - 請假管理 Command Controller
 * 負責請假申請、核准、駁回、取消等寫入操作
 */
@RestController
@RequestMapping("/api/v1/leave")
@Tag(name = "HR03-Leave-Command", description = "請假管理寫入操作")
public class HR03LeaveCmdController extends CommandBaseController {

    /**
     * 提交請假申請
     */
    @Operation(summary = "提交請假申請", operationId = "createLeaveApplication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤或餘額不足"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    @PostMapping("/applications")
    public ResponseEntity<ApplyLeaveResponse> createLeaveApplication(
            @RequestBody @Valid ApplyLeaveRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    /**
     * 核准請假申請
     */
    @Operation(summary = "核准請假申請", operationId = "approveLeave")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "請假申請不存在")
    })
    @PutMapping("/applications/{applicationId}/approve")
    public ResponseEntity<ApproveLeaveResponse> approveLeave(
            @PathVariable String applicationId,
            @RequestBody(required = false) ApproveLeaveRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        if (request == null) {
            request = new ApproveLeaveRequest();
        }
        return ResponseEntity.ok(execCommand(request, currentUser, applicationId));
    }

    /**
     * 駁回請假申請
     */
    @Operation(summary = "駁回請假申請", operationId = "rejectLeave")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "請假申請不存在")
    })
    @PutMapping("/applications/{applicationId}/reject")
    public ResponseEntity<RejectLeaveResponse> rejectLeave(
            @PathVariable String applicationId,
            @RequestBody @Valid RejectLeaveRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, applicationId));
    }

    /**
     * 取消請假申請
     */
    @Operation(summary = "取消請假申請", operationId = "cancelLeave")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請假已開始無法取消"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "請假申請不存在")
    })
    @PutMapping("/applications/{applicationId}/cancel")
    public ResponseEntity<CancelLeaveResponse> cancelLeave(
            @PathVariable String applicationId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(null, currentUser, applicationId));
    }
}
