package com.company.hrms.reporting.application.service.export;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.reporting.api.request.ExportExcelRequest;
import com.company.hrms.reporting.api.response.ExportFileResponse;
import com.company.hrms.reporting.domain.event.ReportExportRequestedEvent;
import com.company.hrms.reporting.domain.repository.IReportExportRepository;
import com.company.hrms.reporting.infrastructure.entity.ReportExportEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Excel 匯出 API 服務實作 (Command)
 * 建立匯出任務並發布事件
 */
@Service("exportExcelServiceImpl")
@Slf4j
@RequiredArgsConstructor
public class ExportExcelServiceImpl implements CommandApiService<ExportExcelRequest, ExportFileResponse> {

        private final IReportExportRepository reportExportRepository;
        private final EventPublisher eventPublisher;
        private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

        @Override
        @Transactional
        public ExportFileResponse execCommand(ExportExcelRequest request, JWTModel currentUser, String... args)
                        throws Exception {
                log.info("收到 Excel 匯出請求: {}", request.getReportType());

                String exportId = UUID.randomUUID().toString();

                // 1. 建立並儲存匯出記錄
                ReportExportEntity entity = ReportExportEntity.builder()
                                .id(exportId)
                                .reportType(request.getReportType())
                                .format("EXCEL")
                                .status("PROCESSING")
                                .fileName(request.getFileName() != null ? request.getFileName() : "report.xlsx")
                                .requesterId(currentUser.getUserId())
                                .tenantId(currentUser.getTenantId())
                                .filtersJson(objectMapper.writeValueAsString(request.getFilters()))
                                .createdAt(LocalDateTime.now())
                                .build();

                reportExportRepository.save(entity);
                log.info("匯出記錄已建立: {}", exportId);

                // 2. 發布匯出請求事件
                ReportExportRequestedEvent event = new ReportExportRequestedEvent(
                                exportId,
                                request.getReportType(),
                                "EXCEL");
                eventPublisher.publish(event);
                log.info("匯出請求事件已發布: {}", exportId);

                // 3. 回傳任務資訊
                return new ExportFileResponse(
                                exportId,
                                entity.getFileName(),
                                null, // URL 待處理完成後生成
                                "PROCESSING");
        }
}
