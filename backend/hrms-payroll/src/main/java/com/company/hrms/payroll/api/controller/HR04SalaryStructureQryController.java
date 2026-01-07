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
import com.company.hrms.payroll.application.dto.request.GetSalaryStructureListRequest;
import com.company.hrms.payroll.application.dto.response.SalaryStructureResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * HR04 薪資結構查詢 Controller
 * 處理薪資結構的查詢操作
 */
@RestController
@RequestMapping("/api/v1/salary-structures")
@Slf4j
@Tag(name = "HR04-SalaryStructure", description = "薪資結構管理")
public class HR04SalaryStructureQryController extends QueryBaseController {

    /**
     * 查詢薪資結構列表
     * Service Bean: getSalaryStructureListServiceImpl
     */
    @GetMapping
    @Operation(summary = "查詢薪資結構列表", operationId = "getSalaryStructureList")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查詢成功"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    public ResponseEntity<PageResponse<SalaryStructureResponse>> getSalaryStructureList(
            @ModelAttribute GetSalaryStructureListRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    /**
     * 查詢員工目前有效的薪資結構
     * Service Bean: getEmployeeSalaryStructureServiceImpl
     */
    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "查詢員工薪資結構", operationId = "getEmployeeSalaryStructure")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查詢成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "員工無有效薪資結構")
    })
    public ResponseEntity<SalaryStructureResponse> getEmployeeSalaryStructure(
            @PathVariable String employeeId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(employeeId, currentUser, employeeId));
    }
}
