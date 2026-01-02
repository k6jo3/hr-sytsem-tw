package com.company.hrms.project.api.controller;

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
import com.company.hrms.project.api.request.AddProjectMemberRequest;
import com.company.hrms.project.api.request.CompleteProjectRequest;
import com.company.hrms.project.api.request.CreateProjectRequest;
import com.company.hrms.project.api.request.StartProjectRequest;
import com.company.hrms.project.api.request.UpdateProjectRequest;
import com.company.hrms.project.api.response.AddProjectMemberResponse;
import com.company.hrms.project.api.response.CompleteProjectResponse;
import com.company.hrms.project.api.response.CreateProjectResponse;
import com.company.hrms.project.api.response.StartProjectResponse;
import com.company.hrms.project.api.response.UpdateProjectResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "HR06-Project-Command")
public class HR06ProjectCmdController extends CommandBaseController {

    @Operation(summary = "Create project", operationId = "createProject")
    @PostMapping
    public ResponseEntity<CreateProjectResponse> createProject(@RequestBody CreateProjectRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "Update project", operationId = "updateProject")
    @PutMapping("/{id}")
    public ResponseEntity<UpdateProjectResponse> updateProject(@PathVariable String id,
            @RequestBody UpdateProjectRequest request, @CurrentUser JWTModel currentUser) throws Exception {
        request.setProjectId(id);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "Add project member", operationId = "addProjectMember")
    @PostMapping("/{id}/members")
    public ResponseEntity<AddProjectMemberResponse> addProjectMember(@PathVariable String id,
            @RequestBody AddProjectMemberRequest request, @CurrentUser JWTModel currentUser) throws Exception {
        request.setProjectId(id);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "Start project", operationId = "startProject")
    @PutMapping("/{id}/start")
    public ResponseEntity<StartProjectResponse> startProject(@PathVariable String id,
            @RequestBody StartProjectRequest request, @CurrentUser JWTModel currentUser) throws Exception {
        request.setProjectId(id);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "Complete project", operationId = "completeProject")
    @PutMapping("/{id}/complete")
    public ResponseEntity<CompleteProjectResponse> completeProject(@PathVariable String id,
            @RequestBody CompleteProjectRequest request, @CurrentUser JWTModel currentUser) throws Exception {
        request.setProjectId(id);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }
}
