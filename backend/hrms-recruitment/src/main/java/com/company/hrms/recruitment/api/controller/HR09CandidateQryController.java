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
import com.company.hrms.recruitment.application.dto.candidate.CandidateResponse;
import com.company.hrms.recruitment.application.dto.candidate.CandidateSearchDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/recruitment/candidates")
@Tag(name = "HR09-Candidate", description = "應徵者查詢")
public class HR09CandidateQryController extends QueryBaseController {

    @Operation(summary = "查詢應徵者列表", operationId = "getCandidates")
    @ApiResponse(responseCode = "200", description = "查詢成功", content = @Content(schema = @Schema(implementation = Page.class)))
    @GetMapping
    public ResponseEntity<Object> getCandidates(
            @ParameterObject CandidateSearchDto searchDto,
            @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(searchDto, currentUser));
    }

    @Operation(summary = "查詢應徵者詳情", operationId = "getCandidateDetail")
    @ApiResponse(responseCode = "200", description = "查詢成功", content = @Content(schema = @Schema(implementation = CandidateResponse.class)))
    @GetMapping("/{id}")
    public ResponseEntity<CandidateResponse> getCandidateDetail(
            @PathVariable String id,
            @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok((CandidateResponse) getResponse(null, currentUser, id));
    }
}
