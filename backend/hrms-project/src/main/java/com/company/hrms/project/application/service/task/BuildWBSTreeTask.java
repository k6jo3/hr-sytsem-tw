package com.company.hrms.project.application.service.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.project.api.response.GetWBSTreeResponse;
import com.company.hrms.project.api.response.TaskTreeNodeDto;
import com.company.hrms.project.application.service.context.WBSTreeContext;
import com.company.hrms.project.domain.model.aggregate.Task;

/**
 * 建構 WBS 樹狀結構 Task
 */
@Component
public class BuildWBSTreeTask implements PipelineTask<WBSTreeContext> {

    @Override
    public void execute(WBSTreeContext context) throws Exception {
        List<Task> allTasks = context.getAllTasks();

        // 1. Convert all to DTO and map by ID
        Map<String, TaskTreeNodeDto> dtoMap = new HashMap<>();
        for (Task task : allTasks) {
            dtoMap.put(task.getId().getValue(), toDto(task));
        }

        // 2. Build Hierarchy
        List<TaskTreeNodeDto> roots = new ArrayList<>();
        for (Task task : allTasks) {
            TaskTreeNodeDto dto = dtoMap.get(task.getId().getValue());
            TaskTreeNodeDto parent = findParent(task, dtoMap);

            if (parent != null) {
                parent.getChildren().add(dto);
            } else {
                roots.add(dto);
            }
        }

        // 3. Set Response
        context.setResponse(GetWBSTreeResponse.builder()
                .projectId(context.getProjectId())
                .rootTasks(roots)
                .build());
    }

    private TaskTreeNodeDto findParent(Task task, Map<String, TaskTreeNodeDto> dtoMap) {
        if (task.getParentTaskId() == null) {
            return null;
        }
        return dtoMap.get(task.getParentTaskId().toString());
    }

    private TaskTreeNodeDto toDto(Task task) {
        return TaskTreeNodeDto.builder()
                .taskId(task.getId().getValue())
                .taskName(task.getTaskName())
                .parentId(task.getParentTaskId() != null ? task.getParentTaskId().toString() : null)
                .status(task.getStatus())
                .progress(task.getProgress())
                .startDate(task.getStartDate())
                .endDate(task.getEndDate())
                .estimatedHours(task.getEstimatedHours())
                .assigneeId(task.getAssigneeId())
                .children(new ArrayList<>())
                .build();
    }

    @Override
    public String getName() {
        return "建構 WBS 樹狀結構";
    }
}
