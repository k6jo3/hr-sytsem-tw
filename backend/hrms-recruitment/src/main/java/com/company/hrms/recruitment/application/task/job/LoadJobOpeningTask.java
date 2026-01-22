package com.company.hrms.recruitment.application.task.job;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.recruitment.application.context.CloseJobOpeningContext;
import com.company.hrms.recruitment.application.context.UpdateJobOpeningContext;
import com.company.hrms.recruitment.domain.model.aggregate.JobOpening;
import com.company.hrms.recruitment.domain.model.valueobject.OpeningId;
import com.company.hrms.recruitment.domain.repository.IJobOpeningRepository;

@Component
public class LoadJobOpeningTask implements PipelineTask<PipelineContext> {

    private final IJobOpeningRepository repository;

    public LoadJobOpeningTask(IJobOpeningRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(PipelineContext context) throws Exception {
        String idStr;
        if (context instanceof UpdateJobOpeningContext c) {
            idStr = c.getOpeningId();
        } else if (context instanceof CloseJobOpeningContext c) {
            idStr = c.getOpeningId();
        } else {
            throw new IllegalArgumentException("Unsupported Context for LoadJobOpeningTask");
        }

        OpeningId id = OpeningId.of(UUID.fromString(idStr));
        JobOpening job = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Job Opening not found: " + idStr));

        if (context instanceof UpdateJobOpeningContext c) {
            c.setJobOpening(job);
        } else if (context instanceof CloseJobOpeningContext c) {
            c.setJobOpening(job);
        }
    }
}
