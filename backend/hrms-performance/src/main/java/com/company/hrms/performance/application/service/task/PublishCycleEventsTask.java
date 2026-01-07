package com.company.hrms.performance.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.performance.application.service.context.StartCycleContext;

import lombok.RequiredArgsConstructor;

/**
 * 發布週期事件 Task (Infrastructure)
 */
@Component
@RequiredArgsConstructor
public class PublishCycleEventsTask implements PipelineTask<StartCycleContext> {

    private final EventPublisher eventPublisher;

    @Override
    public void execute(StartCycleContext context) throws Exception {
        // 發布 Aggregate 內的 Domain Events
        eventPublisher.publishAll(context.getCycle().getDomainEvents());
        context.getCycle().clearDomainEvents();
    }

    @Override
    public String getName() {
        return "發布週期事件";
    }
}
