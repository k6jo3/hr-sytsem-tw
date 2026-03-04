package com.company.hrms.performance.application.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.api.response.PageResponse;
import com.company.hrms.common.application.service.AbstractQueryService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.performance.api.request.GetMyReviewsRequest;
import com.company.hrms.performance.api.response.GetReviewsResponse;
import com.company.hrms.performance.application.factory.ReviewDtoFactory;
import com.company.hrms.performance.domain.model.aggregate.PerformanceReview;
import com.company.hrms.performance.domain.model.valueobject.CycleId;
import com.company.hrms.performance.domain.repository.IPerformanceCycleRepository;
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
    private final IPerformanceCycleRepository cycleRepository;

    @Override
    protected QueryGroup buildQuery(GetMyReviewsRequest request, JWTModel currentUser) {
        // 手動建立查詢（Entity 欄位為 UUID，不能用 fromDto 傳 String）
        QueryBuilder builder = QueryBuilder.where()
                .and("employeeId", Operator.EQ, UUID.fromString(currentUser.getUserId()));

        if (request.getCycleId() != null && !request.getCycleId().isBlank()) {
            builder.and("cycleId", Operator.EQ, UUID.fromString(request.getCycleId()));
        }
        if (request.getStatus() != null) {
            builder.and("status", Operator.EQ, request.getStatus());
        }

        return builder.build();
    }

    @Override
    protected PageResponse<GetReviewsResponse.ReviewSummary> executeQuery(
            QueryGroup query, GetMyReviewsRequest request, JWTModel currentUser, String... args) throws Exception {

        int pageIdx = request.getPage() > 0 ? request.getPage() - 1 : 0;
        PageRequest pageable = PageRequest.of(pageIdx, request.getSize());

        Page<PerformanceReview> page = repository.findAll(query, pageable);
        List<PerformanceReview> reviews = page.getContent();

        // 批量查詢週期名稱
        Map<UUID, String> cycleNameMap = buildCycleNameMap(reviews);

        List<GetReviewsResponse.ReviewSummary> items = reviews.stream()
                .map(r -> ReviewDtoFactory.toSummary(r, cycleNameMap))
                .collect(Collectors.toList());

        return PageResponse.of(items, request.getPage(), request.getSize(), page.getTotalElements());
    }

    private Map<UUID, String> buildCycleNameMap(List<PerformanceReview> reviews) {
        Set<UUID> cycleIds = reviews.stream()
                .map(r -> r.getCycleId().getValue())
                .collect(Collectors.toSet());

        Map<UUID, String> map = new HashMap<>();
        for (UUID cid : cycleIds) {
            cycleRepository.findById(CycleId.of(cid))
                    .ifPresent(c -> map.put(cid, c.getCycleName()));
        }
        return map;
    }
}
