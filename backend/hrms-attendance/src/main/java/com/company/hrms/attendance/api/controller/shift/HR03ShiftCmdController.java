package com.company.hrms.attendance.api.controller.shift;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.attendance.api.request.shift.CreateShiftRequest;
import com.company.hrms.attendance.api.request.shift.UpdateShiftRequest;
import com.company.hrms.attendance.api.response.shift.CreateShiftResponse;
import com.company.hrms.attendance.api.response.shift.UpdateShiftResponse;
import com.company.hrms.attendance.api.response.shift.DeactivateShiftResponse;
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
 * HR03 - 班別管理 Command Controller
 * 負責班別的新增、更新、停用等寫入操作
 */
@RestController
@RequestMapping("/api/v1/shifts")
@Tag(name = "HR03-Shift-Command", description = "班別管理寫入操作")
public class HR03ShiftCmdController extends CommandBaseController {

    /**
     * 建立班別
     */
    @Operation(summary = "建立班別", operationId = "createShift")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "409", description = "班別代碼已存在")
    })
    @PostMapping
    public ResponseEntity<CreateShiftResponse> createShift(
            @RequestBody @Valid CreateShiftRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    /**
     * 更新班別
     */
    @Operation(summary = "更新班別", operationId = "updateShift")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "班別不存在")
    })
    @PutMapping("/{shiftId}")
    public ResponseEntity<UpdateShiftResponse> updateShift(
            @PathVariable String shiftId,
            @RequestBody @Valid UpdateShiftRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, shiftId));
    }

    /**
     * 停用班別
     */
    @Operation(summary = "停用班別", operationId = "deactivateShift")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "班別不存在")
    })
    @PutMapping("/{shiftId}/deactivate")
    public ResponseEntity<DeactivateShiftResponse> deactivateShift(
            @PathVariable String shiftId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(null, currentUser, shiftId));
    }
}
