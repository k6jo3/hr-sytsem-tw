package com.company.hrms.reporting.domain.model.export;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import com.company.hrms.common.domain.model.AggregateRoot;

import lombok.Getter;

/**
 * 報表匯出任務聚合根
 */
@Getter
public class ExportTask extends AggregateRoot<ExportTaskId> {

    private String reportType;
    private String format;
    private ExportStatus status;
    private String fileName;
    private String filePath;
    private UUID requesterId;
    private String tenantId;
    private Map<String, Object> filters;
    private String errorMessage;
    private LocalDateTime completedAt;

    protected ExportTask() {
        super(null);
    }

    private ExportTask(ExportTaskId id) {
        super(id);
    }

    public static ExportTask create(
            String reportType,
            String format,
            String fileName,
            UUID requesterId,
            String tenantId,
            Map<String, Object> filters) {

        ExportTask task = new ExportTask(ExportTaskId.generate());
        task.reportType = reportType;
        task.format = format;
        task.fileName = fileName;
        task.requesterId = requesterId;
        task.tenantId = tenantId;
        task.filters = filters;
        task.status = ExportStatus.PROCESSING;
        task.createdAt = LocalDateTime.now();
        task.updatedAt = LocalDateTime.now();

        return task;
    }

    public void complete(String filePath) {
        this.status = ExportStatus.COMPLETED;
        this.filePath = filePath;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void fail(String errorMessage) {
        this.status = ExportStatus.FAILED;
        this.errorMessage = errorMessage;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public enum ExportStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }
}
