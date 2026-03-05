package com.company.hrms.project.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.project.api.request.GetProjectCostRequest;
import com.company.hrms.project.api.response.GetProjectCostResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * HR06 專案管理 - 成本分析 Controller
 *
 * 負責專案成本分析查詢操作
 */
@RestController
@RequestMapping("/api/v1/projects/{projectId}/cost")
@Tag(name = "HR06-成本分析", description = "專案管理 - 專案成本分析 API")
public class HR06CostQryController extends QueryBaseController {

    @Operation(
        summary = "查詢專案成本分析",
        operationId = "getProjectCost",
        description = "查詢專案成本分析，包含預算使用率、成員成本明細、月份成本趨勢"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "查詢成功",
            content = @Content(schema = @Schema(implementation = GetProjectCostResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "專案不存在"),
        @ApiResponse(responseCode = "403", description = "無權限查看成本資訊")
    })
    @GetMapping
    public ResponseEntity<GetProjectCostResponse> getProjectCost(
            @Parameter(description = "專案 ID", required = true) @PathVariable String projectId,
            @Parameter(description = "期間起 (YYYY-MM)") @RequestParam(required = false) String periodFrom,
            @Parameter(description = "期間迄 (YYYY-MM)") @RequestParam(required = false) String periodTo,
            @CurrentUser JWTModel currentUser) throws Exception {
        GetProjectCostRequest request = new GetProjectCostRequest();
        request.setProjectId(projectId);
        request.setPeriodFrom(periodFrom);
        request.setPeriodTo(periodTo);
        return ResponseEntity.ok(getResponse(request, currentUser));
    }
}
