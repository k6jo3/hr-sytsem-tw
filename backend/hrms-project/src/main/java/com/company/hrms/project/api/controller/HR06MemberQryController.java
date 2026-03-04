package com.company.hrms.project.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.project.api.request.GetProjectMemberListRequest;
import com.company.hrms.project.api.response.ProjectMemberDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * HR06 專案管理 - 成員查詢 Controller
 */
@RestController
@RequestMapping("/api/v1/projects/{projectId}/members")
@Tag(name = "HR06-成員查詢", description = "專案管理 - 專案成員查詢 API")
public class HR06MemberQryController extends QueryBaseController {

    @Operation(summary = "查詢專案成員列表", operationId = "getProjectMembers",
            description = "查詢指定專案的成員列表")
    @GetMapping
    public ResponseEntity<List<ProjectMemberDto>> getProjectMembers(
            @Parameter(description = "專案 ID", required = true) @PathVariable String projectId,
            @CurrentUser JWTModel currentUser) throws Exception {
        GetProjectMemberListRequest request = new GetProjectMemberListRequest();
        request.setProjectId(projectId);
        return ResponseEntity.ok(getResponse(request, currentUser));
    }
}
