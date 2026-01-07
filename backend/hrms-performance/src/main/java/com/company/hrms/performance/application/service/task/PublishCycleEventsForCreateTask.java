package com.company.hrms.performance.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.performance.application.service.context.CreateCycleContext;

import lombok.RequiredArgsConstructor;

/**
 * 發布週期事件 Task (Infrastructure) - for CreateCycle
 */
@Component("publishCycleEventsForCreateTask")
@RequiredArgsConstructor
public class PublishCycleEventsForCreateTask implements PipelineTask<CreateCycleContext> {

    private final EventPublisher eventPublisher;

    @Override
    public void execute(CreateCycleContext context) throws Exception {
        // 發布 Aggregate 內的 Domain Events
        eventPublisher.publishAll(context.getCycle().getDomainEvents());
        context.getCycle().clearDomainEvents();
    }

    @Override
    public String getName() {
        return "發布週期事件(Create)";
    }
}
