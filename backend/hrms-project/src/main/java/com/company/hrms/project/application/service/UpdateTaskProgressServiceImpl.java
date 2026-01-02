package com.company.hrms.project.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.project.api.request.UpdateTaskProgressRequest;
import com.company.hrms.project.api.response.UpdateTaskProgressResponse;
import com.company.hrms.project.domain.model.aggregate.Task;
import com.company.hrms.project.domain.model.valueobject.TaskId;
import com.company.hrms.project.domain.repository.ITaskRepository;

import lombok.RequiredArgsConstructor;

@Service("updateTaskProgressServiceImpl")
@RequiredArgsConstructor
@Transactional
public class UpdateTaskProgressServiceImpl
        implements CommandApiService<UpdateTaskProgressRequest, UpdateTaskProgressResponse> {

    private final ITaskRepository taskRepository;
    private final EventPublisher eventPublisher;

    @Override
    public UpdateTaskProgressResponse execCommand(UpdateTaskProgressRequest req, JWTModel currentUser, String... args)
            throws Exception {
        if (req.getTaskId() == null) {
            throw new IllegalArgumentException("Task ID is required");
        }

        Task task = taskRepository.findById(new TaskId(req.getTaskId()))
                .orElseThrow(() -> new DomainException("Task not found: " + req.getTaskId()));

        task.updateProgress(req.getProgress());

        taskRepository.save(task);

        eventPublisher.publishAll(task.getDomainEvents());
        task.clearDomainEvents();

        return new UpdateTaskProgressResponse(true, task.getProgress(), task.getStatus().name());
    }
}
