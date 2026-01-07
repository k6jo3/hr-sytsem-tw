package com.company.hrms.project.domain.model.aggregate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.company.hrms.common.domain.model.AggregateRoot;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.project.domain.event.TaskAssignedEvent;
import com.company.hrms.project.domain.event.TaskCompletedEvent;
import com.company.hrms.project.domain.model.command.CreateTaskCommand;
import com.company.hrms.project.domain.model.command.UpdateTaskCommand;
import com.company.hrms.project.domain.model.valueobject.TaskId;
import com.company.hrms.project.domain.model.valueobject.TaskStatus;

import lombok.Getter;

@Getter
public class Task extends AggregateRoot<TaskId> {

    private TaskId id;
    private UUID projectId;
    private UUID parentTaskId;
    private String taskCode;
    private String taskName;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private int level;
    private BigDecimal estimatedHours;
    private BigDecimal actualHours = BigDecimal.ZERO;
    private UUID assigneeId;
    private TaskStatus status;
    private int progress = 0;
    private long version;

    private static final int MAX_LEVEL = 5;

    // Internal generic constructor
    private Task(TaskId id) {
        super(id);
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    /**
     * 重建 Aggregate Root (由 Repository 調用)
     */
    public static Task reconstitute(
            TaskId id,
            UUID projectId,
            UUID parentTaskId,
            String taskCode,
            String taskName,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            int level,
            BigDecimal estimatedHours,
            BigDecimal actualHours,
            UUID assigneeId,
            TaskStatus status,
            int progress,
            java.time.LocalDateTime createdAt,
            java.time.LocalDateTime updatedAt,
            long version) {

        Task task = new Task(id);
        task.projectId = projectId;
        task.parentTaskId = parentTaskId;
        task.taskCode = taskCode;
        task.taskName = taskName;
        task.description = description;
        task.startDate = startDate;
        task.endDate = endDate;
        task.level = level;
        task.estimatedHours = estimatedHours;
        task.actualHours = actualHours != null ? actualHours : BigDecimal.ZERO;
        task.assigneeId = assigneeId;
        task.status = status;
        task.progress = progress;
        task.version = version;

        return task;
    }

    /**
     * 建立工項
     */
    public static Task create(
            UUID projectId,
            UUID parentTaskId,
            int parentLevel,
            CreateTaskCommand cmd) {

        int newLevel = parentTaskId == null ? 1 : parentLevel + 1;

        if (newLevel > MAX_LEVEL) {
            throw new DomainException("WBS最多支援" + MAX_LEVEL + "層");
        }

        Task task = new Task(TaskId.generate());
        task.projectId = projectId;
        task.parentTaskId = parentTaskId;
        task.taskCode = cmd.getTaskCode();
        task.taskName = cmd.getTaskName();
        task.description = cmd.getDescription();
        task.startDate = cmd.getPlannedStartDate();
        task.endDate = cmd.getPlannedEndDate();
        task.level = newLevel;
        task.estimatedHours = cmd.getEstimatedHours();
        task.assigneeId = cmd.getAssigneeId();
        task.status = TaskStatus.NOT_STARTED;

        if (cmd.getAssigneeId() != null) {
            task.registerEvent(new TaskAssignedEvent(
                    task.id.getValue(),
                    projectId,
                    cmd.getAssigneeId(),
                    task.taskName));
        }

        return task;
    }

    /**
     * 更新進度
     */
    public void updateProgress(int newProgress) {
        if (newProgress < 0 || newProgress > 100) {
            throw new DomainException("進度必須在0-100之間");
        }

        this.progress = newProgress;

        if (newProgress == 100 && this.status != TaskStatus.COMPLETED) {
            this.status = TaskStatus.COMPLETED;
            registerEvent(new TaskCompletedEvent(
                    this.id.getValue(), this.projectId));
        } else if (newProgress > 0 && this.status == TaskStatus.NOT_STARTED) {
            this.status = TaskStatus.IN_PROGRESS;
        }
    }

    /**
     * 累加實際工時
     */
    public void addActualHours(BigDecimal hours) {
        if (hours != null) {
            this.actualHours = this.actualHours.add(hours);
        }
    }

    /**
     * 指派負責人
     */
    public void assign(UUID assigneeId) {
        this.assigneeId = assigneeId;
        registerEvent(new TaskAssignedEvent(
                this.id.getValue(),
                this.projectId,
                assigneeId,
                this.taskName));
    }

    public void update(UpdateTaskCommand cmd) {
        if (this.status == TaskStatus.COMPLETED) {
            throw new DomainException("Cannot update a completed task");
        }

        this.taskName = cmd.getTaskName();
        this.description = cmd.getDescription();
        this.startDate = cmd.getPlannedStartDate();
        this.endDate = cmd.getPlannedEndDate();
        this.estimatedHours = cmd.getEstimatedHours();
    }
}
