package com.company.hrms.timesheet.domain.model.valueobject;

import java.util.UUID;

import com.company.hrms.common.domain.model.Identifier;

public class TimesheetId extends Identifier<UUID> {

    public TimesheetId(UUID value) {
        super(value);
    }

    public static TimesheetId generate() {
        return new TimesheetId(UUID.randomUUID());
    }
}
