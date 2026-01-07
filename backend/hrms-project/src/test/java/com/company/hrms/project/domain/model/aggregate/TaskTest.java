package com.company.hrms.project.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.project.domain.model.command.CreateTaskCommand;
import com.company.hrms.project.domain.model.valueobject.TaskStatus;

/**
 * TDD: Task Aggregate Root Tests (WBS)
 */
public class TaskTest {

    @Test
    @DisplayName("Test: Successfully create a root task (Level 1)")
    void shouldCreateRootTaskSuccessfully() {
        // Arrange
        UUID projectId = UUID.randomUUID();
        CreateTaskCommand cmd = CreateTaskCommand.builder()
                .taskCode("TASK-001")
                .taskName("Analysis")
                .estimatedHours(new BigDecimal("40"))
                .build();

        // Act
        Task task = Task.create(projectId, null, 0, cmd);

        // Assert
        assertNotNull(task.getId());
        assertEquals("TASK-001", task.getTaskCode());
        assertEquals(1, task.getLevel());
        assertEquals(TaskStatus.NOT_STARTED, task.getStatus());
    }

    @Test
    @DisplayName("Test: Successfully create a child task (Level 2)")
    void shouldCreateChildTaskSuccessfully() {
        // Arrange
        UUID projectId = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();
        CreateTaskCommand cmd = CreateTaskCommand.builder()
                .taskCode("TASK-001-01")
                .taskName("Requirements")
                .estimatedHours(new BigDecimal("20"))
                .build();

        // Act
        Task task = Task.create(projectId, parentId, 1, cmd);

        // Assert
        assertEquals(2, task.getLevel());
        assertEquals(parentId, task.getParentTaskId());
    }

    @Test
    @DisplayName("Test: Cannot create task exceeded max level (Level > 5)")
    void shouldThrowExceptionWhenMaxLevelExceeded() {
        // Arrange
        UUID projectId = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();
        CreateTaskCommand cmd = CreateTaskCommand.builder()
                .taskCode("TASK-LEVEL-6")
                .taskName("Too Deep")
                .build();

        // Act & Assert
        // Parent Level 5 -> New Level 6 (Fail)
        assertThrows(DomainException.class, () -> Task.create(projectId, parentId, 5, cmd));
    }

    @Test
    @DisplayName("Test: Update Task Progress")
    void shouldUpdateProgressSuccessfully() {
        // Arrange
        Task task = createValidTask();

        // Act
        task.updateProgress(50);

        // Assert
        assertEquals(50, task.getProgress());
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());

        // Act: Complete
        task.updateProgress(100);
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
    }

    // Helper
    private Task createValidTask() {
        UUID projectId = UUID.randomUUID();
        CreateTaskCommand cmd = CreateTaskCommand.builder()
                .taskCode("TASK-TEST")
                .taskName("Test Task")
                .estimatedHours(new BigDecimal("10"))
                .build();
        return Task.create(projectId, null, 0, cmd);
    }
}
