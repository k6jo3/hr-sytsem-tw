package com.company.hrms.reporting.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.reporting.api.request.CreateDashboardRequest;
import com.company.hrms.reporting.api.request.UpdateDashboardWidgetsRequest;
import com.company.hrms.reporting.api.response.CreateDashboardResponse;
import com.company.hrms.reporting.api.response.DeleteDashboardResponse;
import com.company.hrms.reporting.api.response.UpdateDashboardWidgetsResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * HR14 儀表板命令 Controller
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@RestController
@RequestMapping("/api/v1/reporting/dashboards")
@Tag(name = "HR14-儀表板管理", description = "儀表板建立、更新、刪除")
public class HR14DashboardCmdController extends CommandBaseController {

    @PostMapping
    @Operation(summary = "建立儀表板", operationId = "createDashboard")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "建立成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "403", description = "權限不足")
    })
    public ResponseEntity<CreateDashboardResponse> createDashboard(
            @Valid @RequestBody CreateDashboardRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @PutMapping("/{dashboardId}/widgets")
    @Operation(summary = "更新 Widget 配置", operationId = "updateDashboardWidgets")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "404", description = "儀表板不存在")
    })
    public ResponseEntity<UpdateDashboardWidgetsResponse> updateDashboardWidgets(
            @PathVariable String dashboardId,
            @Valid @RequestBody UpdateDashboardWidgetsRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        request.setDashboardId(dashboardId);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @DeleteMapping("/{dashboardId}")
    @Operation(summary = "刪除儀表板", operationId = "deleteDashboard")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "刪除成功"),
            @ApiResponse(responseCode = "404", description = "儀表板不存在")
    })
    public ResponseEntity<DeleteDashboardResponse> deleteDashboard(
            @PathVariable String dashboardId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(null, currentUser, dashboardId));
    }
}
