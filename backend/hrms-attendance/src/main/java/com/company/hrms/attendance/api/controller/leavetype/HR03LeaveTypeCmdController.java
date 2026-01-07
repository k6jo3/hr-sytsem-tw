package com.company.hrms.attendance.api.controller.leavetype;

import com.company.hrms.attendance.api.request.leavetype.CreateLeaveTypeRequest;
import com.company.hrms.attendance.api.request.leavetype.UpdateLeaveTypeRequest;
import com.company.hrms.attendance.api.response.leavetype.CreateLeaveTypeResponse;
import com.company.hrms.attendance.api.response.leavetype.DeactivateLeaveTypeResponse;
import com.company.hrms.attendance.api.response.leavetype.UpdateLeaveTypeResponse;
import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HR03 - 假別管理 Command Controller
 * 負責假別的新增、更新、停用等寫入操作
 */
@RestController
@RequestMapping("/api/v1/leave/types")
@Tag(name = "HR03-LeaveType-Command", description = "假別管理寫入操作")
public class HR03LeaveTypeCmdController extends CommandBaseController {

    /**
     * 建立假別
     */
    @Operation(summary = "建立假別", operationId = "createLeaveType")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "409", description = "假別代碼已存在")
    })
    @PostMapping
    public ResponseEntity<CreateLeaveTypeResponse> createLeaveType(
            @RequestBody @Valid CreateLeaveTypeRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    /**
     * 更新假別
     */
    @Operation(summary = "更新假別", operationId = "updateLeaveType")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "假別不存在")
    })
    @PutMapping("/{leaveTypeId}")
    public ResponseEntity<UpdateLeaveTypeResponse> updateLeaveType(
            @PathVariable String leaveTypeId,
            @RequestBody @Valid UpdateLeaveTypeRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, leaveTypeId));
    }

    /**
     * 停用假別
     */
    @Operation(summary = "停用假別", operationId = "deactivateLeaveType")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "假別不存在")
    })
    @PutMapping("/{leaveTypeId}/deactivate")
    public ResponseEntity<DeactivateLeaveTypeResponse> deactivateLeaveType(
            @PathVariable String leaveTypeId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(null, currentUser, leaveTypeId));
    }
}
