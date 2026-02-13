package com.company.hrms.reporting.application.eventhandler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.reporting.api.request.GetEmployeeRosterRequest;
import com.company.hrms.reporting.api.request.GetPayrollSummaryRequest;
import com.company.hrms.reporting.api.response.EmployeeRosterResponse;
import com.company.hrms.reporting.api.response.PayrollSummaryResponse;
import com.company.hrms.reporting.application.service.export.ExcelExportService;
import com.company.hrms.reporting.application.service.report.GetEmployeeRosterServiceImpl;
import com.company.hrms.reporting.application.service.report.GetPayrollSummaryServiceImpl;
import com.company.hrms.reporting.domain.event.GovernmentReportExportRequestedEvent;
import com.company.hrms.reporting.domain.event.ReportExportRequestedEvent;
import com.company.hrms.reporting.domain.repository.IReportExportRepository;
import com.company.hrms.reporting.infrastructure.entity.ReportExportEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 報表匯出事件處理器
 * 
 * 監聽匯出請求事件，非同步執行報表生成與存儲
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReportExportEventHandler {

    private final IReportExportRepository reportExportRepository;
    private final ExcelExportService excelExportService;
    private final GetEmployeeRosterServiceImpl employeeRosterService;
    private final GetPayrollSummaryServiceImpl payrollSummaryService;
    private final ObjectMapper objectMapper;

    /**
     * 處理一般格式匯出 (Excel / PDF)
     */
    @KafkaListener(topics = "report_export.requested", groupId = "reporting-service")
    @EventListener
    @Async
    public void handleReportExportRequested(Object input) {
        log.info("收到報表匯出請求事件: {}", input);
        String message = null;
        try {
            ReportExportRequestedEvent event;
            if (input instanceof String) {
                message = (String) input;
                event = objectMapper.readValue(message, ReportExportRequestedEvent.class);
            } else if (input instanceof ReportExportRequestedEvent) {
                event = (ReportExportRequestedEvent) input;
                message = objectMapper.writeValueAsString(event);
            } else {
                return;
            }

            ReportExportEntity entity = reportExportRepository.findById(event.getExportId())
                    .orElseThrow(() -> new RuntimeException("匯出任務不存在: " + event.getExportId()));

            byte[] content;

            if ("EXCEL".equals(event.getFormat())) {
                content = generateExcelContent(entity);
            } else {
                // 模擬 PDF (目前僅支援 Excel)
                content = ("PDF Content Mock for " + entity.getReportType()).getBytes();
            }

            saveAndComplete(entity, content);

        } catch (Exception e) {
            log.error("報表匯出處理失敗", e);
            if (message != null) {
                markAsFailed(message, e.getMessage());
            }
        }
    }

    /**
     * 處理政府格式匯出
     */
    @KafkaListener(topics = "government_report_export.requested", groupId = "reporting-service")
    @EventListener
    @Async
    public void handleGovernmentReportExportRequested(Object input) {
        log.info("收到政府報表匯出請求事件: {}", input);
        String message = null;
        try {
            GovernmentReportExportRequestedEvent event;
            if (input instanceof String) {
                message = (String) input;
                event = objectMapper.readValue(message, GovernmentReportExportRequestedEvent.class);
            } else if (input instanceof GovernmentReportExportRequestedEvent) {
                event = (GovernmentReportExportRequestedEvent) input;
                message = objectMapper.writeValueAsString(event);
            } else {
                return;
            }

            ReportExportEntity entity = reportExportRepository.findById(event.getExportId())
                    .orElseThrow(() -> new RuntimeException("匯出任務不存在: " + event.getExportId()));

            // 模擬生成 TXT 檔案
            String contentString = "Government Report: " + event.getFormatType() + "\nPeriod: " + event.getPeriod();
            byte[] content = contentString.getBytes(java.nio.charset.StandardCharsets.UTF_8);

            saveAndComplete(entity, content);

        } catch (Exception e) {
            log.error("政府報表匯出處理失敗", e);
            if (message != null) {
                markAsFailed(message, e.getMessage());
            }
        }
    }

    private byte[] generateExcelContent(ReportExportEntity entity) throws Exception {
        JWTModel user = new JWTModel();
        user.setUserId(entity.getRequesterId());
        user.setTenantId(entity.getTenantId());

        @SuppressWarnings("unchecked")
        Map<String, Object> filters = objectMapper.readValue(entity.getFiltersJson(), Map.class);

        if ("EMPLOYEE_ROSTER".equals(entity.getReportType())) {
            GetEmployeeRosterRequest req = objectMapper.convertValue(filters, GetEmployeeRosterRequest.class);
            EmployeeRosterResponse resp = employeeRosterService.getResponse(req, user);
            return excelExportService.exportEmployeeRoster(resp.getContent().stream()
                    .map(item -> ExcelExportService.EmployeeRosterData.builder()
                            .employeeId(item.getEmployeeId())
                            .name(item.getName())
                            .departmentName(item.getDepartmentName())
                            .positionName(item.getPositionName())
                            .hireDate(item.getHireDate() != null ? item.getHireDate().toString() : "")
                            .serviceYears(item.getServiceYears())
                            .status(item.getStatus())
                            .phone(item.getPhone())
                            .email(item.getEmail())
                            .build())
                    .toList());
        } else if ("PAYROLL_SUMMARY".equals(entity.getReportType())) {
            GetPayrollSummaryRequest req = objectMapper.convertValue(filters, GetPayrollSummaryRequest.class);
            PayrollSummaryResponse resp = payrollSummaryService.getResponse(req, user);
            // 這裡可以直接用通用匯出，或者增加一個 exportPayrollSummary 專用方法
            List<String> headers = List.of("員工編號", "姓名", "部門", "實發薪資");
            List<List<Object>> data = resp.getContent().stream()
                    .map(item -> List.<Object>of(item.getEmployeeId(), item.getEmployeeName(), item.getDepartmentName(),
                            item.getNetPay()))
                    .toList();
            return excelExportService.exportToExcel(headers, data, "薪資總表");
        }

        return ("Unknown Excel Report: " + entity.getReportType()).getBytes();
    }

    private void saveAndComplete(ReportExportEntity entity, byte[] content) throws Exception {
        // 存儲檔案到 temp 目錄
        Path path = Paths.get("temp", "exports", entity.getId() + "_" + entity.getFileName());
        Files.createDirectories(path.getParent());
        Files.write(path, content);

        entity.setStatus("COMPLETED");
        entity.setCompletedAt(LocalDateTime.now());
        entity.setFilePath(path.toAbsolutePath().toString());
        reportExportRepository.save(entity);
        log.info("報表檔案已儲存: {}", entity.getFilePath());
    }

    private void markAsFailed(String message, String error) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = objectMapper.readValue(message, Map.class);
            String exportId = (String) map.get("exportId");
            reportExportRepository.findById(exportId).ifPresent(entity -> {
                entity.setStatus("FAILED");
                entity.setUpdatedAt(LocalDateTime.now());
                reportExportRepository.save(entity);
            });
        } catch (Exception ex) {
            log.error("更新任務失敗狀態時發生錯誤", ex);
        }
    }
}
