package com.company.hrms.reporting.application.service.report;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.reporting.api.request.GetProjectCostAnalysisRequest;
import com.company.hrms.reporting.api.response.ProjectCostAnalysisResponse;
import com.company.hrms.reporting.api.response.ProjectCostAnalysisResponse.ProjectCostItem;
import com.company.hrms.reporting.infrastructure.readmodel.ProjectCostAnalysisReadModel;
import com.company.hrms.reporting.infrastructure.readmodel.repository.ProjectCostAnalysisReadModelRepository;

import lombok.RequiredArgsConstructor;

/**
 * 專案成本分析 Service
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Service("getProjectCostAnalysisServiceImpl")
@RequiredArgsConstructor
public class GetProjectCostAnalysisServiceImpl
                implements QueryApiService<GetProjectCostAnalysisRequest, ProjectCostAnalysisResponse> {

        private final ProjectCostAnalysisReadModelRepository repository;

        @Override
        public ProjectCostAnalysisResponse getResponse(
                        GetProjectCostAnalysisRequest request,
                        JWTModel currentUser,
                        String... args) throws Exception {

                request.setTenantId(currentUser.getTenantId());

                QueryGroup query = QueryBuilder.where()
                                .fromDto(request)
                                .build();

                Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

                Page<ProjectCostAnalysisReadModel> page = repository.findPage(query, pageable);

                List<ProjectCostItem> items = page.getContent().stream()
                                .map(this::toDto)
                                .toList();

                return ProjectCostAnalysisResponse.builder()
                                .content(items)
                                .totalElements(page.getTotalElements())
                                .totalPages(page.getTotalPages())
                                .build();
        }

        private ProjectCostItem toDto(ProjectCostAnalysisReadModel model) {
                return ProjectCostItem.builder()
                                .projectId(model.getProjectId())
                                .projectName(model.getProjectName())
                                .customerName(model.getCustomerName())
                                .projectManager(model.getProjectManager())
                                .startDate(model.getStartDate())
                                .endDate(model.getEndDate())
                                .status(model.getStatus())
                                .budgetAmount(model.getBudgetAmount())
                                .laborCost(model.getLaborCost())
                                .otherCost(model.getOtherCost())
                                .totalCost(model.getTotalCost())
                                .costVariance(model.getCostVariance())
                                .costVarianceRate(model.getCostVarianceRate())
                                .totalHours(model.getTotalHours())
                                .utilizationRate(model.getUtilizationRate())
                                .build();
        }
}
