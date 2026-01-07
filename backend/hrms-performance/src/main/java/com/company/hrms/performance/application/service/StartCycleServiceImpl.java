package com.company.hrms.performance.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.performance.api.request.StartCycleRequest;
import com.company.hrms.performance.api.response.SuccessResponse;
import com.company.hrms.performance.domain.event.PerformanceCycleStartedEvent;
import com.company.hrms.performance.domain.model.aggregate.PerformanceCycle;
import com.company.hrms.performance.domain.model.valueobject.CycleId;
import com.company.hrms.performance.domain.repository.IPerformanceCycleRepository;

import lombok.RequiredArgsConstructor;

/**
 * 啟動考核週期 Service
 */
@Service("startCycleServiceImpl")
@RequiredArgsConstructor
@Transactional
public class StartCycleServiceImpl implements CommandApiService<StartCycleRequest, SuccessResponse> {

    private final IPerformanceCycleRepository cycleRepository;
    private final EventPublisher eventPublisher;

    @Override
    public SuccessResponse execCommand(StartCycleRequest req, JWTModel currentUser, String... args)
            throws Exception {

        // 查詢週期
        PerformanceCycle cycle = cycleRepository.findById(CycleId.of(req.getCycleId()))
                .orElseThrow(() -> new IllegalArgumentException("考核週期不存在"));

        // 啟動週期
        cycle.start();
        cycleRepository.save(cycle);

        // 發布領域事件 (使用 Event factory method)
        PerformanceCycleStartedEvent event = PerformanceCycleStartedEvent.create(
                cycle.getCycleId(),
                cycle.getCycleName(),
                cycle.getCycleType().toString(),
                cycle.getSelfEvalDeadline().toString(),
                cycle.getManagerEvalDeadline().toString());

        eventPublisher.publishAll(java.util.Collections.singletonList(event));

        // 發布 Aggregate 內部的 Domain Events
        eventPublisher.publishAll(cycle.getDomainEvents());
        cycle.clearDomainEvents();

        return SuccessResponse.of("考核週期已啟動");
    }
}
