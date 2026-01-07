package com.company.hrms.project.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.project.application.service.context.TaskDetailContext;
import com.company.hrms.project.domain.model.valueobject.TaskId;
import com.company.hrms.project.domain.repository.ITaskRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入工項 Task
 */
@Component
@RequiredArgsConstructor
public class LoadTaskTask implements PipelineTask<TaskDetailContext> {

    private final ITaskRepository taskRepository;

    @Override
    public void execute(TaskDetailContext context) throws Exception {
        TaskId taskId = new TaskId(context.getTaskId());
        com.company.hrms.project.domain.model.aggregate.Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + context.getTaskId()));

        context.setTask(task);
    }
}
