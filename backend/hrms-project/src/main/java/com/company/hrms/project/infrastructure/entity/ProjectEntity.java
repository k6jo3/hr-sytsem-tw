package com.company.hrms.project.infrastructure.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.company.hrms.project.domain.model.valueobject.BudgetType;
import com.company.hrms.project.domain.model.valueobject.ProjectStatus;
import com.company.hrms.project.domain.model.valueobject.ProjectType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "projects")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectEntity {

    @Id
    @Column(name = "project_id")
    private String projectId;

    @Column(name = "project_code", unique = true, nullable = false)
    private String projectCode;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_type")
    private ProjectType projectType;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProjectStatus status;

    @Column(name = "customer_id")
    private String customerId;

    // Project Schedule
    @Column(name = "planned_start_date")
    private LocalDate plannedStartDate;

    @Column(name = "planned_end_date")
    private LocalDate plannedEndDate;

    @Column(name = "actual_start_date")
    private LocalDate actualStartDate;

    @Column(name = "actual_end_date")
    private LocalDate actualEndDate;

    // Project Budget
    @Enumerated(EnumType.STRING)
    @Column(name = "budget_type")
    private BudgetType budgetType;

    @Column(name = "budget_amount")
    private BigDecimal budgetAmount;

    @Column(name = "budget_hours")
    private BigDecimal budgetHours;

    @Column(name = "actual_hours")
    private BigDecimal actualHours;

    @Column(name = "actual_cost")
    private BigDecimal actualCost;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "project_id") // This creates a FK in project_members table
    private List<ProjectMemberEntity> members;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "version")
    private long version;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
