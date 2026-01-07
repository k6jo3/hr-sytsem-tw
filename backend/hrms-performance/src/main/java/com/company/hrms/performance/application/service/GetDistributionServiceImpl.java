package com.company.hrms.performance.application.service;

import java.util.HashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.performance.api.request.StartCycleRequest;
import com.company.hrms.performance.api.response.GetDistributionResponse;
import com.company.hrms.performance.domain.repository.IPerformanceReviewRepository;
import com.company.hrms.performance.domain.service.DistributionValidator;

import lombok.RequiredArgsConstructor;

/**
 * 查詢績效分布 Service
 */
@Service("getDistributionServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDistributionServiceImpl implements QueryApiService<StartCycleRequest, GetDistributionResponse> {

    private final IPerformanceReviewRepository reviewRepository;
    private final DistributionValidator distributionValidator;

    @Override
    public GetDistributionResponse getResponse(StartCycleRequest req, JWTModel currentUser, String... args)
            throws Exception {

        // TODO: 根據 cycleId 查詢所有考核記錄並統計分布

        return GetDistributionResponse.builder()
                .distribution(new HashMap<>())
                .totalCount(0)
                .build();
    }
}
