package com.company.hrms.project.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.project.api.request.GetTaskDetailRequest;
import com.company.hrms.project.api.request.GetWBSTreeRequest;
import com.company.hrms.project.api.response.GetTaskDetailResponse;
import com.company.hrms.project.api.response.GetWBSTreeResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "HR06-Task-Query")
public class HR06TaskQryController extends QueryBaseController {

    @Operation(summary = "Query WBS tree", operationId = "getWBSTree")
    @GetMapping("/projects/{projectId}/wbs")
    public ResponseEntity<GetWBSTreeResponse> getWBSTree(@PathVariable String projectId,
            @CurrentUser JWTModel currentUser) throws Exception {
        GetWBSTreeRequest request = new GetWBSTreeRequest();
        request.setProjectId(projectId);
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    @Operation(summary = "Query task detail", operationId = "getTaskDetail")
    @GetMapping("/projects/{projectId}/tasks/{taskId}")
    public ResponseEntity<GetTaskDetailResponse> getTaskDetail(
            @PathVariable String projectId,
            @PathVariable String taskId,
            @CurrentUser JWTModel currentUser) throws Exception {
        GetTaskDetailRequest request = new GetTaskDetailRequest();
        request.setTaskId(taskId);
        return ResponseEntity.ok(getResponse(request, currentUser));
    }
}
