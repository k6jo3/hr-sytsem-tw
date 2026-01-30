package com.company.hrms.recruitment.application.task.job;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.recruitment.application.context.CreateJobOpeningContext;
import com.company.hrms.recruitment.application.dto.job.CreateJobOpeningRequest;

@Component
public class ValidateJobOpeningTask implements PipelineTask<CreateJobOpeningContext> {

    @org.springframework.beans.factory.annotation.Autowired
    private com.company.hrms.recruitment.infrastructure.client.organization.OrganizationServiceClient organizationServiceClient;

    @Override
    public void execute(CreateJobOpeningContext context) throws Exception {
        CreateJobOpeningRequest request = context.getRequest();

        if (!StringUtils.hasText(request.getJobTitle())) {
            throw new IllegalArgumentException("Job Title is required");
        }

        // Validate Department ID via Organization Service
        String deptId = request.getDepartmentId();
        if (StringUtils.hasText(deptId)) {
            try {
                organizationServiceClient.getDepartment(deptId);
            } catch (Exception e) {
                // Assuming Feign throws exception on 404 or connection error
                throw new IllegalArgumentException("Invalid Department ID: " + deptId);
            }
        }
    }
}
