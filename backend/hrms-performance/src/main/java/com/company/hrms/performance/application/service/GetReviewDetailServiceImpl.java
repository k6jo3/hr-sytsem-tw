package com.company.hrms.performance.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.service.AbstractQueryService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.performance.api.request.GetReviewDetailRequest;
import com.company.hrms.performance.api.response.GetReviewsResponse;
import com.company.hrms.performance.application.factory.ReviewDtoFactory;
import com.company.hrms.performance.domain.model.aggregate.PerformanceReview;
import com.company.hrms.performance.domain.model.valueobject.ReviewId;
import com.company.hrms.performance.domain.repository.IPerformanceReviewRepository;

import lombok.RequiredArgsConstructor;

// ...

@Service("getReviewDetailServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetReviewDetailServiceImpl
                extends AbstractQueryService<GetReviewDetailRequest, GetReviewsResponse.ReviewSummary> {

        private final IPerformanceReviewRepository reviewRepository;

        @Override
        protected QueryGroup buildQuery(GetReviewDetailRequest request, JWTModel currentUser) {
                // 單筆查詢不需要 QueryGroup，直接在 executeQuery 用 findById
                return null;
        }

        @Override
        protected GetReviewsResponse.ReviewSummary executeQuery(
                        QueryGroup query, GetReviewDetailRequest request, JWTModel currentUser, String... args)
                        throws Exception {

                // 直接使用 Repository findById
                PerformanceReview review = reviewRepository.findById(ReviewId.of(request.getReviewId()))
                                .orElseThrow(() -> new IllegalArgumentException("考核記錄不存在"));

                return ReviewDtoFactory.toSummary(review);
        }
}
