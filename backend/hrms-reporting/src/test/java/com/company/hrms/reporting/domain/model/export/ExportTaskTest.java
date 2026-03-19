package com.company.hrms.reporting.domain.model.export;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ExportTask 聚合根單元測試
 * <p>
 * 測試報表匯出任務的建立、狀態轉換邏輯
 * </p>
 *
 * @author Claude
 * @since 2026-03-19
 */
@DisplayName("ExportTask 聚合根測試")
class ExportTaskTest {

    @Nested
    @DisplayName("建立匯出任務")
    class CreateTests {

        @Test
        @DisplayName("應成功建立匯出任務並設定初始狀態為 PROCESSING")
        void shouldCreateWithProcessingStatus() {
            // Given
            UUID requesterId = UUID.randomUUID();

            // When
            ExportTask task = ExportTask.create(
                    "EMPLOYEE_ROSTER", "EXCEL", "員工花名冊.xlsx",
                    requesterId, "tenant-001", Map.of("status", "ACTIVE"));

            // Then
            assertThat(task.getId()).isNotNull();
            assertThat(task.getReportType()).isEqualTo("EMPLOYEE_ROSTER");
            assertThat(task.getFormat()).isEqualTo("EXCEL");
            assertThat(task.getFileName()).isEqualTo("員工花名冊.xlsx");
            assertThat(task.getRequesterId()).isEqualTo(requesterId);
            assertThat(task.getTenantId()).isEqualTo("tenant-001");
            assertThat(task.getStatus()).isEqualTo(ExportTask.ExportStatus.PROCESSING);
            assertThat(task.getFilters()).containsEntry("status", "ACTIVE");
            assertThat(task.getCreatedAt()).isNotNull();
            assertThat(task.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("建立時 errorMessage 和 completedAt 應為 null")
        void shouldHaveNullErrorAndCompletedAt() {
            // When
            ExportTask task = ExportTask.create(
                    "PAYROLL_SUMMARY", "PDF", "薪資報表.pdf",
                    UUID.randomUUID(), "tenant-001", Map.of());

            // Then
            assertThat(task.getErrorMessage()).isNull();
            assertThat(task.getCompletedAt()).isNull();
            assertThat(task.getFilePath()).isNull();
        }
    }

    @Nested
    @DisplayName("complete - 完成匯出")
    class CompleteTests {

        @Test
        @DisplayName("完成後狀態應為 COMPLETED 並記錄檔案路徑")
        void shouldSetCompletedStatusAndFilePath() {
            // Given
            ExportTask task = createTestTask();
            String filePath = "/exports/employee_roster_2026.xlsx";

            // When
            task.complete(filePath);

            // Then
            assertThat(task.getStatus()).isEqualTo(ExportTask.ExportStatus.COMPLETED);
            assertThat(task.getFilePath()).isEqualTo(filePath);
            assertThat(task.getCompletedAt()).isNotNull();
            assertThat(task.getUpdatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("fail - 匯出失敗")
    class FailTests {

        @Test
        @DisplayName("失敗後狀態應為 FAILED 並記錄錯誤訊息")
        void shouldSetFailedStatusAndErrorMessage() {
            // Given
            ExportTask task = createTestTask();
            String error = "資料庫查詢逾時";

            // When
            task.fail(error);

            // Then
            assertThat(task.getStatus()).isEqualTo(ExportTask.ExportStatus.FAILED);
            assertThat(task.getErrorMessage()).isEqualTo(error);
            assertThat(task.getCompletedAt()).isNotNull();
            assertThat(task.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("失敗後 filePath 應為 null")
        void shouldHaveNullFilePath() {
            // Given
            ExportTask task = createTestTask();

            // When
            task.fail("匯出失敗");

            // Then
            assertThat(task.getFilePath()).isNull();
        }
    }

    @Nested
    @DisplayName("ExportStatus 列舉")
    class ExportStatusTests {

        @Test
        @DisplayName("應包含所有必要的狀態值")
        void shouldContainAllStatuses() {
            assertThat(ExportTask.ExportStatus.values()).containsExactlyInAnyOrder(
                    ExportTask.ExportStatus.PENDING,
                    ExportTask.ExportStatus.PROCESSING,
                    ExportTask.ExportStatus.COMPLETED,
                    ExportTask.ExportStatus.FAILED);
        }
    }

    // ==================== 輔助方法 ====================

    private ExportTask createTestTask() {
        return ExportTask.create(
                "EMPLOYEE_ROSTER", "EXCEL", "測試報表.xlsx",
                UUID.randomUUID(), "tenant-001", Map.of());
    }
}
