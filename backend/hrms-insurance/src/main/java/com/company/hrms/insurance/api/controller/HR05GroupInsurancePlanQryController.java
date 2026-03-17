package com.company.hrms.insurance.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.api.response.PageResponse;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.insurance.api.request.GetGroupInsurancePlanListRequest;
import com.company.hrms.insurance.api.response.GroupInsurancePlanDetailResponse;
import com.company.hrms.insurance.api.response.GroupInsurancePlanResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * HR05 團體保險方案管理 Query Controller
 */
@RestController
@RequestMapping("/api/v1/insurance/group-plans")
@Slf4j
@Tag(name = "HR05-GroupInsurancePlan", description = "團體保險方案管理")
public class HR05GroupInsurancePlanQryController extends QueryBaseController {

    @GetMapping
    @Operation(summary = "查詢團體保險方案列表", operationId = "getGroupInsurancePlanList")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查詢成功")
    })
    public ResponseEntity<PageResponse<GroupInsurancePlanResponse>> getGroupInsurancePlanList(
            GetGroupInsurancePlanListRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查詢團體保險方案詳情", operationId = "getGroupInsurancePlanDetail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查詢成功"),
            @ApiResponse(responseCode = "404", description = "方案不存在")
    })
    public ResponseEntity<GroupInsurancePlanDetailResponse> getGroupInsurancePlanDetail(
            @PathVariable String id,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(id, currentUser));
    }
}
