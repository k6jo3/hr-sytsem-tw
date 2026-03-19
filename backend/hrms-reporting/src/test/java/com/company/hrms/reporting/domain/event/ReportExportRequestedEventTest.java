package com.company.hrms.reporting.domain.event;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 報表匯出事件單元測試
 * <p>
 * 測試 ReportExportRequestedEvent 和 GovernmentReportExportRequestedEvent 的建立與序列化
 * </p>
 *
 * @author Claude
 * @since 2026-03-19
 */
@DisplayName("報表匯出事件測試")
class ReportExportRequestedEventTest {

    @Nested
    @DisplayName("ReportExportRequestedEvent")
    class ReportExportTests {

        @Test
        @DisplayName("應正確建立事件並設定所有欄位")
        void shouldCreateEventWithAllFields() {
            // When
            ReportExportRequestedEvent event = new ReportExportRequestedEvent(
                    "export-001", "EMPLOYEE_ROSTER", "EXCEL");

            // Then
            assertThat(event.getExportId()).isEqualTo("export-001");
            assertThat(event.getReportType()).isEqualTo("EMPLOYEE_ROSTER");
            assertThat(event.getFormat()).isEqualTo("EXCEL");
        }

        @Test
        @DisplayName("aggregateId 應為 exportId")
        void aggregateId_shouldBeExportId() {
            // When
            ReportExportRequestedEvent event = new ReportExportRequestedEvent(
                    "export-002", "PAYROLL_SUMMARY", "PDF");

            // Then
            assertThat(event.getAggregateId()).isEqualTo("export-002");
        }

        @Test
        @DisplayName("aggregateType 應為 REPORT_EXPORT")
        void aggregateType_shouldBeReportExport() {
            // When
            ReportExportRequestedEvent event = new ReportExportRequestedEvent(
                    "export-001", "EMPLOYEE_ROSTER", "EXCEL");

            // Then
            assertThat(event.getAggregateType()).isEqualTo("REPORT_EXPORT");
        }

        @Test
        @DisplayName("無參建構子應可正常建立（供 Jackson 反序列化）")
        void noArgConstructor_shouldWork() {
            // When
            ReportExportRequestedEvent event = new ReportExportRequestedEvent();

            // Then
            assertThat(event.getExportId()).isNull();
            assertThat(event.getReportType()).isNull();
            assertThat(event.getFormat()).isNull();
        }
    }

    @Nested
    @DisplayName("GovernmentReportExportRequestedEvent")
    class GovernmentReportTests {

        @Test
        @DisplayName("應正確建立政府報表匯出事件")
        void shouldCreateGovernmentReportEvent() {
            // When
            GovernmentReportExportRequestedEvent event = new GovernmentReportExportRequestedEvent(
                    "export-003", "LABOR_INSURANCE", "2026-02");

            // Then
            assertThat(event.getExportId()).isEqualTo("export-003");
            assertThat(event.getFormatType()).isEqualTo("LABOR_INSURANCE");
            assertThat(event.getPeriod()).isEqualTo("2026-02");
        }

        @Test
        @DisplayName("aggregateId 應為 exportId")
        void aggregateId_shouldBeExportId() {
            // When
            GovernmentReportExportRequestedEvent event = new GovernmentReportExportRequestedEvent(
                    "export-004", "HEALTH_INSURANCE", "2026-03");

            // Then
            assertThat(event.getAggregateId()).isEqualTo("export-004");
            assertThat(event.getAggregateType()).isEqualTo("REPORT_EXPORT");
        }

        @Test
        @DisplayName("無參建構子應可正常建立")
        void noArgConstructor_shouldWork() {
            // When
            GovernmentReportExportRequestedEvent event = new GovernmentReportExportRequestedEvent();

            // Then
            assertThat(event.getExportId()).isNull();
            assertThat(event.getFormatType()).isNull();
            assertThat(event.getPeriod()).isNull();
        }
    }
}
