package com.company.hrms.attendance.application.service.checkin.context;

import com.company.hrms.attendance.api.request.attendance.CheckInRequest;
import com.company.hrms.attendance.domain.model.aggregate.AttendanceRecord;
import com.company.hrms.attendance.domain.model.aggregate.Shift;
import com.company.hrms.common.application.pipeline.PipelineContext;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendanceContext extends PipelineContext {
    private CheckInRequest checkInRequest;
    private AttendanceRecord record;
    private Shift shift;
    private String tenantId;

    public AttendanceContext(CheckInRequest request, String tenantId) {
        this.checkInRequest = request;
        this.tenantId = tenantId;
    }
}
