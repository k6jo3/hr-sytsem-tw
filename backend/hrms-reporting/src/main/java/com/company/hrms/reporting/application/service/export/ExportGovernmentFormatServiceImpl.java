package com.company.hrms.reporting.application.service.export;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.reporting.api.request.ExportGovernmentFormatRequest;
import com.company.hrms.reporting.api.response.ExportFileResponse;
import com.company.hrms.reporting.domain.event.GovernmentReportExportRequestedEvent;
import com.company.hrms.reporting.domain.repository.IReportExportRepository;
import com.company.hrms.reporting.infrastructure.entity.ReportExportEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 政府申報格式匯出 API 服務實作 (Command)
 * 建立匯出任務並發布事件
 */
@Service("exportGovernmentFormatServiceImpl")
@Slf4j
@RequiredArgsConstructor
public class ExportGovernmentFormatServiceImpl
        implements CommandApiService<ExportGovernmentFormatRequest, ExportFileResponse> {

    private final IReportExportRepository reportExportRepository;
    private final EventPublisher eventPublisher;

    @Override
    @Transactional
    public ExportFileResponse execCommand(ExportGovernmentFormatRequest request, JWTModel currentUser, String... args)
            throws Exception {
        log.info("收到政府申報匯出請求: {}, 期間: {}", request.getFormatType(), request.getPeriod());

        String exportId = UUID.randomUUID().toString();

        // 1. 建立並儲存匯出記錄
        ReportExportEntity entity = ReportExportEntity.builder()
                .id(exportId)
                .reportType("GOVERNMENT_FORMAT")
                .format("TXT")
                .formatType(request.getFormatType())
                .period(request.getPeriod())
                .status("PROCESSING")
                .fileName("GovReport_" + request.getFormatType() + "_" + request.getPeriod() + ".txt")
                .requesterId(currentUser.getUserId())
                .createdAt(LocalDateTime.now())
                .build();

        reportExportRepository.save(entity);
        log.info("匯出記錄已建立: {}", exportId);

        // 2. 發布匯出請求事件
        GovernmentReportExportRequestedEvent event = new GovernmentReportExportRequestedEvent(
                exportId,
                request.getFormatType(),
                request.getPeriod());
        eventPublisher.publish(event);
        log.info("匯出請求事件已發布: {}", exportId);

        // 3. 回傳任務資訊
        return new ExportFileResponse(
                exportId,
                entity.getFileName(),
                null,
                "PROCESSING");
    }
}
