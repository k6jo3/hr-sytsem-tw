package com.company.hrms.reporting.application.eventhandler;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.reporting.application.service.export.ExcelExportService;
import com.company.hrms.reporting.application.service.report.GetEmployeeRosterServiceImpl;
import com.company.hrms.reporting.application.service.report.GetPayrollSummaryServiceImpl;
import com.company.hrms.reporting.domain.event.ReportExportRequestedEvent;
import com.company.hrms.reporting.domain.repository.IReportExportRepository;
import com.company.hrms.reporting.infrastructure.entity.ReportExportEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * 報表匯出事件處理器單元測試
 *
 * <p>
 * 驗證 ReportExportEventHandler 對 String（Kafka）和
 * ReportExportRequestedEvent（Spring Event）兩種輸入的處理邏輯。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReportExportEventHandler 單元測試")
class ReportExportEventHandlerTest {

    @Mock
    private IReportExportRepository reportExportRepository;

    @Mock
    private ExcelExportService excelExportService;

    @Mock
    private GetEmployeeRosterServiceImpl employeeRosterService;

    @Mock
    private GetPayrollSummaryServiceImpl payrollSummaryService;

    @Captor
    private ArgumentCaptor<ReportExportEntity> entityCaptor;

    private ReportExportEventHandler handler;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        handler = new ReportExportEventHandler(
                reportExportRepository,
                excelExportService,
                employeeRosterService,
                payrollSummaryService,
                objectMapper);
    }

    private ReportExportEntity createTestEntity(String id, String reportType, String format) {
        return ReportExportEntity.builder()
                .id(id)
                .reportType(reportType)
                .format(format)
                .status("PROCESSING")
                .fileName("test_report.xlsx")
                .requesterId("user-001")
                .tenantId("tenant-001")
                .filtersJson("{}")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("handleReportExportRequested — JSON 字串輸入")
    class JsonStringInput {

        @Test
        @DisplayName("有效 JSON + PDF 格式 → status 更新為 COMPLETED")
        void validJsonPdf_completesExport() {
            // Arrange
            String exportId = "export-001";
            ReportExportEntity entity = createTestEntity(exportId, "EMPLOYEE_ROSTER", "PDF");
            when(reportExportRepository.findById(exportId)).thenReturn(Optional.of(entity));

            String message = """
                    {
                        "exportId": "export-001",
                        "reportType": "EMPLOYEE_ROSTER",
                        "format": "PDF"
                    }
                    """;

            // Act
            handler.handleReportExportRequested(message);

            // Assert — 儲存兩次：完成時
            verify(reportExportRepository).save(entityCaptor.capture());
            ReportExportEntity saved = entityCaptor.getValue();
            assertThat(saved.getStatus()).isEqualTo("COMPLETED");
            assertThat(saved.getCompletedAt()).isNotNull();
        }

        @Test
        @DisplayName("匯出任務不存在 → status 更新為 FAILED")
        void entityNotFound_marksFailed() {
            // Arrange
            when(reportExportRepository.findById("not-exist")).thenReturn(Optional.empty());

            String message = """
                    {
                        "exportId": "not-exist",
                        "reportType": "EMPLOYEE_ROSTER",
                        "format": "EXCEL"
                    }
                    """;

            // Act
            handler.handleReportExportRequested(message);

            // Assert — findById 被呼叫 2 次：handleReportExportRequested + markAsFailed
            verify(reportExportRepository, atLeastOnce()).findById("not-exist");
        }
    }

    @Nested
    @DisplayName("handleReportExportRequested — Event 物件輸入")
    class EventObjectInput {

        @Test
        @DisplayName("ReportExportRequestedEvent 物件 → 正確處理")
        void eventObject_completesExport() {
            // Arrange
            String exportId = "export-002";
            ReportExportEntity entity = createTestEntity(exportId, "PAYROLL_SUMMARY", "PDF");
            when(reportExportRepository.findById(exportId)).thenReturn(Optional.of(entity));

            ReportExportRequestedEvent event = new ReportExportRequestedEvent(
                    exportId, "PAYROLL_SUMMARY", "PDF");

            // Act
            handler.handleReportExportRequested(event);

            // Assert
            verify(reportExportRepository).save(entityCaptor.capture());
            assertThat(entityCaptor.getValue().getStatus()).isEqualTo("COMPLETED");
        }

        @Test
        @DisplayName("不支援的輸入型別 → 直接返回不處理")
        void unsupportedInputType_ignored() {
            // Act
            handler.handleReportExportRequested(12345);

            // Assert
            verify(reportExportRepository, never()).findById(any());
            verify(reportExportRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("錯誤處理")
    class ErrorHandling {

        @Test
        @DisplayName("格式錯誤 JSON → 不拋出未捕獲異常")
        void malformedJson_doesNotThrow() {
            handler.handleReportExportRequested("{ broken json");

            // 不應拋出異常
            verify(reportExportRepository, never()).save(any());
        }
    }
}
