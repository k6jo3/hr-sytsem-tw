package com.company.hrms.performance.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.performance.api.request.StartCycleRequest;
import com.company.hrms.performance.api.response.SuccessResponse;
import com.company.hrms.performance.domain.repository.IPerformanceReviewRepository;

import lombok.RequiredArgsConstructor;

/**
 * 匯出績效報表 Service
 */
@Service("exportReportServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExportReportServiceImpl implements QueryApiService<StartCycleRequest, SuccessResponse> {

    private final IPerformanceReviewRepository reviewRepository;

    @Override
    public SuccessResponse getResponse(StartCycleRequest req, JWTModel currentUser, String... args) throws Exception {

        // TODO: 根據 cycleId 匯出報表為 CSV/Excel

        return SuccessResponse.of("報表匯出成功");
    }
}
