package com.company.hrms.recruitment.application.task.job;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.recruitment.application.context.CloseJobOpeningContext;
import com.company.hrms.recruitment.application.context.UpdateJobOpeningContext;
import com.company.hrms.recruitment.domain.model.aggregate.JobOpening;
import com.company.hrms.recruitment.domain.repository.IJobOpeningRepository;

@Component
public class SaveUpdatedJobOpeningTask implements PipelineTask<PipelineContext> {

    private final IJobOpeningRepository repository;

    public SaveUpdatedJobOpeningTask(IJobOpeningRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(PipelineContext context) throws Exception {
        JobOpening job = null;
        if (context instanceof UpdateJobOpeningContext c) {
            job = c.getJobOpening();
        } else if (context instanceof CloseJobOpeningContext c) {
            job = c.getJobOpening();
        }

        if (job != null) {
            repository.update(job);
        }
    }
}
