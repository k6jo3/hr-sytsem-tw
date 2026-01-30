package com.company.hrms.project.application.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.project.api.request.GetProjectListRequest;
import com.company.hrms.project.api.response.GetProjectListResponse;
import com.company.hrms.project.api.response.ProjectListItemResponse;
import com.company.hrms.project.domain.model.aggregate.Project;
import com.company.hrms.project.domain.repository.IProjectRepository;

import lombok.RequiredArgsConstructor;

@Service("getProjectListServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetProjectListServiceImpl implements QueryApiService<GetProjectListRequest, GetProjectListResponse> {

        private final IProjectRepository projectRepository;

        @Override
        public GetProjectListResponse getResponse(GetProjectListRequest req, JWTModel currentUser, String... args)
                        throws Exception {
                // TODO: 未符合Fluent-Query-Engine的設計
                // Build QueryGroup
                QueryGroup query = QueryGroup.and();

                if (req.getStatus() != null && !req.getStatus().isEmpty()) {
                        query.eq("status", req.getStatus());
                }

                // if (req.getOwnerId() != null && !req.getOwnerId().isEmpty()) {
                // query.eq("ownerId", req.getOwnerId());
                // }

                if (req.getKeyword() != null && !req.getKeyword().isEmpty()) {
                        query.addSubGroup(
                                        QueryGroup.or()
                                                        .like("projectName", "%" + req.getKeyword() + "%")
                                                        .like("projectCode", "%" + req.getKeyword() + "%"));
                }

                // Build Pageable
                Sort.Direction direction = "ASC".equalsIgnoreCase(req.getSortDirection()) ? Sort.Direction.ASC
                                : Sort.Direction.DESC;
                Sort sort = Sort.by(direction, req.getSortBy());
                Pageable pageable = PageRequest.of(req.getPage(), req.getSize(), sort);

                // Execute Query
                Page<Project> pageResult = projectRepository.findProjects(query, pageable);

                // Map to Response
                List<ProjectListItemResponse> items = pageResult.getContent().stream()
                                .map(this::toDto)
                                .collect(Collectors.toList());

                return GetProjectListResponse.builder()
                                .items(items)
                                .total(pageResult.getTotalElements())
                                .page(pageResult.getNumber())
                                .size(pageResult.getSize())
                                .totalPages(pageResult.getTotalPages())
                                .build();
        }

        private ProjectListItemResponse toDto(Project project) {
                return ProjectListItemResponse.builder()
                                .projectId(project.getId().getValue())
                                .projectCode(project.getProjectCode())
                                .projectName(project.getProjectName())
                                .projectType(project.getProjectType())
                                .status(project.getStatus())
                                .startDate(project.getSchedule().getPlannedStartDate())
                                .endDate(project.getSchedule().getPlannedEndDate())
                                .totalBudget(project.getBudget().getBudgetAmount())
                                // .ownerId(project.getOwnerId())
                                .customerId(
                                                project.getCustomerId() != null
                                                                ? UUID.fromString(project.getCustomerId().getValue())
                                                                : null)
                                .build();
        }
}
