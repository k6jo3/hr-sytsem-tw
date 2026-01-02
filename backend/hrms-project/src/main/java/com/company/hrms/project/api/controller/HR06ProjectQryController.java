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

/**
 * HR06 專案管理 - 專案查詢 Controller
 * 
 * 負責專案的查詢操作，包含列表查詢、詳情查詢、我的專案（ESS）
 */
@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "HR06-專案查詢", description = "專案管理 - 專案查詢 API")
public class HR06ProjectQryController extends QueryBaseController {

    @Operation(summary = "查詢專案列表", operationId = "getProjectList", description = "查詢專案列表，支援分頁與過濾")
    @GetMapping
    public ResponseEntity<GetProjectListResponse> getProjectList(@ModelAttribute GetProjectListRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    @Operation(summary = "查詢專案詳情", operationId = "getProjectDetail", description = "根據專案 ID 查詢專案詳細資訊")
    @GetMapping("/{id}")
    public ResponseEntity<GetProjectDetailResponse> getProjectDetail(@PathVariable String id,
            @CurrentUser JWTModel currentUser) throws Exception {
        GetProjectDetailRequest request = new GetProjectDetailRequest();
        request.setProjectId(id);
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    @Operation(summary = "查詢我的專案", operationId = "getMyProjects", description = "ESS - 查詢當前使用者參與的專案列表")
    @GetMapping("/my")
    public ResponseEntity<GetMyProjectsResponse> getMyProjects(
            @ModelAttribute GetMyProjectsRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser));
    }
}
