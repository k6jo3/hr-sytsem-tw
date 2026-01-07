package com.company.hrms.performance.application.service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.performance.api.request.StartCycleRequest;
import com.company.hrms.performance.api.response.GetReviewsResponse;
import com.company.hrms.performance.domain.repository.IPerformanceReviewRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢團隊考核記錄 Service (管理者)
 */
@Service("getTeamReviewsServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetTeamReviewsServiceImpl implements QueryApiService<StartCycleRequest, GetReviewsResponse> {

    private final IPerformanceReviewRepository reviewRepository;

    @Override
    public GetReviewsResponse getResponse(StartCycleRequest req, JWTModel currentUser, String... args)
            throws Exception {

        // TODO: 根據 currentUser 查詢團隊的考核記錄

        return GetReviewsResponse.builder()
                .reviews(new ArrayList<>())
                .totalCount(0)
                .build();
    }
}
