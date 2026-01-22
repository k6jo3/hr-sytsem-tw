package com.company.hrms.recruitment.application.task.job;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.recruitment.application.context.CreateJobOpeningContext;
import com.company.hrms.recruitment.application.dto.job.CreateJobOpeningRequest;

@Component
public class ValidateJobOpeningTask implements PipelineTask<CreateJobOpeningContext> {

    @Override
    public void execute(CreateJobOpeningContext context) throws Exception {
        CreateJobOpeningRequest request = context.getRequest();

        if (!StringUtils.hasText(request.getJobTitle())) {
            throw new IllegalArgumentException("Job Title is required");
        }

        // TODO: Validate Department ID via Organization Service Feign Client
        // String deptId = request.getDepartmentId();
        // orgService.getDepartment(deptId);
    }
}
