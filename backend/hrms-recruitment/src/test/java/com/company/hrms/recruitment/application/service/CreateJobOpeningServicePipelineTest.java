package com.company.hrms.recruitment.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.recruitment.application.context.CreateJobOpeningContext;
import com.company.hrms.recruitment.application.dto.job.CreateJobOpeningRequest;
import com.company.hrms.recruitment.application.dto.job.CreateJobOpeningResponse;
import com.company.hrms.recruitment.application.task.job.PublishJobOpeningEventTask;
import com.company.hrms.recruitment.application.task.job.SaveJobOpeningTask;
import com.company.hrms.recruitment.application.task.job.ValidateJobOpeningTask;
import com.company.hrms.recruitment.domain.model.aggregate.JobOpening;
import com.company.hrms.recruitment.domain.model.valueobject.JobStatus;

@ExtendWith(MockitoExtension.class)
class CreateJobOpeningServicePipelineTest {

    @Mock
    private ValidateJobOpeningTask validateTask;

    @Mock
    private SaveJobOpeningTask saveTask;

    @Mock
    private PublishJobOpeningEventTask eventTask;

    @InjectMocks
    private CreateJobOpeningServiceImpl service;

    @Test
    void execCommand_ShouldExecutePipelineAndReturnResponse() throws Exception {
        // Arrange
        CreateJobOpeningRequest request = new CreateJobOpeningRequest();
        request.setJobTitle("Test Job");
        request.setDepartmentId(UUID.randomUUID().toString());
        request.setNumberOfPositions(1);

        JobOpening mockJob = JobOpening.create("Test Job", UUID.fromString(request.getDepartmentId()), 1);

        // Mock Task Execution
        when(validateTask.shouldExecute(any(CreateJobOpeningContext.class))).thenReturn(true);
        when(saveTask.shouldExecute(any(CreateJobOpeningContext.class))).thenReturn(true);
        when(eventTask.shouldExecute(any(CreateJobOpeningContext.class))).thenReturn(true);

        doAnswer(invocation -> null).when(validateTask).execute(any(CreateJobOpeningContext.class));
        doAnswer(invocation -> {
            CreateJobOpeningContext ctx = invocation.getArgument(0);
            ctx.setJobOpening(mockJob); // Simulate saving setting the job in context
            return null;
        }).when(saveTask).execute(any(CreateJobOpeningContext.class));
        doAnswer(invocation -> null).when(eventTask).execute(any(CreateJobOpeningContext.class));

        // Act
        CreateJobOpeningResponse response = service.execCommand(request, new JWTModel());

        // Assert
        assertNotNull(response);
        assertEquals(mockJob.getId().getValue().toString(), response.getOpeningId());
        assertEquals("Test Job", response.getJobTitle());
        assertEquals(JobStatus.DRAFT.name(), response.getStatus());

        verify(validateTask).execute(any(CreateJobOpeningContext.class));
        verify(saveTask).execute(any(CreateJobOpeningContext.class));
        verify(eventTask).execute(any(CreateJobOpeningContext.class));
    }
}
