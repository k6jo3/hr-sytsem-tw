package com.company.hrms.payroll.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.api.response.PageResponse;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.GetLegalDeductionListRequest;
import com.company.hrms.payroll.application.dto.response.LegalDeductionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * HR04 法扣款查詢 Controller
 * 處理法扣款的查詢操作
 */
@RestController
@RequestMapping("/api/v1/payroll/legal-deductions")
@Slf4j
@Tag(name = "HR04-LegalDeduction", description = "法扣款管理")
public class HR04LegalDeductionQryController extends QueryBaseController {

    /**
     * 查詢法扣款列表
     * Service Bean: getLegalDeductionListServiceImpl
     */
    @GetMapping
    @Operation(summary = "查詢法扣款列表", operationId = "getLegalDeductionList")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查詢成功"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    public ResponseEntity<PageResponse<LegalDeductionResponse>> getLegalDeductionList(
            @ModelAttribute GetLegalDeductionListRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    /**
     * 查詢單筆法扣款
     * Service Bean: getLegalDeductionByIdServiceImpl
     */
    @GetMapping("/{id}")
    @Operation(summary = "查詢單筆法扣款", operationId = "getLegalDeductionById")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查詢成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "法扣款不存在")
    })
    public ResponseEntity<LegalDeductionResponse> getLegalDeductionById(
            @PathVariable String id,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(id, currentUser, id));
    }
}
