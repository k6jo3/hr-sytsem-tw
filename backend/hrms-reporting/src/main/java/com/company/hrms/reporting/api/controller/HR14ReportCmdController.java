package com.company.hrms.reporting.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.reporting.api.request.GenerateHrReportRequest;
import com.company.hrms.reporting.api.request.GenerateProjectReportRequest;
import com.company.hrms.reporting.api.response.GenerateReportResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * HR14 報表生成 Command Controller
 *
 * @author SA Team
 * @since 2026-02-09
 */
@RestController
@RequestMapping("/api/v1/reporting/reports")
@Tag(name = "HR14-報表生成", description = "報表生成命令操作")
public class HR14ReportCmdController extends CommandBaseController {

    @PostMapping("/generate/hr")
    @Operation(summary = "生成人力資源報表", operationId = "generateHrReport")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "生成成功"),
            @ApiResponse(responseCode = "400", description = "請求參數錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    public ResponseEntity<GenerateReportResponse> generateHrReport(
            @RequestBody GenerateHrReportRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @PostMapping("/generate/project")
    @Operation(summary = "生成專案成本報表", operationId = "generateProjectReport")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "生成成功"),
            @ApiResponse(responseCode = "400", description = "請求參數錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    public ResponseEntity<GenerateReportResponse> generateProjectReport(
            @RequestBody GenerateProjectReportRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }
}
