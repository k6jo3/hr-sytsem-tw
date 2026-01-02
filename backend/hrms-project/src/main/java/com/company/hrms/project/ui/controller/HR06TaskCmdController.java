package com.company.hrms.project.ui.controller;

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

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "專案管理-工項維護")
public class HR06TaskCmdController extends CommandBaseController {

    @Operation(summary = "建立工項(WBS)", operationId = "createTask")
    @PostMapping
    public ResponseEntity<CreateTaskResponse> createTask(@RequestBody CreateTaskRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "更新工項", operationId = "updateTask")
    @PutMapping("/{id}")
    public ResponseEntity<UpdateTaskResponse> updateTask(@PathVariable String id,
            @RequestBody UpdateTaskRequest request, @CurrentUser JWTModel currentUser) throws Exception {
        request.setTaskId(id);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "更新工項進度", operationId = "updateTaskProgress")
    @PutMapping("/{id}/progress")
    public ResponseEntity<UpdateTaskProgressResponse> updateTaskProgress(@PathVariable String id,
            @RequestBody UpdateTaskProgressRequest request, @CurrentUser JWTModel currentUser) throws Exception {
        request.setTaskId(id);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "指派工項負責人", operationId = "assignTask")
    @PutMapping("/{id}/assign")
    public ResponseEntity<AssignTaskResponse> assignTask(@PathVariable String id,
            @RequestBody AssignTaskRequest request, @CurrentUser JWTModel currentUser) throws Exception {
        request.setTaskId(id);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }
}
