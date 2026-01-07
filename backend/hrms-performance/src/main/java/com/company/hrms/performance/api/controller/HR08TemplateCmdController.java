package com.company.hrms.performance.api.controller;

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
import com.company.hrms.performance.api.request.SaveTemplateRequest;
import com.company.hrms.performance.api.request.StartCycleRequest;
import com.company.hrms.performance.api.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * HR08 績效考核 - 考核範本維護 Controller
 */
@RestController
@RequestMapping("/api/v1/performance/cycles")
@Tag(name = "HR08-考核範本維護", description = "績效考核 - 考核範本維護 API")
public class HR08TemplateCmdController extends CommandBaseController {

    @Operation(summary = "儲存考核範本", operationId = "saveTemplate")
    @PostMapping("/{id}/template")
    public ResponseEntity<SuccessResponse> saveTemplate(@PathVariable String id,
            @RequestBody SaveTemplateRequest request, @CurrentUser JWTModel currentUser) throws Exception {
        request.setCycleId(id);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "發布考核範本", operationId = "publishTemplate")
    @PutMapping("/{id}/template/publish")
    public ResponseEntity<SuccessResponse> publishTemplate(@PathVariable String id,
            @CurrentUser JWTModel currentUser) throws Exception {
        StartCycleRequest request = StartCycleRequest.builder().cycleId(id).build();
        return ResponseEntity.ok(execCommand(request, currentUser));
    }
}
