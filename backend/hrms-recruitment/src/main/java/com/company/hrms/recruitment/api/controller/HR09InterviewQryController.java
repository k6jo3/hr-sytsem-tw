package com.company.hrms.recruitment.api.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.recruitment.application.dto.interview.InterviewResponse;
import com.company.hrms.recruitment.application.dto.interview.InterviewSearchDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 面試查詢 Controller
 */
@RestController
@RequestMapping("/api/v1/recruitment/interviews")
@Tag(name = "HR09-Interview", description = "面試查詢")
public class HR09InterviewQryController extends QueryBaseController {

    @GetMapping
    @Operation(summary = "查詢面試列表", operationId = "getInterviews")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查詢成功", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Object> getInterviews(
            @ParameterObject InterviewSearchDto searchDto,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(searchDto, currentUser));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查詢面試詳情", operationId = "getInterview")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查詢成功", content = @Content(schema = @Schema(implementation = InterviewResponse.class))),
            @ApiResponse(responseCode = "404", description = "面試不存在")
    })
    public ResponseEntity<InterviewResponse> getInterview(
            @Parameter(description = "面試 ID") @PathVariable String id,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(null, currentUser, id));
    }
}
