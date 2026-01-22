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
import com.company.hrms.project.api.request.CompleteProjectRequest;
import com.company.hrms.project.api.request.CreateProjectRequest;
import com.company.hrms.project.api.request.HoldProjectRequest;
import com.company.hrms.project.api.request.StartProjectRequest;
import com.company.hrms.project.api.request.UpdateProjectRequest;
import com.company.hrms.project.api.response.CompleteProjectResponse;
import com.company.hrms.project.api.response.CreateProjectResponse;
import com.company.hrms.project.api.response.HoldProjectResponse;
import com.company.hrms.project.api.response.StartProjectResponse;
import com.company.hrms.project.api.response.UpdateProjectResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * HR06 專案管理 - 專案維護 Controller
 *
 * 負責專案的新增、修改、啟動、暫停、結案等寫入操作
 */
@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "HR06-專案維護", description = "專案管理 - 專案維護 API")
public class HR06ProjectCmdController extends CommandBaseController {

    @Operation(
        summary = "建立專案",
        operationId = "createProject",
        description = "建立新專案，設定預算、時程、指派 PM。專案初始狀態為 PLANNING。"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "建立成功",
            content = @Content(schema = @Schema(implementation = CreateProjectResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "專案代碼已存在或日期範圍無效"),
        @ApiResponse(responseCode = "404", description = "客戶或專案經理不存在")
    })
    @PostMapping
    public ResponseEntity<CreateProjectResponse> createProject(
            @RequestBody CreateProjectRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(
        summary = "更新專案",
        operationId = "updateProject",
        description = "更新專案基本資訊、預算、時程等。已結案或取消的專案無法編輯。"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "更新成功",
            content = @Content(schema = @Schema(implementation = UpdateProjectResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "專案已結案/取消或日期範圍無效"),
        @ApiResponse(responseCode = "404", description = "專案不存在")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UpdateProjectResponse> updateProject(
            @Parameter(description = "專案 ID", required = true) @PathVariable String id,
            @RequestBody UpdateProjectRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        request.setProjectId(id);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(
        summary = "啟動專案",
        operationId = "startProject",
        description = "將專案狀態從 PLANNING 變更為 IN_PROGRESS，開始追蹤成本與工時。"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "啟動成功",
            content = @Content(schema = @Schema(implementation = StartProjectResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "只有規劃中的專案可以啟動"),
        @ApiResponse(responseCode = "404", description = "專案不存在")
    })
    @PutMapping("/{id}/start")
    public ResponseEntity<StartProjectResponse> startProject(
            @Parameter(description = "專案 ID", required = true) @PathVariable String id,
            @RequestBody(required = false) StartProjectRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        if (request == null) {
            request = new StartProjectRequest();
        }
        request.setProjectId(id);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(
        summary = "暫停專案",
        operationId = "holdProject",
        description = "將專案狀態從 IN_PROGRESS 變更為 ON_HOLD，記錄暫停原因。"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "暫停成功",
            content = @Content(schema = @Schema(implementation = HoldProjectResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "只有進行中的專案可以暫停，或暫停原因為必填"),
        @ApiResponse(responseCode = "404", description = "專案不存在")
    })
    @PutMapping("/{id}/hold")
    public ResponseEntity<HoldProjectResponse> holdProject(
            @Parameter(description = "專案 ID", required = true) @PathVariable String id,
            @RequestBody HoldProjectRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        request.setProjectId(id);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(
        summary = "結案",
        operationId = "completeProject",
        description = "完成專案並結案，鎖定成本資料。只有進行中的專案可以結案。"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "結案成功",
            content = @Content(schema = @Schema(implementation = CompleteProjectResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "只有進行中的專案可以結案，或仍有未完成的工項"),
        @ApiResponse(responseCode = "404", description = "專案不存在")
    })
    @PutMapping("/{id}/complete")
    public ResponseEntity<CompleteProjectResponse> completeProject(
            @Parameter(description = "專案 ID", required = true) @PathVariable String id,
            @RequestBody(required = false) CompleteProjectRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        if (request == null) {
            request = new CompleteProjectRequest();
        }
        request.setProjectId(id);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }
}
