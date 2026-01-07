package com.company.hrms.project.application.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.project.api.request.GetProjectDetailRequest;
import com.company.hrms.project.api.response.GetProjectDetailResponse;
import com.company.hrms.project.api.response.ProjectMemberDto;
import com.company.hrms.project.domain.model.aggregate.Project;
import com.company.hrms.project.domain.model.aggregate.ProjectMember;
import com.company.hrms.project.domain.model.valueobject.ProjectId;
import com.company.hrms.project.domain.repository.IProjectRepository;

import lombok.RequiredArgsConstructor;

@Service("getProjectDetailServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetProjectDetailServiceImpl implements QueryApiService<GetProjectDetailRequest, GetProjectDetailResponse> {

    private final IProjectRepository projectRepository;

    @Override
    public GetProjectDetailResponse getResponse(GetProjectDetailRequest req, JWTModel currentUser, String... args)
            throws Exception {
        String projectIdStr = (args.length > 0 && args[0] != null) ? args[0] : req.getProjectId();

        Project project = projectRepository.findById(new ProjectId(projectIdStr))
                .orElseThrow(() -> new DomainException("專案不存在: " + projectIdStr));

        return toDto(project);
    }

    private GetProjectDetailResponse toDto(Project project) {
        List<ProjectMemberDto> members = project.getMembers().stream()
                .map(this::toMemberDto)
                .collect(Collectors.toList());

        return GetProjectDetailResponse.builder()
                .projectId(project.getId().getValue())
                .projectCode(project.getProjectCode())
                .projectName(project.getProjectName())
                .projectType(project.getProjectType())
                .status(project.getStatus())
                .description(project.getDescription())
                .plannedStartDate(project.getSchedule().getPlannedStartDate())
                .plannedEndDate(project.getSchedule().getPlannedEndDate())
                .actualStartDate(project.getSchedule().getActualStartDate())
                .actualEndDate(project.getSchedule().getActualEndDate())
                .budgetType(project.getBudget().getBudgetType())
                .budgetAmount(project.getBudget().getBudgetAmount())
                .budgetHours(project.getBudget().getBudgetHours())
                .actualHours(project.getActualHours())
                .actualCost(project.getActualCost())
                .customerId(
                        project.getCustomerId() != null ? UUID.fromString(project.getCustomerId().getValue()) : null)
                .members(members)
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .version(project.getVersion())
                .build();
    }

    private ProjectMemberDto toMemberDto(ProjectMember member) {
        return ProjectMemberDto.builder()
                .id(member.getId().toString())
                .employeeId(member.getEmployeeId())
                .role(member.getRole())
                .allocatedHours(member.getAllocatedHours())
                .joinDate(member.getJoinDate())
                .leaveDate(member.getLeaveDate())
                .build();
    }
}
