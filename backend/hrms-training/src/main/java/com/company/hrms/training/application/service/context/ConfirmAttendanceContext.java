package com.company.hrms.training.application.service.context;

import com.company.hrms.training.api.request.ConfirmAttendanceRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConfirmAttendanceContext extends EnrollmentActionContext {
    private ConfirmAttendanceRequest request;

    public ConfirmAttendanceContext(String enrollmentId, ConfirmAttendanceRequest request, String operatorId) {
        super(enrollmentId, operatorId);
        this.request = request;
    }
}
