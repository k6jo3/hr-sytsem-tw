package com.company.hrms.recruitment.api.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.recruitment.application.dto.job.JobOpeningDetailResponse;
import com.company.hrms.recruitment.application.dto.job.JobOpeningSearchDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/recruitment/jobs")
@Tag(name = "Job Opening Query API", description = "職缺管理查詢介面 (List, Detail)")
public class HR09JobQryController extends QueryBaseController {

    @GetMapping
    @Operation(summary = "查詢職缺列表", description = "根據條件查詢職缺列表", operationId = "getJobOpenings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功查詢")
    })
    public ResponseEntity<Page<Object>> getJobOpenings(
            @Parameter(description = "查詢條件") JobOpeningSearchDto request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查詢職缺詳情", description = "根據 ID 查詢職缺詳情", operationId = "getJobOpening")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功查詢"),
            @ApiResponse(responseCode = "404", description = "職缺不存在")
    })
    public ResponseEntity<JobOpeningDetailResponse> getJobOpening(
            @Parameter(description = "職缺 ID") @PathVariable String id,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(id, currentUser));
    }
}
