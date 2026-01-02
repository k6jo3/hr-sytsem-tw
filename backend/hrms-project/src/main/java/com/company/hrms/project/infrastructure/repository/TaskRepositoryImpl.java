package com.company.hrms.project.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.project.domain.model.aggregate.Task;
import com.company.hrms.project.domain.model.valueobject.TaskId;
import com.company.hrms.project.domain.repository.ITaskRepository;
import com.company.hrms.project.infrastructure.entity.TaskEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class TaskRepositoryImpl extends BaseRepository<TaskEntity, String> implements ITaskRepository {

    public TaskRepositoryImpl(JPAQueryFactory factory) {
        super(factory, TaskEntity.class);
    }

    @Override
    public Task save(Task task) {
        TaskEntity entity = toEntity(task);
        super.save(entity);
        return task;
    }

    @Override
    public Optional<Task> findById(TaskId id) {
        return super.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public List<Task> findByProjectId(UUID projectId) {
        // QueryDSL will be generated later, using string property for now
        QueryGroup query = QueryGroup.and().eq("projectId", projectId.toString());
        return super.findAll(query).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> findByParentId(UUID parentId) {
        QueryGroup query = QueryGroup.and().eq("parentTaskId", parentId.toString());
        return super.findAll(query).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(TaskId id) {
        return super.existsById(id.getValue());
    }

    // ================= Mapper =================

    private TaskEntity toEntity(Task domain) {
        return TaskEntity.builder()
                .taskId(domain.getId().getValue())
                .projectId(domain.getProjectId().toString())
                .parentTaskId(domain.getParentTaskId() != null ? domain.getParentTaskId().toString() : null)
                .taskCode(domain.getTaskCode())
                .taskName(domain.getTaskName())
                .description(domain.getDescription())
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .level(domain.getLevel())
                .estimatedHours(domain.getEstimatedHours())
                .actualHours(domain.getActualHours())
                .assigneeId(domain.getAssigneeId())
                .status(domain.getStatus())
                .progress(domain.getProgress())
                .version(domain.getVersion())
                .build();
    }

    private Task toDomain(TaskEntity entity) {
        return Task.reconstitute(
                new TaskId(entity.getTaskId()),
                UUID.fromString(entity.getProjectId()),
                entity.getParentTaskId() != null ? UUID.fromString(entity.getParentTaskId()) : null,
                entity.getTaskCode(),
                entity.getTaskName(),
                entity.getDescription(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getLevel(),
                entity.getEstimatedHours(),
                entity.getActualHours(),
                entity.getAssigneeId(),
                entity.getStatus(),
                entity.getProgress(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion());
    }
}
