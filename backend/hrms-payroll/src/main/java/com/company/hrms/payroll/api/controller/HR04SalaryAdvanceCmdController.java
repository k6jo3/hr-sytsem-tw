package com.company.hrms.payroll.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.ApplySalaryAdvanceRequest;
import com.company.hrms.payroll.application.dto.request.ApproveSalaryAdvanceRequest;
import com.company.hrms.payroll.application.dto.request.RejectSalaryAdvanceRequest;
import com.company.hrms.payroll.application.dto.response.SalaryAdvanceResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * HR04 預借薪資命令 Controller
 * 處理預借薪資的申請、核准、駁回、撥款、取消操作
 */
@RestController
@RequestMapping("/api/v1/payroll/salary-advances")
@Slf4j
@Tag(name = "HR04-SalaryAdvance", description = "預借薪資管理")
public class HR04SalaryAdvanceCmdController extends CommandBaseController {

    /**
     * 申請預借薪資
     * Service Bean: applySalaryAdvanceServiceImpl
     */
    @PostMapping
    @Operation(summary = "申請預借薪資", operationId = "applySalaryAdvance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "申請成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤或業務規則違反"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    public ResponseEntity<SalaryAdvanceResponse> applySalaryAdvance(
            @Valid @RequestBody ApplySalaryAdvanceRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    /**
     * 核准預借薪資
     * Service Bean: approveSalaryAdvanceServiceImpl
     */
    @PutMapping("/{id}/approve")
    @Operation(summary = "核准預借薪資", operationId = "approveSalaryAdvance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "核准成功"),
            @ApiResponse(responseCode = "400", description = "核准金額無效"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "預借記錄不存在"),
            @ApiResponse(responseCode = "409", description = "狀態不允許核准")
    })
    public ResponseEntity<SalaryAdvanceResponse> approveSalaryAdvance(
            @PathVariable String id,
            @Valid @RequestBody ApproveSalaryAdvanceRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, id));
    }

    /**
     * 駁回預借薪資
     * Service Bean: rejectSalaryAdvanceServiceImpl
     */
    @PutMapping("/{id}/reject")
    @Operation(summary = "駁回預借薪資", operationId = "rejectSalaryAdvance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "駁回成功"),
            @ApiResponse(responseCode = "400", description = "駁回原因為必填"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "預借記錄不存在"),
            @ApiResponse(responseCode = "409", description = "狀態不允許駁回")
    })
    public ResponseEntity<SalaryAdvanceResponse> rejectSalaryAdvance(
            @PathVariable String id,
            @Valid @RequestBody RejectSalaryAdvanceRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, id));
    }

    /**
     * 撥款預借薪資
     * Service Bean: disburseSalaryAdvanceServiceImpl
     */
    @PutMapping("/{id}/disburse")
    @Operation(summary = "撥款預借薪資", operationId = "disburseSalaryAdvance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "撥款成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "預借記錄不存在"),
            @ApiResponse(responseCode = "409", description = "狀態不允許撥款")
    })
    public ResponseEntity<SalaryAdvanceResponse> disburseSalaryAdvance(
            @PathVariable String id,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(null, currentUser, id));
    }

    /**
     * 取消預借薪資
     * Service Bean: cancelSalaryAdvanceServiceImpl
     */
    @PutMapping("/{id}/cancel")
    @Operation(summary = "取消預借薪資", operationId = "cancelSalaryAdvance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "取消成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "預借記錄不存在"),
            @ApiResponse(responseCode = "409", description = "已撥款或扣回中的預借不可取消")
    })
    public ResponseEntity<SalaryAdvanceResponse> cancelSalaryAdvance(
            @PathVariable String id,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(null, currentUser, id));
    }
}
