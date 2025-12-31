package com.company.hrms.attendance.domain.model.entity;

import com.company.hrms.attendance.domain.model.valueobject.CorrectionId;
import com.company.hrms.common.domain.model.Entity;

public class AttendanceCorrection extends Entity<CorrectionId> {
    public AttendanceCorrection(CorrectionId id) {
        super(id);
    }
}
