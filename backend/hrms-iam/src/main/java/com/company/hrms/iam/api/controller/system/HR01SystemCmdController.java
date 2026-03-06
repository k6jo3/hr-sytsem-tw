package com.company.hrms.iam.api.controller.system;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.iam.api.request.system.ToggleFeatureRequest;
import com.company.hrms.iam.api.request.system.UpdateScheduledJobRequest;
import com.company.hrms.iam.api.request.system.UpdateSystemParameterRequest;
import com.company.hrms.iam.api.response.system.FeatureToggleResponse;
import com.company.hrms.iam.api.response.system.ScheduledJobConfigResponse;
import com.company.hrms.iam.api.response.system.SystemParameterResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * 系統管理寫入 Controller
 * 提供系統參數更新、功能開關切換、排程任務配置的 API
 */
@RestController
@RequestMapping("/api/v1/system")
@Tag(name = "HR01-System-Command", description = "系統管理寫入操作")
@PreAuthorize("hasAuthority('ADMIN') or hasRole('ADMIN')")
public class HR01SystemCmdController extends CommandBaseController {

    /**
     * 更新系統參數
     * 方法名 updateSystemParameter → 對應 Service bean "updateSystemParameterServiceImpl"
     */
    @Operation(summary = "更新系統參數", operationId = "updateSystemParameter")
    @PutMapping("/parameters/{paramCode}")
    public ResponseEntity<SystemParameterResponse> updateSystemParameter(
            @PathVariable String paramCode,
            @RequestBody @Valid UpdateSystemParameterRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, paramCode));
    }

    /**
     * 切換功能開關
     * 方法名 toggleFeature → 對應 Service bean "toggleFeatureServiceImpl"
     */
    @Operation(summary = "切換功能開關", operationId = "toggleFeature")
    @PutMapping("/features/{featureCode}/toggle")
    public ResponseEntity<FeatureToggleResponse> toggleFeature(
            @PathVariable String featureCode,
            @RequestBody(required = false) ToggleFeatureRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, featureCode));
    }

    /**
     * 更新排程任務配置
     * 方法名 updateScheduledJob → 對應 Service bean "updateScheduledJobServiceImpl"
     */
    @Operation(summary = "更新排程任務配置", operationId = "updateScheduledJob")
    @PutMapping("/jobs/{jobCode}")
    public ResponseEntity<ScheduledJobConfigResponse> updateScheduledJob(
            @PathVariable String jobCode,
            @RequestBody @Valid UpdateScheduledJobRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, jobCode));
    }
}
