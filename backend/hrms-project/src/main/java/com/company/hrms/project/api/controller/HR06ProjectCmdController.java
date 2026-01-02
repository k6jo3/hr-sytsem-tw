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

/**
 * HR06 專案管理 - 專案維護 Controller
 * 
 * 負責專案的新增、修改、成員管理、啟動、結案等寫入操作
 */
@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "HR06-專案維護", description = "專案管理 - 專案維護 API")
public class HR06ProjectCmdController extends CommandBaseController {

    @Operation(summary = "建立專案", operationId = "createProject", description = "建立新專案")
    @PostMapping
    public ResponseEntity<CreateProjectResponse> createProject(@RequestBody CreateProjectRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "更新專案", operationId = "updateProject", description = "更新專案基本資訊")
    @PutMapping("/{id}")
    public ResponseEntity<UpdateProjectResponse> updateProject(@PathVariable String id,
            @RequestBody UpdateProjectRequest request, @CurrentUser JWTModel currentUser) throws Exception {
        request.setProjectId(id);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "新增專案成員", operationId = "addProjectMember", description = "將員工加入專案團隊")
    @PostMapping("/{id}/members")
    public ResponseEntity<AddProjectMemberResponse> addProjectMember(@PathVariable String id,
            @RequestBody AddProjectMemberRequest request, @CurrentUser JWTModel currentUser) throws Exception {
        request.setProjectId(id);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "啟動專案", operationId = "startProject", description = "將專案狀態變更為進行中")
    @PutMapping("/{id}/start")
    public ResponseEntity<StartProjectResponse> startProject(@PathVariable String id,
            @RequestBody StartProjectRequest request, @CurrentUser JWTModel currentUser) throws Exception {
        request.setProjectId(id);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "結案", operationId = "completeProject", description = "完成專案並結案")
    @PutMapping("/{id}/complete")
    public ResponseEntity<CompleteProjectResponse> completeProject(@PathVariable String id,
            @RequestBody CompleteProjectRequest request, @CurrentUser JWTModel currentUser) throws Exception {
        request.setProjectId(id);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }
}
