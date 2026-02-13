package com.company.hrms.reporting.infrastructure.persistence.mapper;

import java.util.Map;

import com.company.hrms.reporting.domain.model.export.ExportTask;
import com.company.hrms.reporting.domain.model.export.ExportTaskId;
import com.company.hrms.reporting.infrastructure.persistence.po.ExportTaskPO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExportTaskMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ExportTaskPO toPO(ExportTask task) {
        if (task == null)
            return null;
        ExportTaskPO po = new ExportTaskPO();
        po.setId(task.getId().getValue());
        po.setReportType(task.getReportType());
        po.setFormat(task.getFormat());
        po.setStatus(task.getStatus().name());
        po.setFileName(task.getFileName());
        po.setFilePath(task.getFilePath());
        po.setRequesterId(task.getRequesterId());
        po.setTenantId(task.getTenantId());
        po.setErrorMessage(task.getErrorMessage());
        po.setCreatedAt(task.getCreatedAt());
        po.setUpdatedAt(task.getUpdatedAt());
        po.setCompletedAt(task.getCompletedAt());

        try {
            po.setFiltersJson(objectMapper.writeValueAsString(task.getFilters()));
        } catch (JsonProcessingException e) {
            po.setFiltersJson("{}");
        }
        return po;
    }

    public static ExportTask toDomain(ExportTaskPO po) {
        if (po == null)
            return null;
        ExportTask task = ExportTask.create(
                po.getReportType(),
                po.getFormat(),
                po.getFileName(),
                po.getRequesterId(),
                po.getTenantId(),
                parseFilters(po.getFiltersJson()));

        // Reflection to set ID and other fields not in create
        try {
            var idField = ExportTask.class.getSuperclass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(task, ExportTaskId.of(po.getId()));

            var statusField = ExportTask.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(task, ExportTask.ExportStatus.valueOf(po.getStatus()));

            var pathField = ExportTask.class.getDeclaredField("filePath");
            pathField.setAccessible(true);
            pathField.set(task, po.getFilePath());

            var errField = ExportTask.class.getDeclaredField("errorMessage");
            errField.setAccessible(true);
            errField.set(task, po.getErrorMessage());

            var createField = ExportTask.class.getSuperclass().getSuperclass().getDeclaredField("createdAt");
            createField.setAccessible(true);
            createField.set(task, po.getCreatedAt());

            var updateField = ExportTask.class.getSuperclass().getSuperclass().getDeclaredField("updatedAt");
            updateField.setAccessible(true);
            updateField.set(task, po.getUpdatedAt());

            var completeField = ExportTask.class.getDeclaredField("completedAt");
            completeField.setAccessible(true);
            completeField.set(task, po.getCompletedAt());

        } catch (Exception e) {
            throw new RuntimeException("Failed to map ExportTask domain", e);
        }

        return task;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> parseFilters(String json) {
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            return Map.of();
        }
    }
}
