package com.company.hrms.project.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.project.api.request.CreateTaskRequest;
import com.company.hrms.project.api.response.CreateTaskResponse;
import com.company.hrms.project.domain.model.aggregate.Task;
import com.company.hrms.project.domain.model.command.CreateTaskCommand;
import com.company.hrms.project.domain.model.valueobject.TaskId;
import com.company.hrms.project.domain.repository.ITaskRepository;

import lombok.RequiredArgsConstructor;

@Service("createTaskServiceImpl")
@RequiredArgsConstructor
@Transactional
public class CreateTaskServiceImpl implements CommandApiService<CreateTaskRequest, CreateTaskResponse> {

    private final ITaskRepository taskRepository;
    private final EventPublisher eventPublisher;

    @Override
    public CreateTaskResponse execCommand(CreateTaskRequest req, JWTModel currentUser, String... args)
            throws Exception {
        CreateTaskCommand cmd = req.toCommand();

        int parentLevel = 0;
        if (req.getParentTaskId() != null) {
            Task parentTask = taskRepository.findById(new TaskId(req.getParentTaskId().toString()))
                    .orElseThrow(() -> new DomainException("父任務不存在"));
            parentLevel = parentTask.getLevel();
        }

        Task task = Task.create(req.getProjectId(), req.getParentTaskId(), parentLevel, cmd);
        taskRepository.save(task);

        eventPublisher.publishAll(task.getDomainEvents());
        task.clearDomainEvents();

        return new CreateTaskResponse(task.getId().getValue());
    }
}
