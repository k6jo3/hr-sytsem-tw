package com.company.hrms.project.application.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.project.api.request.GetWBSTreeRequest;
import com.company.hrms.project.api.response.GetWBSTreeResponse;
import com.company.hrms.project.api.response.TaskTreeNodeDto;
import com.company.hrms.project.domain.model.aggregate.Task;
import com.company.hrms.project.domain.repository.ITaskRepository;

import lombok.RequiredArgsConstructor;

@Service("getWBSTreeServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetWBSTreeServiceImpl implements QueryApiService<GetWBSTreeRequest, GetWBSTreeResponse> {

    private final ITaskRepository taskRepository;

    @Override
    public GetWBSTreeResponse getResponse(GetWBSTreeRequest req, JWTModel currentUser, String... args)
            throws Exception {
        String projectIdStr = (args.length > 0 && args[0] != null) ? args[0] : req.getProjectId();

        List<Task> allTasks = taskRepository.findByProjectId(UUID.fromString(projectIdStr));

        // Map DTOs
        Map<String, TaskTreeNodeDto> dtoMap = new HashMap<>();
        List<TaskTreeNodeDto> roots = new ArrayList<>();

        // First pass: Create DTOs
        for (Task task : allTasks) {
            TaskTreeNodeDto dto = toDto(task);
            dtoMap.put(task.getId().getValue(), dto);
        }

        // Second pass: Build hierarchy
        for (Task task : allTasks) {
            TaskTreeNodeDto dto = dtoMap.get(task.getId().getValue());
            if (task.getParentTaskId() != null) {
                TaskTreeNodeDto parent = dtoMap.get(task.getParentTaskId().toString());
                if (parent != null) {
                    parent.getChildren().add(dto);
                } else {
                    // Parent not found in this set (orphan or root in this context), treat as root
                    roots.add(dto);
                }
            } else {
                roots.add(dto);
            }
        }

        return GetWBSTreeResponse.builder()
                .projectId(projectIdStr)
                .rootTasks(roots)
                .build();
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
}
