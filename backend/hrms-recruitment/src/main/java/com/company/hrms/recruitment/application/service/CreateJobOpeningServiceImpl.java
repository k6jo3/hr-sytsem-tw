package com.company.hrms.recruitment.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.recruitment.application.context.CreateJobOpeningContext;
import com.company.hrms.recruitment.application.dto.job.CreateJobOpeningRequest;
import com.company.hrms.recruitment.application.dto.job.CreateJobOpeningResponse;
import com.company.hrms.recruitment.application.task.job.PublishJobOpeningEventTask;
import com.company.hrms.recruitment.application.task.job.SaveJobOpeningTask;
import com.company.hrms.recruitment.application.task.job.ValidateJobOpeningTask;
import com.company.hrms.recruitment.domain.model.aggregate.JobOpening;

@Service("createJobOpeningServiceImpl")
public class CreateJobOpeningServiceImpl
                implements CommandApiService<CreateJobOpeningRequest, CreateJobOpeningResponse> {

        @Autowired
        private ValidateJobOpeningTask validateTask;

        @Autowired
        private SaveJobOpeningTask saveTask;

        @Autowired
        private PublishJobOpeningEventTask eventTask;

        @Override
        @Transactional
        public CreateJobOpeningResponse execCommand(CreateJobOpeningRequest request, JWTModel currentUser,
                        String... args)
                        throws Exception {
                CreateJobOpeningContext ctx = new CreateJobOpeningContext();
                ctx.setRequest(request);
                ctx.setCurrentUser(currentUser);

                BusinessPipeline.start(ctx)
                                .next(validateTask)
                                .next(saveTask)
                                .next(eventTask)
                                .execute();

                JobOpening job = ctx.getJobOpening();

                return CreateJobOpeningResponse.builder()
                                .openingId(job.getId().getValue().toString())
                                .jobTitle(job.getJobTitle())
                                .departmentId(job.getDepartmentId().toString())
                                // .departmentName(ctx.getDepartmentName()) // Mapped if available
                                .status(job.getStatus().name())
                                .createdAt(job.getCreatedAt())
                                .build();
        }
}
