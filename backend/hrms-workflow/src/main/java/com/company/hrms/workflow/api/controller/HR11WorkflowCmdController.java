package com.company.hrms.workflow.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.workflow.api.request.ApproveTaskRequest;
import com.company.hrms.workflow.api.request.RejectTaskRequest;
import com.company.hrms.workflow.api.request.StartWorkflowRequest;
import com.company.hrms.workflow.api.response.ApproveTaskResponse;
import com.company.hrms.workflow.api.response.RejectTaskResponse;
import com.company.hrms.workflow.api.response.StartWorkflowResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * HR11 簽核流程 Command Controller
 */
@RestController
@RequestMapping("/api/v1/workflows")
@Tag(name = "HR11 Workflow Command", description = "簽核流程命令服務 (啟動、核准、駁回)")
public class HR11WorkflowCmdController extends CommandBaseController {

    @Operation(summary = "發起流程", description = "啟動一個新的簽核流程實例")
    @PostMapping("/start")
    public ResponseEntity<StartWorkflowResponse> startWorkflow(
            @RequestBody StartWorkflowRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "核准任務", description = "核准目前指派的任務")
    @PostMapping("/approve")
    public ResponseEntity<ApproveTaskResponse> approveTask(
            @RequestBody ApproveTaskRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "駁回任務", description = "駁回目前指派的任務")
    @PostMapping("/reject")
    public ResponseEntity<RejectTaskResponse> rejectTask(
            @RequestBody RejectTaskRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "建立代理人", description = "設定特定期間的代理人")
    @PostMapping("/delegations")
    public ResponseEntity<com.company.hrms.workflow.api.response.CreateDelegationResponse> createDelegation(
            @RequestBody com.company.hrms.workflow.api.request.CreateDelegationRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    // --- Definitions ---

    @Operation(summary = "建立流程定義", description = "建立新的流程定義草稿")
    @PostMapping("/definitions")
    public ResponseEntity<com.company.hrms.workflow.api.response.CreateWorkflowDefinitionResponse> createDefinition(
            @RequestBody com.company.hrms.workflow.api.request.CreateWorkflowDefinitionRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "發布流程定義", description = "將流程定義發布為正式版本")
    @org.springframework.web.bind.annotation.PutMapping("/definitions/{definitionId}/publish")
    public ResponseEntity<Void> publishDefinition(
            @org.springframework.web.bind.annotation.PathVariable("definitionId") String definitionId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity
                .ok(execCommand(new com.company.hrms.workflow.application.service.PublishWorkflowDefinitionRequest(),
                        currentUser, definitionId));
    }

    // --- Delegation ---

    // Note: createDelegation is already present above.

    @Operation(summary = "刪除代理人", description = "刪除指定的代理人設定")
    @org.springframework.web.bind.annotation.DeleteMapping("/delegations/{delegationId}")
    public ResponseEntity<Void> deleteDelegation(
            @org.springframework.web.bind.annotation.PathVariable("delegationId") String delegationId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity
                .ok(execCommand(new com.company.hrms.workflow.application.service.DeleteDelegationRequest(),
                        currentUser, delegationId));
    }
}
