package com.company.hrms.performance.api.controller;

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
import com.company.hrms.performance.api.request.CreateCycleRequest;
import com.company.hrms.performance.api.request.DeleteCycleRequest;
import com.company.hrms.performance.api.request.StartCycleRequest;
import com.company.hrms.performance.api.request.UpdateCycleRequest;
import com.company.hrms.performance.api.response.CreateCycleResponse;
import com.company.hrms.performance.api.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * HR08 績效考核 - 考核週期維護 Controller
 */
@RestController
@RequestMapping("/api/v1/performance/cycles")
@Tag(name = "HR08-考核週期維護", description = "績效考核 - 考核週期維護 API")
public class HR08CycleCmdController extends CommandBaseController {

    @Operation(summary = "建立考核週期", operationId = "createCycle")
    @PostMapping
    public ResponseEntity<CreateCycleResponse> createCycle(@RequestBody CreateCycleRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "更新考核週期", operationId = "updateCycle")
    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse> updateCycle(@PathVariable String id,
            @RequestBody UpdateCycleRequest request, @CurrentUser JWTModel currentUser) throws Exception {
        request.setCycleId(id);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "刪除考核週期", operationId = "deleteCycle")
    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse> deleteCycle(@PathVariable String id,
            @CurrentUser JWTModel currentUser) throws Exception {
        DeleteCycleRequest request = DeleteCycleRequest.builder().cycleId(id).build();
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "啟動考核週期", operationId = "startCycle")
    @PutMapping("/{id}/start")
    public ResponseEntity<SuccessResponse> startCycle(@PathVariable String id,
            @CurrentUser JWTModel currentUser) throws Exception {
        StartCycleRequest request = StartCycleRequest.builder().cycleId(id).build();
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "完成考核週期", operationId = "completeCycle")
    @PutMapping("/{id}/complete")
    public ResponseEntity<SuccessResponse> completeCycle(@PathVariable String id,
            @CurrentUser JWTModel currentUser) throws Exception {
        StartCycleRequest request = StartCycleRequest.builder().cycleId(id).build();
        return ResponseEntity.ok(execCommand(request, currentUser));
    }
}
