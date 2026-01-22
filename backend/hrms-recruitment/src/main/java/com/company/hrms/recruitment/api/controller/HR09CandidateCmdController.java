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
import com.company.hrms.recruitment.application.dto.candidate.CandidateResponse;
import com.company.hrms.recruitment.application.dto.candidate.CreateCandidateRequest;
import com.company.hrms.recruitment.application.dto.candidate.UpdateCandidateStatusRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/candidates")
@Tag(name = "HR09-Candidate", description = "應徵者管理")
public class HR09CandidateCmdController extends CommandBaseController {

    @Operation(summary = "建立應徵者", operationId = "createCandidate")
    @ApiResponse(responseCode = "200", description = "建立成功", content = @Content(schema = @Schema(implementation = CandidateResponse.class)))
    @PostMapping
    public ResponseEntity<CandidateResponse> createCandidate(
            @RequestBody CreateCandidateRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok((CandidateResponse) execCommand(request, currentUser));
    }

    @Operation(summary = "更新應徵者狀態", operationId = "updateCandidateStatus")
    @ApiResponse(responseCode = "200", description = "更新成功", content = @Content(schema = @Schema(implementation = CandidateResponse.class)))
    @PutMapping("/{id}/status")
    public ResponseEntity<CandidateResponse> updateCandidateStatus(
            @PathVariable String id,
            @RequestBody UpdateCandidateStatusRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok((CandidateResponse) execCommand(request, currentUser, id));
    }

    @Operation(summary = "拒絕應徵者", operationId = "rejectCandidate")
    @ApiResponse(responseCode = "200", description = "拒絕成功", content = @Content(schema = @Schema(implementation = CandidateResponse.class)))
    @PutMapping("/{id}/reject")
    public ResponseEntity<CandidateResponse> rejectCandidate(
            @PathVariable String id,
            @RequestBody UpdateCandidateStatusRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok((CandidateResponse) execCommand(request, currentUser, id));
    }

    @Operation(summary = "錄取應徵者", operationId = "hireCandidate")
    @ApiResponse(responseCode = "200", description = "錄取成功", content = @Content(schema = @Schema(implementation = CandidateResponse.class)))
    @PutMapping("/{id}/hire")
    public ResponseEntity<CandidateResponse> hireCandidate(
            @PathVariable String id,
            @RequestBody UpdateCandidateStatusRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok((CandidateResponse) execCommand(request, currentUser, id));
    }
}
