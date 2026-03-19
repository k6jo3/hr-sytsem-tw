package com.company.hrms.recruitment.application.task.candidate;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.recruitment.application.context.CreateCandidateContext;
import com.company.hrms.recruitment.domain.model.aggregate.Candidate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 發布候選人領域事件任務
 *
 * <p>在候選人建立/更新流程的最後一步，
 * 將聚合根中累積的領域事件（如 CandidateHiredEvent）發布至事件匯流排。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PublishCandidateEventTask implements PipelineTask<CreateCandidateContext> {

    private final EventPublisher eventPublisher;

    @Override
    public void execute(CreateCandidateContext context) {
        Candidate candidate = context.getCandidate();
        if (candidate != null && !candidate.getDomainEvents().isEmpty()) {
            log.info("[PublishCandidateEventTask] 發布候選人領域事件，事件數量: {}", candidate.getDomainEvents().size());
            eventPublisher.publishAll(candidate.getDomainEvents());
            candidate.clearDomainEvents();
        }
    }
}
