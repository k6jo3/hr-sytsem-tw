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

/**
 * HR06 專案管理 - 工項查詢 Controller
 * 
 * 負責工項（任務）的查詢操作，包含 WBS 結構查詢、工項詳情查詢
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "HR06-工項查詢", description = "專案管理 - 工項查詢 API")
public class HR06TaskQryController extends QueryBaseController {

    @Operation(summary = "查詢 WBS 結構", operationId = "getWBSTree", description = "查詢專案的 WBS（工作分解結構）樹狀結構")
    @GetMapping("/projects/{projectId}/wbs")
    public ResponseEntity<GetWBSTreeResponse> getWBSTree(@PathVariable String projectId,
            @CurrentUser JWTModel currentUser) throws Exception {
        GetWBSTreeRequest request = new GetWBSTreeRequest();
        request.setProjectId(projectId);
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    @Operation(summary = "查詢工項詳情", operationId = "getTaskDetail", description = "根據工項 ID 查詢工項詳細資訊")
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
