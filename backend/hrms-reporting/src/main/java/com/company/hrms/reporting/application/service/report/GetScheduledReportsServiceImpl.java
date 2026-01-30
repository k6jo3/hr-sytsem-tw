package com.company.hrms.reporting.application.service.report;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.reporting.api.request.GetScheduledReportsRequest;
import com.company.hrms.reporting.api.response.ScheduledReportResponse;
import com.company.hrms.reporting.api.response.ScheduledReportResponse.ScheduledReportItem;
import com.company.hrms.reporting.infrastructure.readmodel.ScheduledReportReadModel;
import com.company.hrms.reporting.infrastructure.readmodel.repository.ScheduledReportReadModelRepository;

import lombok.RequiredArgsConstructor;

/**
 * 排程報表查詢 Service
 */
@Service("getScheduledReportsServiceImpl")
@RequiredArgsConstructor
public class GetScheduledReportsServiceImpl
                implements QueryApiService<GetScheduledReportsRequest, ScheduledReportResponse> {

        private final ScheduledReportReadModelRepository repository;

        @Override
        public ScheduledReportResponse getResponse(GetScheduledReportsRequest request, JWTModel currentUser,
                        String... args)
                        throws Exception {

                request.setTenantId(currentUser.getTenantId());

                QueryBuilder builder = QueryBuilder.where()
                                .fromDto(request);

                if (request.getEnabled() != null) {
                        builder.and("isEnabled", Operator.EQ, request.getEnabled());
                }

                if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
                        builder.and("scheduleName", Operator.LIKE, request.getKeyword());
                }

                QueryGroup query = builder.build();

                Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

                Page<ScheduledReportReadModel> page = repository.findPage(query, pageable);

                List<ScheduledReportItem> items = page.getContent().stream()
                                .map(this::toDto)
                                .toList();

                return ScheduledReportResponse.builder()
                                .content(items)
                                .totalElements(page.getTotalElements())
                                .totalPages(page.getTotalPages())
                                .build();
        }

        private ScheduledReportItem toDto(ScheduledReportReadModel model) {
                return ScheduledReportItem.builder()
                                .scheduleId(model.getId())
                                .scheduleName(model.getScheduleName())
                                .reportType(model.getReportType())
                                .cronExpression(model.getCronExpression())
                                .nextRunTime(model.getNextRunTime())
                                .isEnabled(model.getIsEnabled())
                                .build();
        }
}
