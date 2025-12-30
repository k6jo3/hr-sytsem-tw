package com.company.hrms.attendance.api.controller.leave;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.attendance.api.request.leave.ApplyLeaveRequest;
import com.company.hrms.attendance.api.request.leave.ApproveLeaveRequest;
import com.company.hrms.attendance.api.response.leave.ApplyLeaveResponse;
import com.company.hrms.attendance.api.response.leave.ApproveLeaveResponse;
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
 * 負責請假申請、核准等寫入操作
 */
@RestController
@RequestMapping("/api/v1/leaves")
@Tag(name = "HR03-Leave-Command", description = "請假管理寫入操作")
public class HR03LeaveCmdController extends CommandBaseController {

    /**
     * 請假申請
     */
    @Operation(summary = "請假申請", operationId = "applyLeave")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    @PostMapping
    public ResponseEntity<ApplyLeaveResponse> applyLeave(
            @RequestBody @Valid ApplyLeaveRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    /**
     * 核准請假
     */
    @Operation(summary = "核准請假", operationId = "approveLeave")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "請假申請不存在")
    })
    @PostMapping("/{applicationId}/approve")
    public ResponseEntity<ApproveLeaveResponse> approveLeave(
            @PathVariable String applicationId,
            @RequestBody(required = false) ApproveLeaveRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        if (request == null) {
            request = new ApproveLeaveRequest();
        }
        request.setApplicationId(applicationId);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }
}
