package com.company.hrms.recruitment.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.recruitment.application.context.CloseJobOpeningContext;
import com.company.hrms.recruitment.application.dto.job.CloseJobOpeningRequest;
import com.company.hrms.recruitment.application.dto.job.CloseJobOpeningResponse;
import com.company.hrms.recruitment.application.task.job.CloseJobOpeningTask;
import com.company.hrms.recruitment.application.task.job.LoadJobOpeningTask;
import com.company.hrms.recruitment.application.task.job.PublishJobOpeningEventTask;
import com.company.hrms.recruitment.application.task.job.SaveUpdatedJobOpeningTask;

@Service("closeJobOpeningServiceImpl")
@Transactional
public class CloseJobOpeningServiceImpl implements CommandApiService<CloseJobOpeningRequest, CloseJobOpeningResponse> {

    private final LoadJobOpeningTask loadTask;
    private final CloseJobOpeningTask closeTask;
    private final SaveUpdatedJobOpeningTask saveTask;
    private final PublishJobOpeningEventTask eventTask;

    public CloseJobOpeningServiceImpl(
            LoadJobOpeningTask loadTask,
            CloseJobOpeningTask closeTask,
            SaveUpdatedJobOpeningTask saveTask,
            PublishJobOpeningEventTask eventTask) {
        this.loadTask = loadTask;
        this.closeTask = closeTask;
        this.saveTask = saveTask;
        this.eventTask = eventTask;
    }

    @Override
    public CloseJobOpeningResponse execCommand(CloseJobOpeningRequest request, JWTModel currentUser, String... args)
            throws Exception {
        if (args.length == 0) {
            throw new IllegalArgumentException("Job Opening ID is required");
        }
        String openingId = args[0];

        CloseJobOpeningContext ctx = new CloseJobOpeningContext();
        ctx.setOpeningId(openingId);
        ctx.setRequest(request);
        ctx.setCurrentUser(currentUser);

        BusinessPipeline.start(ctx)
                .next(loadTask)
                .next(closeTask)
                .next(saveTask)
                .next(eventTask)
                .execute();

        return CloseJobOpeningResponse.builder()
                .openingId(ctx.getJobOpening().getId().toString())
                .status(ctx.getJobOpening().getStatus().name())
                .closedAt(LocalDateTime.now())
                .build();
    }
}
