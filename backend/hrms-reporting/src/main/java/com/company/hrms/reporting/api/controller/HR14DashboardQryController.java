package com.company.hrms.reporting.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.reporting.api.request.GetDashboardListRequest;
import com.company.hrms.reporting.api.response.DashboardListResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * HR14 儀表板查詢 Controller
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@RestController
@RequestMapping("/api/v1/reporting/dashboards")
@Tag(name = "HR14-儀表板查詢", description = "儀表板查詢操作")
public class HR14DashboardQryController extends QueryBaseController {

    @GetMapping
    @Operation(summary = "查詢儀表板列表", operationId = "getDashboardList")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查詢成功"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    public ResponseEntity<DashboardListResponse> getDashboardList(
            @ModelAttribute GetDashboardListRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser));
    }
}
