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
import com.company.hrms.payroll.application.dto.request.GetPayrollRunListRequest;
import com.company.hrms.payroll.application.dto.response.PayrollRunResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * HR04 薪資批次查詢 Controller
 * 處理薪資批次的查詢操作
 */
@RestController
@RequestMapping("/api/v1/payroll-runs")
@Slf4j
@Tag(name = "HR04-PayrollRun", description = "薪資批次管理")
public class HR04PayrollRunQryController extends QueryBaseController {

    /**
     * 查詢薪資批次列表
     * Service Bean: getPayrollRunListServiceImpl
     */
    @GetMapping
    @Operation(summary = "查詢薪資批次列表", operationId = "getPayrollRunList")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查詢成功"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    public ResponseEntity<PageResponse<PayrollRunResponse>> getPayrollRunList(
            @ModelAttribute GetPayrollRunListRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    /**
     * 查詢單一薪資批次
     * Service Bean: getPayrollRunByIdServiceImpl
     */
    @GetMapping("/{runId}")
    @Operation(summary = "查詢薪資批次詳情", operationId = "getPayrollRunById")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查詢成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "批次不存在")
    })
    public ResponseEntity<PayrollRunResponse> getPayrollRunById(
            @PathVariable String runId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(runId, currentUser, runId));
    }
}
