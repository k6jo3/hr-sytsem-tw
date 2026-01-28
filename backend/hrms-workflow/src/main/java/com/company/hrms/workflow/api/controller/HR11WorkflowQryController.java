package com.company.hrms.workflow.api.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.workflow.api.request.GetPendingTasksRequest;
import com.company.hrms.workflow.api.request.GetWorkflowHistoryRequest;
import com.company.hrms.workflow.api.response.PendingTaskResponse;
import com.company.hrms.workflow.api.response.WorkflowHistoryResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.company.hrms.workflow.api.request.GetDelegationsRequest;
import com.company.hrms.workflow.api.request.GetMyApplicationsRequest;
import com.company.hrms.workflow.api.request.GetWorkflowDefinitionListRequest;
import com.company.hrms.workflow.api.request.GetWorkflowInstanceDetailRequest;
import com.company.hrms.workflow.api.response.GetDelegationsResponse;
import com.company.hrms.workflow.api.response.MyApplicationsResponse;
import com.company.hrms.workflow.api.response.WorkflowDefinitionResponse;
import com.company.hrms.workflow.api.response.WorkflowInstanceDetailResponse;

/**
 * HR11 簽核流程 Query Controller
 */
@RestController
@RequestMapping("/api/v1/workflows")
@Tag(name = "HR11 Workflow Query", description = "簽核流程查詢服務 (待辦、歷史)")
public class HR11WorkflowQryController extends QueryBaseController {

    @Operation(summary = "查詢待辦任務", description = "查詢使用者的待辦任務列表")
    @GetMapping("/pending-tasks")
    public ResponseEntity<Page<PendingTaskResponse>> getPendingTasks(
            @ParameterObject @ModelAttribute GetPendingTasksRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    @Operation(summary = "查詢流程歷史", description = "查詢特定流程實例的歷程")
    @GetMapping("/{instanceId}/history")
    public ResponseEntity<WorkflowHistoryResponse> getWorkflowHistory(
            @PathVariable("instanceId") String instanceId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {

        GetWorkflowHistoryRequest request = new GetWorkflowHistoryRequest();
        request.setInstanceId(instanceId);

        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    @Operation(summary = "查詢代理人", description = "查詢使用者(或自己)的代理人設定")
    @GetMapping("/delegations")
    public ResponseEntity<GetDelegationsResponse> getDelegations(
            @ParameterObject @ModelAttribute GetDelegationsRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    // --- Definitions ---

    @Operation(summary = "查詢流程定義列表", description = "查詢系統中的流程定義")
    @GetMapping("/definitions")
    public ResponseEntity<Page<WorkflowDefinitionResponse>> getDefinitions(
            @ParameterObject @ModelAttribute GetWorkflowDefinitionListRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    // --- Applications ---

    @Operation(summary = "查詢我的申請", description = "查詢使用者提交的申請單")
    @GetMapping("/my/applications")
    public ResponseEntity<Page<MyApplicationsResponse>> getMyApplications(
            @ParameterObject @ModelAttribute GetMyApplicationsRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    // --- Instance Detail ---

    @Operation(summary = "查詢流程實例詳情", description = "查詢流程實例的詳細資訊與歷程")
    @GetMapping("/instances/{instanceId}")
    public ResponseEntity<WorkflowInstanceDetailResponse> getInstanceDetail(
            @PathVariable("instanceId") String instanceId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(
                new GetWorkflowInstanceDetailRequest(), currentUser, instanceId));
    }
}
