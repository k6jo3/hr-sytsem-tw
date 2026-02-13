package com.company.hrms.project.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.CommandBatchBaseRepository;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.project.domain.model.aggregate.Project;
import com.company.hrms.project.domain.model.aggregate.ProjectMember;
import com.company.hrms.project.domain.model.valueobject.CustomerId;
import com.company.hrms.project.domain.model.valueobject.ProjectBudget;
import com.company.hrms.project.domain.model.valueobject.ProjectId;
import com.company.hrms.project.domain.model.valueobject.ProjectSchedule;
import com.company.hrms.project.domain.repository.IProjectRepository;
import com.company.hrms.project.infrastructure.entity.ProjectEntity;
import com.company.hrms.project.infrastructure.entity.ProjectMemberEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class ProjectRepositoryImpl extends CommandBatchBaseRepository<ProjectEntity, String>
        implements IProjectRepository {

    public ProjectRepositoryImpl(JPAQueryFactory factory) {
        super(factory, ProjectEntity.class);
    }

    @Override
    public Project save(Project project) {
        ProjectEntity entity = toEntity(project);
        super.save(entity);
        return project;
    }

    @Override
    public Page<Project> findProjects(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable).map(this::toDomain);
    }

    @Override
    public Optional<Project> findById(ProjectId id) {
        return super.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public List<Project> findAll() {
        return super.findAll(new QueryGroup()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(ProjectId id) {
        return super.existsById(id.getValue());
    }

    @Override
    public void deleteById(ProjectId id) {
        super.deleteById(id.getValue());
    }

    @Override
    public Page<Project> findByMemberEmployeeId(UUID employeeId, Pageable pageable) {
        // 使用 Fluent-Query-Engine 宣告式查詢 (自動處理 JOIN)
        QueryGroup query = QueryBuilder.where()
                .eq("members.employeeId", employeeId)
                .build();

        return super.findPage(query, pageable).map(this::toDomain);
    }

    // ================= Mapper =================

    private ProjectEntity toEntity(Project domain) {
        List<ProjectMemberEntity> memberEntities = domain.getMembers().stream()
                .map(m -> ProjectMemberEntity.builder()
                        .id(m.getId())
                        .employeeId(m.getEmployeeId())
                        .role(m.getRole())
                        .allocatedHours(m.getAllocatedHours())
                        .hourlyRate(m.getHourlyRate())
                        .joinDate(m.getJoinDate())
                        .leaveDate(m.getLeaveDate())
                        .build())
                .collect(Collectors.toList());

        return ProjectEntity.builder()
                .projectId(domain.getId().getValue())
                .projectCode(domain.getProjectCode())
                .projectName(domain.getProjectName())
                .projectType(domain.getProjectType())
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .description(domain.getDescription())
                .status(domain.getStatus())
                .customerId(domain.getCustomerId() != null ? domain.getCustomerId().getValue() : null)
                .plannedStartDate(domain.getSchedule().getPlannedStartDate())
                .plannedEndDate(domain.getSchedule().getPlannedEndDate())
                .actualStartDate(domain.getSchedule().getActualStartDate())
                .actualEndDate(domain.getSchedule().getActualEndDate())
                .budgetType(domain.getBudget().getBudgetType())
                .budgetAmount(domain.getBudget().getBudgetAmount())
                .budgetHours(domain.getBudget().getBudgetHours())
                .actualHours(domain.getActualHours())
                .actualCost(domain.getActualCost())
                .members(memberEntities)
                .version(domain.getVersion())
                .build();
    }

    private Project toDomain(ProjectEntity entity) {
        List<ProjectMember> members = entity.getMembers().stream()
                .map(e -> ProjectMember.reconstitute(
                        e.getId(),
                        new ProjectId(entity.getProjectId()),
                        e.getEmployeeId(),
                        e.getRole(),
                        e.getAllocatedHours(),
                        e.getHourlyRate(),
                        e.getJoinDate(),
                        e.getLeaveDate()))
                .collect(Collectors.toList());

        ProjectSchedule schedule = new ProjectSchedule(entity.getPlannedStartDate(), entity.getPlannedEndDate());
        schedule.setActualStartDate(entity.getActualStartDate());
        schedule.setActualEndDate(entity.getActualEndDate());

        ProjectBudget budget = new ProjectBudget(entity.getBudgetType(), entity.getBudgetAmount(),
                entity.getBudgetHours());

        return Project.reconstitute(
                new ProjectId(entity.getProjectId()),
                entity.getProjectCode(),
                entity.getProjectName(),
                entity.getProjectType(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getCustomerId() != null ? new CustomerId(entity.getCustomerId()) : null,
                schedule,
                budget,
                members,
                entity.getActualHours(),
                entity.getActualCost(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion());
    }
}
