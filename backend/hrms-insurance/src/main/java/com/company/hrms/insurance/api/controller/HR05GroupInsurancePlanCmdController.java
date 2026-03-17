package com.company.hrms.insurance.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.insurance.api.request.AddPlanTierRequest;
import com.company.hrms.insurance.api.request.CreateGroupInsurancePlanRequest;
import com.company.hrms.insurance.api.response.GroupInsurancePlanDetailResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * HR05 團體保險方案管理 Command Controller
 */
@RestController
@RequestMapping("/api/v1/insurance/group-plans")
@Slf4j
@Tag(name = "HR05-GroupInsurancePlan", description = "團體保險方案管理")
public class HR05GroupInsurancePlanCmdController extends CommandBaseController {

    @PostMapping
    @Operation(summary = "建立團體保險方案", operationId = "createGroupInsurancePlan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "建立成功"),
            @ApiResponse(responseCode = "400", description = "請求參數驗證失敗"),
            @ApiResponse(responseCode = "409", description = "方案代碼已存在")
    })
    public ResponseEntity<GroupInsurancePlanDetailResponse> createGroupInsurancePlan(
            @Valid @RequestBody CreateGroupInsurancePlanRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @PostMapping("/{id}/tiers")
    @Operation(summary = "新增方案職等對應", operationId = "addPlanTier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "新增成功"),
            @ApiResponse(responseCode = "400", description = "請求參數驗證失敗或職等已存在"),
            @ApiResponse(responseCode = "404", description = "方案不存在")
    })
    public ResponseEntity<GroupInsurancePlanDetailResponse> addPlanTier(
            @PathVariable String id,
            @Valid @RequestBody AddPlanTierRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, id));
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "停用團體保險方案", operationId = "deactivateGroupInsurancePlan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "停用成功"),
            @ApiResponse(responseCode = "404", description = "方案不存在")
    })
    public ResponseEntity<GroupInsurancePlanDetailResponse> deactivateGroupInsurancePlan(
            @PathVariable String id,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(null, currentUser, id));
    }
}
