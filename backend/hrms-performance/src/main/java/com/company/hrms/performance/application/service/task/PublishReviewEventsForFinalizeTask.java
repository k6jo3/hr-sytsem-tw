package com.company.hrms.performance.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.performance.application.service.context.FinalizeReviewContext;

import lombok.RequiredArgsConstructor;

/**
 * 發布考核事件 Task (Infrastructure) - for FinalizeReview
 */
@Component("publishReviewEventsForFinalizeTask")
@RequiredArgsConstructor
public class PublishReviewEventsForFinalizeTask implements PipelineTask<FinalizeReviewContext> {

    private final EventPublisher eventPublisher;

    @Override
    public void execute(FinalizeReviewContext context) throws Exception {
        // 發布 Aggregate 內的 Domain Events
        eventPublisher.publishAll(context.getReview().getDomainEvents());
        context.getReview().clearDomainEvents();
    }

    @Override
    public String getName() {
        return "發布考核事件(Finalize)";
    }
}
