package com.company.hrms.project.application.service.task;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.project.application.service.context.WBSTreeContext;
import com.company.hrms.project.domain.model.aggregate.Task;
import com.company.hrms.project.domain.repository.ITaskRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入專案所有任務 Task
 */
@Component
@RequiredArgsConstructor
public class LoadProjectTasksTask implements PipelineTask<WBSTreeContext> {

    private final ITaskRepository taskRepository;

    @Override
    public void execute(WBSTreeContext context) throws Exception {
        String projectIdStr = context.getProjectId();
        if (projectIdStr == null || projectIdStr.isBlank()) {
            throw new IllegalArgumentException("ProjectId is required");
        }

        List<Task> tasks = taskRepository.findByProjectId(UUID.fromString(projectIdStr));
        context.setAllTasks(tasks);
    }

    @Override
    public String getName() {
        return "載入專案任務";
    }
}
