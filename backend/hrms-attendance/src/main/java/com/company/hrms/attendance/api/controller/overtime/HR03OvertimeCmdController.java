package com.company.hrms.attendance.api.controller.overtime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.attendance.api.request.overtime.ApplyOvertimeRequest;
import com.company.hrms.attendance.api.request.overtime.ApproveOvertimeRequest;
import com.company.hrms.attendance.api.request.overtime.RejectOvertimeRequest;
import com.company.hrms.attendance.api.response.overtime.ApplyOvertimeResponse;
import com.company.hrms.attendance.api.response.overtime.ApproveOvertimeResponse;
import com.company.hrms.attendance.api.response.overtime.RejectOvertimeResponse;
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
 * HR03 - 加班管理 Command Controller
 * 負責加班申請、核准、駁回等寫入操作
 */
@RestController
@RequestMapping("/api/v1/overtime")
@Tag(name = "HR03-Overtime-Command", description = "加班管理寫入操作")
public class HR03OvertimeCmdController extends CommandBaseController {

    /**
     * 提交加班申請
     */
    @Operation(summary = "提交加班申請", operationId = "applyOvertime")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤或超過加班上限"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    @PostMapping("/applications")
    public ResponseEntity<ApplyOvertimeResponse> applyOvertime(
            @RequestBody @Valid ApplyOvertimeRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    /**
     * 核准加班申請
     */
    @Operation(summary = "核准加班申請", operationId = "approveOvertime")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "加班申請不存在")
    })
    @PutMapping("/applications/{overtimeId}/approve")
    public ResponseEntity<ApproveOvertimeResponse> approveOvertime(
            @PathVariable String overtimeId,
            @RequestBody(required = false) ApproveOvertimeRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        if (request == null) {
            request = new ApproveOvertimeRequest();
        }
        return ResponseEntity.ok(execCommand(request, currentUser, overtimeId));
    }

    /**
     * 駁回加班申請
     */
    @Operation(summary = "駁回加班申請", operationId = "rejectOvertime")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "加班申請不存在")
    })
    @PutMapping("/applications/{overtimeId}/reject")
    public ResponseEntity<RejectOvertimeResponse> rejectOvertime(
            @PathVariable String overtimeId,
            @RequestBody @Valid RejectOvertimeRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, overtimeId));
    }
}
