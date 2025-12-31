package com.company.hrms.attendance.application.service.checkout.context;

import com.company.hrms.attendance.api.request.attendance.CheckOutRequest;
import com.company.hrms.attendance.domain.model.aggregate.AttendanceRecord;
import com.company.hrms.attendance.domain.model.aggregate.Shift;
import com.company.hrms.common.application.pipeline.PipelineContext;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckOutContext extends PipelineContext {
    private CheckOutRequest checkOutRequest;
    private AttendanceRecord record;
    private Shift shift;
    private double workingHours;
    private String tenantId;

    public CheckOutContext(CheckOutRequest request, String tenantId) {
        this.checkOutRequest = request;
        this.tenantId = tenantId;
    }
}
