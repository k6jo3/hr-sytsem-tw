package com.company.hrms.performance.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.performance.api.request.CreateCycleRequest;
import com.company.hrms.performance.api.response.CreateCycleResponse;
import com.company.hrms.performance.domain.model.aggregate.PerformanceCycle;
import com.company.hrms.performance.domain.repository.IPerformanceCycleRepository;

import lombok.RequiredArgsConstructor;

/**
 * 建立考核週期 Service
 */
@Service("createCycleServiceImpl")
@RequiredArgsConstructor
@Transactional
public class CreateCycleServiceImpl implements CommandApiService<CreateCycleRequest, CreateCycleResponse> {

    private final IPerformanceCycleRepository cycleRepository;
    private final EventPublisher eventPublisher;

    @Override
    public CreateCycleResponse execCommand(CreateCycleRequest req, JWTModel currentUser, String... args)
            throws Exception {

        // 建立考核週期聚合根
        PerformanceCycle cycle = PerformanceCycle.create(
                req.getCycleName(),
                req.getCycleType(),
                req.getStartDate(),
                req.getEndDate(),
                req.getSelfEvalDeadline(),
                req.getManagerEvalDeadline());

        // 儲存
        cycleRepository.save(cycle);

        // 發布領域事件
        eventPublisher.publishAll(cycle.getDomainEvents());
        cycle.clearDomainEvents();

        return new CreateCycleResponse(cycle.getCycleId().getValue().toString());
    }
}
