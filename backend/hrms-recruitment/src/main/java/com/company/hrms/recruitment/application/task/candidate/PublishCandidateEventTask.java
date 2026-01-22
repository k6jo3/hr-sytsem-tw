package com.company.hrms.recruitment.application.task.candidate;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.recruitment.application.context.CreateCandidateContext;
import com.company.hrms.recruitment.domain.model.aggregate.Candidate;

@Component
public class PublishCandidateEventTask implements PipelineTask<CreateCandidateContext> {

    // TODO: Inject EventPublisher when available

    @Override
    public void execute(CreateCandidateContext context) {
        Candidate candidate = context.getCandidate();
        if (candidate != null) {
            // TODO: publish events
            // eventPublisher.publish(candidate.getDomainEvents());
            // candidate.clearDomainEvents();
        }
    }
}
