package com.company.hrms.recruitment.application.task.job;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.recruitment.application.context.CloseJobOpeningContext;
import com.company.hrms.recruitment.application.dto.job.CloseJobOpeningRequest;
import com.company.hrms.recruitment.domain.model.aggregate.JobOpening;

@Component
public class CloseJobOpeningTask implements PipelineTask<CloseJobOpeningContext> {

    @Override
    public void execute(CloseJobOpeningContext context) throws Exception {
        JobOpening job = context.getJobOpening();
        CloseJobOpeningRequest req = context.getRequest();

        if (!StringUtils.hasText(req.getReason())) {
            throw new IllegalArgumentException("Close reason is required");
        }

        job.close(req.getReason());
    }
}
