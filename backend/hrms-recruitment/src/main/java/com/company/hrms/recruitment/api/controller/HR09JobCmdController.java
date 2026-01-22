package com.company.hrms.recruitment.api.controller;

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
import com.company.hrms.recruitment.application.dto.job.CloseJobOpeningRequest;
import com.company.hrms.recruitment.application.dto.job.CloseJobOpeningResponse;
import com.company.hrms.recruitment.application.dto.job.CreateJobOpeningRequest;
import com.company.hrms.recruitment.application.dto.job.CreateJobOpeningResponse;
import com.company.hrms.recruitment.application.dto.job.UpdateJobOpeningRequest;
import com.company.hrms.recruitment.application.dto.job.UpdateJobOpeningResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/recruitment/jobs")
@Tag(name = "Job Opening Command API", description = "職缺管理指令介面 (Create, Update, Close)")
public class HR09JobCmdController extends CommandBaseController {

    @PostMapping
    @Operation(summary = "新增職缺", description = "建立新的職缺申請", operationId = "createJobOpening")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功建立"),
            @ApiResponse(responseCode = "400", description = "輸入資料有誤")
    })
    public ResponseEntity<CreateJobOpeningResponse> createJobOpening(
            @RequestBody CreateJobOpeningRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新職缺", description = "更新現有的職缺資訊", operationId = "updateJobOpening")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功更新"),
            @ApiResponse(responseCode = "404", description = "職缺不存在")
    })
    public ResponseEntity<UpdateJobOpeningResponse> updateJobOpening(
            @Parameter(description = "職缺 ID") @PathVariable String id,
            @RequestBody UpdateJobOpeningRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, id));
    }

    @PostMapping("/{id}/close")
    @Operation(summary = "關閉職缺", description = "關閉指定的職缺", operationId = "closeJobOpening")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功關閉"),
            @ApiResponse(responseCode = "404", description = "職缺不存在")
    })
    public ResponseEntity<CloseJobOpeningResponse> closeJobOpening(
            @Parameter(description = "職缺 ID") @PathVariable String id,
            @RequestBody CloseJobOpeningRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, id));
    }
}
