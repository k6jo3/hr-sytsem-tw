package com.company.hrms.training.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.training.api.request.TrainingStatisticsQuery;
import com.company.hrms.training.api.response.MyTrainingHoursResponse;
import com.company.hrms.training.api.response.TrainingStatisticsResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/training/reports")
@Tag(name = "HR10 - Reporting", description = "訓練報表 (Query)")
@RequiredArgsConstructor
public class HR10ReportQryController extends QueryBaseController {

    @GetMapping("/my-hours")
    @Operation(summary = "查詢我的訓練時數", operationId = "getMyTrainingHours")
    public ResponseEntity<MyTrainingHoursResponse> getMyTrainingHours(
            @RequestAttribute("currentUser") JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(new QueryGroup(), currentUser));
    }

    @GetMapping("/statistics")
    @Operation(summary = "查詢訓練統計", operationId = "getTrainingStatistics")
    public ResponseEntity<TrainingStatisticsResponse> getTrainingStatistics(
            @RequestAttribute("currentUser") JWTModel currentUser,
            @Parameter(description = "統計條件") TrainingStatisticsQuery query) throws Exception {
        if (query == null)
            query = new TrainingStatisticsQuery();
        return ResponseEntity.ok(getResponse(query, currentUser));
    }
}
