package com.company.hrms.performance.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.performance.api.request.StartCycleRequest;
import com.company.hrms.performance.api.response.SuccessResponse;
import com.company.hrms.performance.domain.model.aggregate.PerformanceCycle;
import com.company.hrms.performance.domain.model.valueobject.CycleId;
import com.company.hrms.performance.domain.repository.IPerformanceCycleRepository;

import lombok.RequiredArgsConstructor;

/**
 * 完成考核週期 Service
 */
@Service("completeCycleServiceImpl")
@RequiredArgsConstructor
@Transactional
public class CompleteCycleServiceImpl implements CommandApiService<StartCycleRequest, SuccessResponse> {

    private final IPerformanceCycleRepository cycleRepository;
    private final EventPublisher eventPublisher;

    @Override
    public SuccessResponse execCommand(StartCycleRequest req, JWTModel currentUser, String... args)
            throws Exception {

        PerformanceCycle cycle = cycleRepository.findById(CycleId.of(req.getCycleId()))
                .orElseThrow(() -> new IllegalArgumentException("考核週期不存在"));

        cycle.complete();
        cycleRepository.save(cycle);

        eventPublisher.publishAll(cycle.getDomainEvents());
        cycle.clearDomainEvents();

        return SuccessResponse.of("考核週期已完成");
    }
}
