package com.company.hrms.reporting.domain.model.export;

import java.util.UUID;

import com.company.hrms.common.domain.model.Identifier;

public class ExportTaskId extends Identifier<UUID> {
    private ExportTaskId(UUID value) {
        super(value);
    }

    public static ExportTaskId of(UUID value) {
        return new ExportTaskId(value);
    }

    public static ExportTaskId of(String value) {
        return new ExportTaskId(UUID.fromString(value));
    }

    public static ExportTaskId generate() {
        return new ExportTaskId(UUID.randomUUID());
    }
}
