package com.company.hrms.performance.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.performance.api.request.StartCycleRequest;
import com.company.hrms.performance.api.response.GetReviewsResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * HR08 績效考核 - 考核查詢 Controller
 */
@RestController
@RequestMapping("/api/v1/performance/reviews")
@Tag(name = "HR08-考核查詢", description = "績效考核 - 考核查詢 API")
public class HR08ReviewQryController extends QueryBaseController {

    @Operation(summary = "查詢我的考核", operationId = "getMyReviews")
    @GetMapping("/my")
    public ResponseEntity<GetReviewsResponse> getMyReviews(@CurrentUser JWTModel currentUser) throws Exception {
        StartCycleRequest request = new StartCycleRequest();
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    @Operation(summary = "查詢團隊考核", operationId = "getTeamReviews")
    @GetMapping("/team")
    public ResponseEntity<GetReviewsResponse> getTeamReviews(@CurrentUser JWTModel currentUser) throws Exception {
        StartCycleRequest request = new StartCycleRequest();
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    @Operation(summary = "查詢考核詳情", operationId = "getReviewDetail")
    @GetMapping("/{id}")
    public ResponseEntity<GetReviewsResponse.ReviewSummary> getReviewDetail(@PathVariable String id,
            @CurrentUser JWTModel currentUser) throws Exception {
        StartCycleRequest request = StartCycleRequest.builder().cycleId(id).build();
        return ResponseEntity.ok(getResponse(request, currentUser));
    }
}
