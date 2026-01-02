package com.company.hrms.project.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.company.hrms.project.domain.model.aggregate.Task;
import com.company.hrms.project.domain.model.valueobject.TaskId;

public interface ITaskRepository {
    Task save(Task task);

    Optional<Task> findById(TaskId id);

    List<Task> findByProjectId(UUID projectId);

    List<Task> findByParentId(UUID parentId);

    boolean existsById(TaskId id);
}
