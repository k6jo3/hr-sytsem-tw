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
import com.company.hrms.payroll.application.dto.request.CreateLegalDeductionRequest;
import com.company.hrms.payroll.application.dto.request.LegalDeductionActionRequest;
import com.company.hrms.payroll.application.dto.response.LegalDeductionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * HR04 法扣款命令 Controller
 * 處理法扣款的建立、暫停、恢復、終止操作
 */
@RestController
@RequestMapping("/api/v1/payroll/legal-deductions")
@Slf4j
@Tag(name = "HR04-LegalDeduction", description = "法扣款管理")
public class HR04LegalDeductionCmdController extends CommandBaseController {

    /**
     * 建立法扣款
     * Service Bean: createLegalDeductionServiceImpl
     */
    @PostMapping
    @Operation(summary = "建立法扣款", operationId = "createLegalDeduction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "建立成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    public ResponseEntity<LegalDeductionResponse> createLegalDeduction(
            @Valid @RequestBody CreateLegalDeductionRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    /**
     * 暫停法扣款
     * Service Bean: suspendLegalDeductionServiceImpl
     */
    @PutMapping("/{id}/suspend")
    @Operation(summary = "暫停法扣款", operationId = "suspendLegalDeduction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "暫停成功"),
            @ApiResponse(responseCode = "400", description = "狀態不允許暫停"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "法扣款不存在")
    })
    public ResponseEntity<LegalDeductionResponse> suspendLegalDeduction(
            @PathVariable String id,
            @RequestBody(required = false) LegalDeductionActionRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        LegalDeductionActionRequest actionRequest = request != null ? request : new LegalDeductionActionRequest();
        actionRequest.setDeductionId(id);
        return ResponseEntity.ok(execCommand(actionRequest, currentUser, id));
    }

    /**
     * 恢復法扣款
     * Service Bean: resumeLegalDeductionServiceImpl
     */
    @PutMapping("/{id}/resume")
    @Operation(summary = "恢復法扣款", operationId = "resumeLegalDeduction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "恢復成功"),
            @ApiResponse(responseCode = "400", description = "狀態不允許恢復"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "法扣款不存在")
    })
    public ResponseEntity<LegalDeductionResponse> resumeLegalDeduction(
            @PathVariable String id,
            @RequestBody(required = false) LegalDeductionActionRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        LegalDeductionActionRequest actionRequest = request != null ? request : new LegalDeductionActionRequest();
        actionRequest.setDeductionId(id);
        return ResponseEntity.ok(execCommand(actionRequest, currentUser, id));
    }

    /**
     * 終止法扣款
     * Service Bean: terminateLegalDeductionServiceImpl
     */
    @PutMapping("/{id}/terminate")
    @Operation(summary = "終止法扣款", operationId = "terminateLegalDeduction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "終止成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "法扣款不存在")
    })
    public ResponseEntity<LegalDeductionResponse> terminateLegalDeduction(
            @PathVariable String id,
            @RequestBody(required = false) LegalDeductionActionRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        LegalDeductionActionRequest actionRequest = request != null ? request : new LegalDeductionActionRequest();
        actionRequest.setDeductionId(id);
        return ResponseEntity.ok(execCommand(actionRequest, currentUser, id));
    }
}
