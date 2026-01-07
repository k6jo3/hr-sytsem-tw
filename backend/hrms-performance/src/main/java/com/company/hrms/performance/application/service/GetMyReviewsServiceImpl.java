package com.company.hrms.performance.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.api.response.PageResponse;
import com.company.hrms.common.application.service.AbstractQueryService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.performance.api.request.GetMyReviewsRequest;
import com.company.hrms.performance.api.response.GetReviewsResponse;
import com.company.hrms.performance.application.factory.ReviewDtoFactory;
import com.company.hrms.performance.domain.model.aggregate.PerformanceReview;
import com.company.hrms.performance.domain.repository.IPerformanceReviewRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢我的考核列表 Service
 */
@Service("getMyReviewsServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetMyReviewsServiceImpl
        extends AbstractQueryService<GetMyReviewsRequest, PageResponse<GetReviewsResponse.ReviewSummary>> {

    private final IPerformanceReviewRepository repository;

    @Override
    protected QueryGroup buildQuery(GetMyReviewsRequest request, JWTModel currentUser) {
        // 設定當前用戶ID到 request (權限控制)
        request.setEmployeeId(currentUser.getUserId().toString());

        // 純宣告式查詢
        return QueryBuilder.where()
                .fromDto(request)
                .build();
    }

    @Override
    protected PageResponse<GetReviewsResponse.ReviewSummary> executeQuery(
            QueryGroup query, GetMyReviewsRequest request, JWTModel currentUser, String... args) throws Exception {

        int pageIdx = request.getPage() > 0 ? request.getPage() - 1 : 0;
        PageRequest pageable = PageRequest.of(pageIdx, request.getSize());

        Page<PerformanceReview> page = repository.findAll(query, pageable);

        List<GetReviewsResponse.ReviewSummary> items = page.getContent().stream()
                .map(ReviewDtoFactory::toSummary)
                .collect(Collectors.toList());

        return PageResponse.of(items, request.getPage(), request.getSize(), page.getTotalElements());
    }
}
