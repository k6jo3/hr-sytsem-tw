package com.company.hrms.project.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.project.api.request.GetMyProjectsRequest;
import com.company.hrms.project.api.request.GetProjectDetailRequest;
import com.company.hrms.project.api.request.GetProjectListRequest;
import com.company.hrms.project.api.response.GetMyProjectsResponse;
import com.company.hrms.project.api.response.GetProjectDetailResponse;
import com.company.hrms.project.api.response.GetProjectListResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "HR06-Project-Query")
public class HR06ProjectQryController extends QueryBaseController {

    @Operation(summary = "Query project list", operationId = "getProjectList")
    @GetMapping
    public ResponseEntity<GetProjectListResponse> getProjectList(@ModelAttribute GetProjectListRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    @Operation(summary = "Query project detail", operationId = "getProjectDetail")
    @GetMapping("/{id}")
    public ResponseEntity<GetProjectDetailResponse> getProjectDetail(@PathVariable String id,
            @CurrentUser JWTModel currentUser) throws Exception {
        GetProjectDetailRequest request = new GetProjectDetailRequest();
        request.setProjectId(id);
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    @Operation(summary = "Query my projects (ESS)", operationId = "getMyProjects")
    @GetMapping("/my")
    public ResponseEntity<GetMyProjectsResponse> getMyProjects(
            @ModelAttribute GetMyProjectsRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser));
    }
}
