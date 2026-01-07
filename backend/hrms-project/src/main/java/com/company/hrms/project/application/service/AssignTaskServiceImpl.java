package com.company.hrms.project.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.project.api.request.AssignTaskRequest;
import com.company.hrms.project.api.response.AssignTaskResponse;
import com.company.hrms.project.domain.model.aggregate.Task;
import com.company.hrms.project.domain.model.valueobject.TaskId;
import com.company.hrms.project.domain.repository.ITaskRepository;

import lombok.RequiredArgsConstructor;

@Service("assignTaskServiceImpl")
@RequiredArgsConstructor
@Transactional
public class AssignTaskServiceImpl implements CommandApiService<AssignTaskRequest, AssignTaskResponse> {

    private final ITaskRepository taskRepository;
    private final EventPublisher eventPublisher;

    @Override
    public AssignTaskResponse execCommand(AssignTaskRequest req, JWTModel currentUser, String... args)
            throws Exception {
        if (req.getTaskId() == null) {
            throw new IllegalArgumentException("Task ID is required");
        }
        if (req.getAssigneeId() == null) {
            throw new IllegalArgumentException("Assignee ID is required");
        }

        Task task = taskRepository.findById(new TaskId(req.getTaskId()))
                .orElseThrow(() -> new DomainException("Task not found: " + req.getTaskId()));

        task.assign(req.getAssigneeId());

        taskRepository.save(task);

        eventPublisher.publishAll(task.getDomainEvents());
        task.clearDomainEvents();

        return new AssignTaskResponse(true);
    }
}
