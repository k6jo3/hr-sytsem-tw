package com.company.hrms.performance.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.performance.api.request.GetCycleDetailRequest;
import com.company.hrms.performance.api.response.GetCyclesResponse;
import com.company.hrms.performance.domain.model.aggregate.PerformanceCycle;
import com.company.hrms.performance.domain.model.valueobject.CycleId;
import com.company.hrms.performance.domain.repository.IPerformanceCycleRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢考核週期詳情 Service
 */
@Service("getCycleDetailServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetCycleDetailServiceImpl
                implements QueryApiService<GetCycleDetailRequest, GetCyclesResponse.CycleSummary> {

        private final IPerformanceCycleRepository cycleRepository;

        @Override
        public GetCyclesResponse.CycleSummary getResponse(GetCycleDetailRequest req, JWTModel currentUser,
                        String... args)
                        throws Exception {

                PerformanceCycle cycle = cycleRepository.findById(CycleId.of(req.getCycleId()))
                                .orElseThrow(() -> new IllegalArgumentException("考核週期不存在"));

                return GetCyclesResponse.CycleSummary.builder()
                                .cycleId(cycle.getCycleId().getValue().toString())
                                .cycleName(cycle.getCycleName())
                                .cycleType(cycle.getCycleType())
                                .status(cycle.getStatus())
                                .startDate(cycle.getStartDate())
                                .endDate(cycle.getEndDate())
                                .hasTemplate(cycle.getTemplate() != null)
                                .build();
        }
}
