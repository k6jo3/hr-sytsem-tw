package com.company.hrms.training.api.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import com.company.hrms.common.annotation.CurrentUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.training.api.request.GetMyTrainingsRequest;
import com.company.hrms.training.api.response.MyTrainingHoursResponse;
import com.company.hrms.training.api.response.TrainingEnrollmentResponse;
import com.company.hrms.training.application.service.GetMyTrainingHoursServiceImpl;
import com.company.hrms.training.application.service.GetMyTrainingsServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 我的訓練 Controller
 * 符合 API 規格: /api/v1/training/my 系列端點
 */
@RestController
@RequestMapping("/api/v1/training/my")
@Tag(name = "HR10 - My Training", description = "我的訓練 (Query)")
@RequiredArgsConstructor
public class HR10MyTrainingQryController extends QueryBaseController {

    private final GetMyTrainingsServiceImpl getMyTrainingsServiceImpl;
    private final GetMyTrainingHoursServiceImpl getMyTrainingHoursServiceImpl;

    /**
     * 查詢我的訓練 (報名紀錄)
     * 端點: GET /api/v1/training/my
     */
    @GetMapping
    @Operation(summary = "查詢我的訓練", operationId = "getMyTrainings")
    public ResponseEntity<Page<TrainingEnrollmentResponse>> getMyTrainings(
            @CurrentUser JWTModel currentUser,
            @Parameter(description = "查詢條件") GetMyTrainingsRequest request) throws Exception {
        if (request == null) {
            request = new GetMyTrainingsRequest();
        }
        return ResponseEntity.ok(getMyTrainingsServiceImpl.getResponse(request, currentUser));
    }

    /**
     * 查詢我的訓練時數
     * 端點: GET /api/v1/training/my/hours
     */
    @GetMapping("/hours")
    @Operation(summary = "查詢我的訓練時數", operationId = "getMyTrainingHours")
    public ResponseEntity<MyTrainingHoursResponse> getMyTrainingHours(
            @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getMyTrainingHoursServiceImpl.getResponse(new QueryGroup(), currentUser));
    }
}
