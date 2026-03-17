package com.company.hrms.payroll.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.GetSalaryAdvanceListRequest;
import com.company.hrms.payroll.application.dto.response.SalaryAdvanceResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * HR04 預借薪資查詢 Controller
 * 處理預借薪資的查詢操作
 */
@RestController
@RequestMapping("/api/v1/payroll/salary-advances")
@Slf4j
@Tag(name = "HR04-SalaryAdvance", description = "預借薪資管理")
public class HR04SalaryAdvanceQryController extends QueryBaseController {

    /**
     * 查詢預借薪資列表
     * Service Bean: getSalaryAdvancesServiceImpl
     *
     * 支援 employeeId、status 篩選
     */
    @GetMapping
    @Operation(summary = "查詢預借薪資列表", operationId = "getSalaryAdvances")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查詢成功"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    public ResponseEntity<List<SalaryAdvanceResponse>> getSalaryAdvances(
            @ModelAttribute GetSalaryAdvanceListRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    /**
     * 查詢單筆預借薪資
     * Service Bean: getSalaryAdvancesServiceImpl（同一 service，由 args 區分）
     */
    @GetMapping("/{id}")
    @Operation(summary = "查詢單筆預借薪資", operationId = "getSalaryAdvanceById")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查詢成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "預借記錄不存在")
    })
    public ResponseEntity<SalaryAdvanceResponse> getSalaryAdvanceById(
            @PathVariable String id,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(null, currentUser, id));
    }
}
