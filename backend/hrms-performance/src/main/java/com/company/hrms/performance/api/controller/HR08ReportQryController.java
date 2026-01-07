package com.company.hrms.performance.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.performance.api.request.ExportReportRequest;
import com.company.hrms.performance.api.request.GetDistributionRequest;
import com.company.hrms.performance.api.response.GetDistributionResponse;
import com.company.hrms.performance.api.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * HR08 績效考核 - 報表查詢 Controller
 */
@RestController
@RequestMapping("/api/v1/performance/reports")
@Tag(name = "HR08-報表查詢", description = "績效考核 - 報表查詢 API")
public class HR08ReportQryController extends QueryBaseController {

    @Operation(summary = "查詢績效分布", operationId = "getDistribution")
    @GetMapping("/distribution/{cycleId}")
    public ResponseEntity<GetDistributionResponse> getDistribution(@PathVariable String cycleId,
            @CurrentUser JWTModel currentUser) throws Exception {
        GetDistributionRequest request = GetDistributionRequest.builder().cycleId(cycleId).build();
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    @Operation(summary = "匯出績效報表", operationId = "exportReport")
    @GetMapping("/export/{cycleId}")
    public ResponseEntity<SuccessResponse> exportReport(@PathVariable String cycleId,
            @CurrentUser JWTModel currentUser) throws Exception {
        ExportReportRequest request = ExportReportRequest.builder().cycleId(cycleId).build();
        return ResponseEntity.ok(getResponse(request, currentUser));
    }
}
