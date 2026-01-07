package com.company.hrms.project.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.project.api.request.UpdateTaskRequest;
import com.company.hrms.project.api.response.UpdateTaskResponse;
import com.company.hrms.project.domain.model.aggregate.Task;
import com.company.hrms.project.domain.model.command.UpdateTaskCommand;
import com.company.hrms.project.domain.model.valueobject.TaskId;
import com.company.hrms.project.domain.repository.ITaskRepository;

import lombok.RequiredArgsConstructor;

@Service("updateTaskServiceImpl")
@RequiredArgsConstructor
@Transactional
public class UpdateTaskServiceImpl implements CommandApiService<UpdateTaskRequest, UpdateTaskResponse> {

    private final ITaskRepository taskRepository;

    @Override
    public UpdateTaskResponse execCommand(UpdateTaskRequest req, JWTModel currentUser, String... args)
            throws Exception {
        if (req.getTaskId() == null) {
            throw new IllegalArgumentException("Task ID is required");
        }

        Task task = taskRepository.findById(new TaskId(req.getTaskId()))
                .orElseThrow(() -> new DomainException("Task not found: " + req.getTaskId()));

        UpdateTaskCommand cmd = req.toCommand();
        task.update(cmd);

        taskRepository.save(task);

        return new UpdateTaskResponse(true);
    }
}
