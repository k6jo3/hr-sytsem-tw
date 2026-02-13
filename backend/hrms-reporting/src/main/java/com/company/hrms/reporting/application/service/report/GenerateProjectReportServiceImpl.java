package com.company.hrms.reporting.application.service.report;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.reporting.api.request.GenerateProjectReportRequest;
import com.company.hrms.reporting.api.response.GenerateReportResponse;
import com.company.hrms.reporting.domain.event.ReportExportRequestedEvent;
import com.company.hrms.reporting.domain.repository.IReportExportRepository;
import com.company.hrms.reporting.infrastructure.entity.ReportExportEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

/**
 * 生成專案成本報表 Service - RPT_CMD_002
 */
@Service("generateProjectReportServiceImpl")
@RequiredArgsConstructor
public class GenerateProjectReportServiceImpl
        implements CommandApiService<GenerateProjectReportRequest, GenerateReportResponse> {

    private final IReportExportRepository reportExportRepository;
    private final EventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public GenerateReportResponse execCommand(GenerateProjectReportRequest req, JWTModel currentUser, String... args)
            throws Exception {

        String exportId = UUID.randomUUID().toString();
        String reportName = "PROJECT_COST_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + ".xlsx";

        Map<String, Object> filters = new HashMap<>();
        filters.put("projectId", req.getProjectId());
        filters.put("organizationId", req.getOrganizationId());
        filters.put("startYearMonth", req.getStartYearMonth());
        filters.put("endYearMonth", req.getEndYearMonth());

        ReportExportEntity entity = ReportExportEntity.builder()
                .id(exportId)
                .reportType("PROJECT_COST_ANALYSIS")
                .format("EXCEL")
                .status("PROCESSING")
                .fileName(reportName)
                .requesterId(currentUser.getUserId())
                .tenantId(currentUser.getTenantId())
                .filtersJson(objectMapper.writeValueAsString(filters))
                .createdAt(LocalDateTime.now())
                .build();

        reportExportRepository.save(entity);

        // 發布事件觸發後端處理
        ReportExportRequestedEvent event = new ReportExportRequestedEvent(
                exportId,
                "PROJECT_COST_ANALYSIS",
                "EXCEL");
        eventPublisher.publish(event);

        return new GenerateReportResponse(exportId, reportName, "PROCESSING");
    }
}
