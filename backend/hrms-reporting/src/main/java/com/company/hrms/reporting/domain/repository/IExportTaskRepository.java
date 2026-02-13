package com.company.hrms.reporting.domain.repository;

import java.util.Optional;

import com.company.hrms.reporting.domain.model.export.ExportTask;
import com.company.hrms.reporting.domain.model.export.ExportTaskId;

/**
 * 報表匯出任務 Repository 介面
 */
public interface IExportTaskRepository {

    ExportTask save(ExportTask task);

    Optional<ExportTask> findById(ExportTaskId id);

    void delete(ExportTaskId id);
}
