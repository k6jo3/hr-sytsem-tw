package com.company.hrms.insurance.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.insurance.api.request.BatchAdjustLevelsRequest;
import com.company.hrms.insurance.api.response.BatchAdjustLevelsResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * HR05 投保級距 Command Controller
 */
@RestController
@RequestMapping("/api/v1/insurance/levels")
@Slf4j
@Tag(name = "HR05-Level", description = "投保級距管理")
public class HR05LevelCmdController extends CommandBaseController {

    @PostMapping("/batch-adjust")
    @Operation(summary = "批量調整投保級距", operationId = "batchAdjustLevels",
            description = "政府公告級距調整時，批量產生新版級距並停用舊版")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "批量調整成功"),
            @ApiResponse(responseCode = "400", description = "請求參數錯誤")
    })
    public ResponseEntity<BatchAdjustLevelsResponse> batchAdjustLevels(
            @Valid @RequestBody BatchAdjustLevelsRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }
}
