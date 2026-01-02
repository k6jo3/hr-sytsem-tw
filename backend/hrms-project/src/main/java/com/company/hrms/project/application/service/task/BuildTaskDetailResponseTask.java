package com.company.hrms.project.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.project.api.response.GetTaskDetailResponse;
import com.company.hrms.project.application.service.context.TaskDetailContext;

/**
 * 建構工項詳情回應 Task
 */
@Component
public class BuildTaskDetailResponseTask implements PipelineTask<TaskDetailContext> {

    @Override
    public void execute(TaskDetailContext context) throws Exception {
        var task = context.getTask();

        GetTaskDetailResponse response = GetTaskDetailResponse.builder()
                .taskId(task.getId().getValue())
                .projectId(task.getProjectId().toString())
                .taskName(task.getTaskName())
                .description(task.getDescription())
                .parentTaskId(task.getParentTaskId() != null ? task.getParentTaskId().toString() : null)
                .status(task.getStatus().name())
                .progress(task.getProgress())
                .startDate(task.getStartDate())
                .endDate(task.getEndDate())
                .estimatedHours(task.getEstimatedHours())
                .assigneeId(task.getAssigneeId())
                .build();

        context.setResponse(response);
    }
}
