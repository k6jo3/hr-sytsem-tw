package com.company.hrms.performance.api.controller;

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
import com.company.hrms.performance.api.request.FinalizeReviewRequest;
import com.company.hrms.performance.api.request.SubmitReviewRequest;
import com.company.hrms.performance.api.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * HR08 績效考核 - 考核維護 Controller
 */
@RestController
@RequestMapping("/api/v1/performance/reviews")
@Tag(name = "HR08-考核維護", description = "績效考核 - 考核維護 API")
public class HR08ReviewCmdController extends CommandBaseController {

    @Operation(summary = "提交考核", operationId = "submitReview")
    @PostMapping("/{id}/submit")
    public ResponseEntity<SuccessResponse> submitReview(@PathVariable String id,
            @RequestBody SubmitReviewRequest request, @CurrentUser JWTModel currentUser) throws Exception {
        request.setReviewId(id);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "確認最終評等", operationId = "finalizeReview")
    @PutMapping("/{id}/finalize")
    public ResponseEntity<SuccessResponse> finalizeReview(@PathVariable String id,
            @RequestBody FinalizeReviewRequest request, @CurrentUser JWTModel currentUser) throws Exception {
        request.setReviewId(id);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }
}
