package com.company.hrms.iam.api.controller.system;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.iam.api.request.system.ListSystemParametersRequest;
import com.company.hrms.iam.api.response.system.FeatureToggleResponse;
import com.company.hrms.iam.api.response.system.ScheduledJobConfigResponse;
import com.company.hrms.iam.api.response.system.SystemParameterResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 系統管理查詢 Controller
 * 提供系統參數、功能開關、排程任務的查詢 API
 */
@RestController
@RequestMapping("/api/v1/system")
@Tag(name = "HR01-System-Query", description = "系統管理查詢操作")
public class HR01SystemQryController extends QueryBaseController {

    /**
     * 查詢所有系統參數
     * 方法名 listSystemParameters → 對應 Service bean "listSystemParametersServiceImpl"
     */
    @Operation(summary = "查詢所有系統參數", operationId = "listSystemParameters")
    @GetMapping("/parameters")
    public ResponseEntity<List<SystemParameterResponse>> listSystemParameters(
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        List<SystemParameterResponse> response = getResponse(new ListSystemParametersRequest(), currentUser);
        return ResponseEntity.ok(response);
    }

    /**
     * 查詢所有功能開關
     * 方法名 listFeatureToggles → 對應 Service bean "listFeatureTogglesServiceImpl"
     */
    @Operation(summary = "查詢所有功能開關", operationId = "listFeatureToggles")
    @GetMapping("/features")
    public ResponseEntity<List<FeatureToggleResponse>> listFeatureToggles(
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        List<FeatureToggleResponse> response = getResponse(null, currentUser);
        return ResponseEntity.ok(response);
    }

    /**
     * 查詢所有排程任務
     * 方法名 listScheduledJobs → 對應 Service bean "listScheduledJobsServiceImpl"
     */
    @Operation(summary = "查詢所有排程任務", operationId = "listScheduledJobs")
    @GetMapping("/jobs")
    public ResponseEntity<List<ScheduledJobConfigResponse>> listScheduledJobs(
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        List<ScheduledJobConfigResponse> response = getResponse(null, currentUser);
        return ResponseEntity.ok(response);
    }
}
