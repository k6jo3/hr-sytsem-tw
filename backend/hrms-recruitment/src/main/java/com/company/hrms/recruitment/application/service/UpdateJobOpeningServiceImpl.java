package com.company.hrms.recruitment.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.recruitment.application.context.UpdateJobOpeningContext;
import com.company.hrms.recruitment.application.dto.job.UpdateJobOpeningRequest;
import com.company.hrms.recruitment.application.dto.job.UpdateJobOpeningResponse;
import com.company.hrms.recruitment.application.task.job.LoadJobOpeningTask;
import com.company.hrms.recruitment.application.task.job.PublishJobOpeningEventTask;
import com.company.hrms.recruitment.application.task.job.SaveUpdatedJobOpeningTask;
import com.company.hrms.recruitment.application.task.job.UpdateJobOpeningTask;

@Service("updateJobOpeningServiceImpl")
@Transactional
public class UpdateJobOpeningServiceImpl
        implements CommandApiService<UpdateJobOpeningRequest, UpdateJobOpeningResponse> {

    private final LoadJobOpeningTask loadTask;
    private final UpdateJobOpeningTask updateTask;
    private final SaveUpdatedJobOpeningTask saveTask;
    private final PublishJobOpeningEventTask eventTask;

    public UpdateJobOpeningServiceImpl(
            LoadJobOpeningTask loadTask,
            UpdateJobOpeningTask updateTask,
            SaveUpdatedJobOpeningTask saveTask,
            PublishJobOpeningEventTask eventTask) {
        this.loadTask = loadTask;
        this.updateTask = updateTask;
        this.saveTask = saveTask;
        this.eventTask = eventTask;
    }

    @Override
    public UpdateJobOpeningResponse execCommand(UpdateJobOpeningRequest request, JWTModel currentUser, String... args)
            throws Exception {
        if (args.length == 0) {
            throw new IllegalArgumentException("Job Opening ID is required");
        }
        String openingId = args[0];

        UpdateJobOpeningContext ctx = new UpdateJobOpeningContext();
        ctx.setOpeningId(openingId);
        ctx.setRequest(request);
        ctx.setCurrentUser(currentUser);

        BusinessPipeline.start(ctx)
                .next(loadTask)
                .next(updateTask)
                .next(saveTask)
                .next(eventTask)
                .execute();

        return UpdateJobOpeningResponse.builder()
                .openingId(ctx.getJobOpening().getId().toString())
                .status(ctx.getJobOpening().getStatus().name())
                .updatedAt(ctx.getJobOpening().getUpdatedAt())
                .build();
    }
}
