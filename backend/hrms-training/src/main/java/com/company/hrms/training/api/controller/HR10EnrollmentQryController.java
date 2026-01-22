package com.company.hrms.training.api.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.training.api.request.GetEnrollmentsRequest;
import com.company.hrms.training.api.request.GetMyTrainingsRequest;
import com.company.hrms.training.api.response.TrainingEnrollmentResponse;
import com.company.hrms.training.application.service.GetMyTrainingsServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 報名管理查詢 Controller
 * 使用 Request DTO 進行宣告式查詢
 */
@RestController
@RequestMapping("/api/v1/training/enrollments")
@Tag(name = "HR10 - Enrollment Management", description = "報名管理 (Query)")
@RequiredArgsConstructor
public class HR10EnrollmentQryController extends QueryBaseController {

    private final GetMyTrainingsServiceImpl getMyTrainingsServiceImpl;

    @GetMapping
    @Operation(summary = "查詢報名列表", operationId = "getEnrollments")
    public ResponseEntity<Page<TrainingEnrollmentResponse>> getEnrollments(
            @RequestAttribute("currentUser") JWTModel currentUser,
            GetEnrollmentsRequest request) throws Exception {
        if (request == null) {
            request = new GetEnrollmentsRequest();
        }
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    @GetMapping("/me")
    @Operation(summary = "查詢我的訓練", operationId = "getMyTrainings")
    public ResponseEntity<Page<TrainingEnrollmentResponse>> getMyTrainings(
            @RequestAttribute("currentUser") JWTModel currentUser,
            @Parameter(description = "查詢條件") GetMyTrainingsRequest request) throws Exception {
        if (request == null) {
            request = new GetMyTrainingsRequest();
        }
        return ResponseEntity.ok(getMyTrainingsServiceImpl.getResponse(request, currentUser));
    }
}
