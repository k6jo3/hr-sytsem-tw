package com.company.hrms.project.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.project.domain.model.command.CreateProjectCommand;
import com.company.hrms.project.domain.model.valueobject.BudgetType;
import com.company.hrms.project.domain.model.valueobject.ProjectStatus;
import com.company.hrms.project.domain.model.valueobject.ProjectType;

/**
 * TDD: Project Aggregate Root Tests
 */
public class ProjectTest {

    @Test
    @DisplayName("Test: Successfully create a project")
    void shouldCreateProjectSuccessfully() {
        // Arrange
        CreateProjectCommand cmd = CreateProjectCommand.builder()
                .projectCode("PRJ-2025-001")
                .projectName("Test Project")
                .customerId(UUID.randomUUID())
                .projectType(ProjectType.DEVELOPMENT)
                .plannedStartDate(LocalDate.of(2025, 1, 1))
                .plannedEndDate(LocalDate.of(2025, 12, 31))
                .budgetType(BudgetType.FIXED_PRICE)
                .budgetAmount(new BigDecimal("1000000"))
                .budgetHours(new BigDecimal("1000"))
                .projectManager(UUID.randomUUID())
                .members(new ArrayList<>())
                .build();

        // Act
        Project project = Project.create(cmd);

        // Assert
        assertNotNull(project.getId());
        assertEquals("PRJ-2025-001", project.getProjectCode());
        assertEquals(ProjectStatus.PLANNING, project.getStatus());
        assertEquals(BigDecimal.ZERO, project.getActualCost());
    }

    @Test
    @DisplayName("Test: Cannot create project with invalid dates (End < Start)")
    void shouldThrowExceptionWhenEndDateBeforeStartDate() {
        // Arrange
        CreateProjectCommand cmd = CreateProjectCommand.builder()
                .projectCode("PRJ-FAIL")
                .projectName("Fail Project")
                .customerId(UUID.randomUUID())
                .projectType(ProjectType.MAINTENANCE)
                .plannedStartDate(LocalDate.of(2025, 12, 31))
                .plannedEndDate(LocalDate.of(2025, 1, 1)) // Invalid
                .projectManager(UUID.randomUUID())
                .members(new ArrayList<>())
                .build();

        // Act & Assert
        assertThrows(DomainException.class, () -> Project.create(cmd));
    }

    @Test
    @DisplayName("Test: Add member to project")
    void shouldAddMemberSuccessfully() {
        // Arrange
        Project project = createValidProject();
        UUID employeeId = UUID.randomUUID();

        // Act
        project.addMember(employeeId, "Developer", new BigDecimal("160"), new BigDecimal("800"));

        // Assert
        assertEquals(1, project.getMembers().size());
        Assertions.assertTrue(project.getMembers().stream()
                .anyMatch(m -> m.getEmployeeId().equals(employeeId)));
    }

    // Helper
    private Project createValidProject() {
        CreateProjectCommand cmd = CreateProjectCommand.builder()
                .projectCode("PRJ-TEST")
                .projectName("Test Project")
                .customerId(UUID.randomUUID())
                .projectType(ProjectType.DEVELOPMENT)
                .plannedStartDate(LocalDate.now())
                .plannedEndDate(LocalDate.now().plusMonths(6))
                .budgetType(BudgetType.FIXED_PRICE)
                .projectManager(UUID.randomUUID())
                .members(new ArrayList<>())
                .build();
        return Project.create(cmd);
    }
}
