package com.company.hrms.performance.api.controller;

import org.springdoc.core.annotations.ParameterObject;
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
import com.company.hrms.performance.api.request.GetCycleDetailRequest;
import com.company.hrms.performance.api.request.GetCyclesRequest;
import com.company.hrms.performance.api.response.GetCyclesResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * HR08 績效考核 - 考核週期查詢 Controller
 */
@RestController
@RequestMapping("/api/v1/performance/cycles")
@Tag(name = "HR08-考核週期查詢", description = "績效考核 - 考核週期查詢 API")
public class HR08CycleQryController extends QueryBaseController {

    @Operation(summary = "查詢考核週期列表", operationId = "getCycles")
    @GetMapping
    public ResponseEntity<PageResponse<GetCyclesResponse.CycleSummary>> getCycles(
            @ParameterObject @ModelAttribute GetCyclesRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    @Operation(summary = "查詢考核週期詳情", operationId = "getCycleDetail")
    @GetMapping("/{id}")
    public ResponseEntity<GetCyclesResponse.CycleSummary> getCycleDetail(@PathVariable String id,
            @CurrentUser JWTModel currentUser) throws Exception {
        GetCycleDetailRequest request = GetCycleDetailRequest.builder().cycleId(id).build();
        return ResponseEntity.ok(getResponse(request, currentUser));
    }
}
