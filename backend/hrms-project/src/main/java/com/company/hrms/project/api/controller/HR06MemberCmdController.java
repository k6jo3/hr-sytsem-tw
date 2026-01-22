package com.company.hrms.project.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.project.api.request.AddProjectMemberRequest;
import com.company.hrms.project.api.request.RemoveProjectMemberRequest;
import com.company.hrms.project.api.response.AddProjectMemberResponse;
import com.company.hrms.project.api.response.RemoveProjectMemberResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * HR06 專案管理 - 成員維護 Controller
 *
 * 負責專案成員的新增、移除等寫入操作
 */
@RestController
@RequestMapping("/api/v1/projects/{projectId}/members")
@Tag(name = "HR06-成員維護", description = "專案管理 - 專案成員維護 API")
public class HR06MemberCmdController extends CommandBaseController {

    @Operation(
        summary = "新增專案成員",
        operationId = "addProjectMember",
        description = "將員工加入專案團隊，設定角色、分配工時與計費費率"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "新增成功",
            content = @Content(schema = @Schema(implementation = AddProjectMemberResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "請求格式錯誤或成員已存在"),
        @ApiResponse(responseCode = "404", description = "專案或員工不存在"),
        @ApiResponse(responseCode = "403", description = "無權限執行此操作")
    })
    @PostMapping
    public ResponseEntity<AddProjectMemberResponse> addProjectMember(
            @Parameter(description = "專案 ID", required = true) @PathVariable String projectId,
            @RequestBody AddProjectMemberRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        request.setProjectId(projectId);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(
        summary = "移除專案成員",
        operationId = "removeProjectMember",
        description = "將成員從專案團隊中移除，設定離開日期。專案經理不可自行移除。"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "移除成功",
            content = @Content(schema = @Schema(implementation = RemoveProjectMemberResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "專案經理不可移除或成員有未結算工時"),
        @ApiResponse(responseCode = "404", description = "專案或成員不存在"),
        @ApiResponse(responseCode = "403", description = "無權限執行此操作")
    })
    @DeleteMapping("/{memberId}")
    public ResponseEntity<RemoveProjectMemberResponse> removeProjectMember(
            @Parameter(description = "專案 ID", required = true) @PathVariable String projectId,
            @Parameter(description = "成員 ID", required = true) @PathVariable String memberId,
            @CurrentUser JWTModel currentUser) throws Exception {
        RemoveProjectMemberRequest request = new RemoveProjectMemberRequest();
        request.setProjectId(projectId);
        request.setMemberId(memberId);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }
}
