package com.company.hrms.training.api.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.training.api.request.TrainingStatisticsQuery;
import com.company.hrms.training.api.response.ExportResponse;
import com.company.hrms.training.api.response.TrainingStatisticsResponse;
import com.company.hrms.training.application.service.ExportTrainingStatisticsServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 訓練統計報表 Controller
 * 符合 API 規格: /api/v1/training/statistics 系列端點
 */
@RestController
@RequestMapping("/api/v1/training/statistics")
@Tag(name = "HR10 - Statistics", description = "訓練統計 (Query)")
@RequiredArgsConstructor

public class HR10StatisticsQryController extends QueryBaseController {

    private final ExportTrainingStatisticsServiceImpl exportService;

    /**
     * 查詢訓練統計
     * 端點: GET /api/v1/training/statistics
     */
    @GetMapping
    @Operation(summary = "查詢訓練統計", operationId = "getTrainingStatistics")
    public ResponseEntity<TrainingStatisticsResponse> getTrainingStatistics(
            @RequestAttribute("currentUser") JWTModel currentUser,
            @Parameter(description = "統計條件") TrainingStatisticsQuery query) throws Exception {
        if (query == null) {
            query = new TrainingStatisticsQuery();
        }
        return ResponseEntity.ok(getResponse(query, currentUser));
    }

    /**
     * 匯出統計報表
     * 端點: GET /api/v1/training/statistics/export
     */
    @GetMapping("/export")
    @Operation(summary = "匯出統計報表", operationId = "exportTrainingStatistics")
    public ResponseEntity<byte[]> exportTrainingStatistics(
            @RequestAttribute("currentUser") JWTModel currentUser,
            @Parameter(description = "匯出條件") TrainingStatisticsQuery query) throws Exception {
        if (query == null) {
            query = new TrainingStatisticsQuery();
        }

        ExportResponse export = exportService.getResponse(query, currentUser);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(export.getContentType()));
        headers.setContentDispositionFormData("attachment", export.getFileName());
        headers.setContentLength(export.getData().length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(export.getData());
    }
}
