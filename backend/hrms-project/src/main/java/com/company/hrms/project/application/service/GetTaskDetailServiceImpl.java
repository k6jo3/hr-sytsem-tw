package com.company.hrms.project.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.project.api.request.GetTaskDetailRequest;
import com.company.hrms.project.api.response.GetTaskDetailResponse;
import com.company.hrms.project.domain.model.aggregate.Task;
import com.company.hrms.project.domain.model.valueobject.TaskId;
import com.company.hrms.project.domain.repository.ITaskRepository;

import lombok.RequiredArgsConstructor;

@Service("getTaskDetailServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetTaskDetailServiceImpl implements QueryApiService<GetTaskDetailRequest, GetTaskDetailResponse> {

    private final ITaskRepository taskRepository;

    @Override
    public GetTaskDetailResponse getResponse(GetTaskDetailRequest req, JWTModel currentUser, String... args)
            throws Exception {
        TaskId taskId = new TaskId(req.getTaskId());
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + req.getTaskId()));

        return GetTaskDetailResponse.builder()
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
    }
}
