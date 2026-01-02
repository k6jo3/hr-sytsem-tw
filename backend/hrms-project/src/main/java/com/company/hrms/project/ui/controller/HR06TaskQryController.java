package com.company.hrms.project.ui.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.project.api.request.GetWBSTreeRequest;
import com.company.hrms.project.api.response.GetWBSTreeResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "專案管理-工項查詢")
public class HR06TaskQryController extends QueryBaseController {

    @Operation(summary = "查詢WBS結構", operationId = "getWBSTree")
    @GetMapping("/projects/{projectId}/wbs")
    public ResponseEntity<GetWBSTreeResponse> getWBSTree(@PathVariable String projectId,
            @CurrentUser JWTModel currentUser) throws Exception {
        GetWBSTreeRequest request = new GetWBSTreeRequest();
        request.setProjectId(projectId);
        return ResponseEntity.ok(getResponse(request, currentUser));
    }
}
