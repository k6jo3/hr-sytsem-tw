package com.company.hrms.project.api.controller;

import java.util.UUID;

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
import com.company.hrms.project.api.request.AssignTaskRequest;
import com.company.hrms.project.api.request.CreateTaskRequest;
import com.company.hrms.project.api.request.UpdateTaskProgressRequest;
import com.company.hrms.project.api.request.UpdateTaskRequest;
import com.company.hrms.project.api.response.AssignTaskResponse;
import com.company.hrms.project.api.response.CreateTaskResponse;
import com.company.hrms.project.api.response.UpdateTaskProgressResponse;
import com.company.hrms.project.api.response.UpdateTaskResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * HR06 專案管理 - 工項維護 Controller
 * 
 * 負責工項（任務）的新增、修改、進度更新、指派等寫入操作
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "HR06-工項維護", description = "專案管理 - 工項維護 API")
public class HR06TaskCmdController extends CommandBaseController {

    @Operation(summary = "建立工項", operationId = "createTask", description = "在專案下建立新工項（WBS 節點）")
    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<CreateTaskResponse> createTask(@PathVariable String projectId,
            @RequestBody CreateTaskRequest request, @CurrentUser JWTModel currentUser) throws Exception {
        request.setProjectId(UUID.fromString(projectId));
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "更新工項", operationId = "updateTask", description = "更新工項基本資訊")
    @PutMapping("/projects/{projectId}/tasks/{taskId}")
    public ResponseEntity<UpdateTaskResponse> updateTask(@PathVariable String projectId,
            @PathVariable String taskId, @RequestBody UpdateTaskRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        request.setTaskId(taskId);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "更新工項進度", operationId = "updateTaskProgress", description = "更新工項完成百分比")
    @PutMapping("/projects/{projectId}/tasks/{taskId}/progress")
    public ResponseEntity<UpdateTaskProgressResponse> updateTaskProgress(@PathVariable String projectId,
            @PathVariable String taskId, @RequestBody UpdateTaskProgressRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        request.setTaskId(taskId);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "指派工項", operationId = "assignTask", description = "將工項指派給負責人")
    @PutMapping("/projects/{projectId}/tasks/{taskId}/assign")
    public ResponseEntity<AssignTaskResponse> assignTask(@PathVariable String projectId,
            @PathVariable String taskId, @RequestBody AssignTaskRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        request.setTaskId(taskId);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }
}
