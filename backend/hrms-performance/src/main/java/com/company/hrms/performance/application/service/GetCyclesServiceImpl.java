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
import com.company.hrms.performance.api.request.GetCyclesRequest;
import com.company.hrms.performance.api.response.GetCyclesResponse;
import com.company.hrms.performance.application.factory.CycleDtoFactory;
import com.company.hrms.performance.domain.model.aggregate.PerformanceCycle;
import com.company.hrms.performance.domain.repository.IPerformanceCycleRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢考核週期列表 Service
 * 
 * <p>
 * 使用 Fluent-Query-Engine 宣告式查詢：
 * <ul>
 * <li>Request DTO 上使用 @QueryFilter 註解宣告篩選條件</li>
 * <li>Service 使用 QueryBuilder.fromDto() 自動解析</li>
 * <li>無需手動 if 判斷，實現「能用宣告的就不用程式碼」原則</li>
 * </ul>
 */
@Service("getCyclesServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetCyclesServiceImpl
                extends AbstractQueryService<GetCyclesRequest, PageResponse<GetCyclesResponse.CycleSummary>> {

        private final IPerformanceCycleRepository repository;

        @Override
        protected QueryGroup buildQuery(GetCyclesRequest request, JWTModel currentUser) {
                // 純宣告式查詢：自動解析 Request 上的 @QueryFilter 註解
                return QueryBuilder.where()
                                .fromDto(request) // 自動處理 status, cycleType 等所有 @QueryFilter 欄位
                                .build();
        }

        @Override
        protected PageResponse<GetCyclesResponse.CycleSummary> executeQuery(
                        QueryGroup query, GetCyclesRequest request, JWTModel currentUser, String... args)
                        throws Exception {

                int pageIdx = request.getPage() > 0 ? request.getPage() - 1 : 0;
                PageRequest pageable = PageRequest.of(pageIdx, request.getSize());

                Page<PerformanceCycle> page = repository.findAll(query, pageable);

                List<GetCyclesResponse.CycleSummary> items = page.getContent().stream()
                                .map(CycleDtoFactory::toSummary)
                                .collect(Collectors.toList());

                return PageResponse.of(items, request.getPage(), request.getSize(), page.getTotalElements());
        }
}
