package com.company.hrms.recruitment.application.task.job;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.recruitment.application.context.CloseJobOpeningContext;
import com.company.hrms.recruitment.application.context.CreateJobOpeningContext;
import com.company.hrms.recruitment.application.context.UpdateJobOpeningContext;
import com.company.hrms.recruitment.domain.model.aggregate.JobOpening;
import com.company.hrms.recruitment.domain.model.valueobject.JobStatus;

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

        // 只有草稿狀態才執行發布（狀態轉 OPEN）
        if (job != null && job.getStatus() == JobStatus.DRAFT) {
            job.publish();
        }
    }
}
