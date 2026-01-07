package com.company.hrms.performance.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.performance.api.request.StartCycleRequest;
import com.company.hrms.performance.api.response.GetCyclesResponse;
import com.company.hrms.performance.domain.model.aggregate.PerformanceCycle;
import com.company.hrms.performance.domain.repository.IPerformanceCycleRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢考核週期列表 Service
 */
@Service("getCyclesServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetCyclesServiceImpl implements QueryApiService<StartCycleRequest, GetCyclesResponse> {

    private final IPerformanceCycleRepository cycleRepository;

    @Override
    public GetCyclesResponse getResponse(StartCycleRequest req, JWTModel currentUser, String... args) throws Exception {

        // 查詢所有週期（可加入分頁和篩選條件）
        QueryGroup queryGroup = new QueryGroup();
        List<PerformanceCycle> cycles = cycleRepository
                .findAll(queryGroup, org.springframework.data.domain.PageRequest.of(0, 100)).getContent();

        // 轉換為 DTO
        List<GetCyclesResponse.CycleSummary> summaries = cycles.stream()
                .map(cycle -> GetCyclesResponse.CycleSummary.builder()
                        .cycleId(cycle.getCycleId().getValue().toString())
                        .cycleName(cycle.getCycleName())
                        .cycleType(cycle.getCycleType())
                        .status(cycle.getStatus())
                        .startDate(cycle.getStartDate())
                        .endDate(cycle.getEndDate())
                        .hasTemplate(cycle.getTemplate() != null)
                        .build())
                .collect(Collectors.toList());

        return GetCyclesResponse.builder()
                .cycles(summaries)
                .totalCount(summaries.size())
                .pageSize(summaries.size())
                .currentPage(1)
                .build();
    }
}
