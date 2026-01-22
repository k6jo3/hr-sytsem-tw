package com.company.hrms.recruitment.application.task.job;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.recruitment.application.context.CloseJobOpeningContext;
import com.company.hrms.recruitment.application.context.CreateJobOpeningContext;
import com.company.hrms.recruitment.application.context.UpdateJobOpeningContext;
import com.company.hrms.recruitment.domain.model.aggregate.JobOpening;

@Component
public class PublishJobOpeningEventTask implements PipelineTask<PipelineContext> {

    @Override
    public void execute(PipelineContext context) throws Exception {
        JobOpening job = null;
        if (context instanceof CreateJobOpeningContext c) {
            job = c.getJobOpening();
        } else if (context instanceof UpdateJobOpeningContext c) {
            job = c.getJobOpening();
        } else if (context instanceof CloseJobOpeningContext c) {
            job = c.getJobOpening();
        }

        if (job != null) {
            job.publish(); // This registers domain event in the Aggregate Root
        }
    }
}
