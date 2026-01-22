package com.company.hrms.training.application.service.context;

import com.company.hrms.training.api.request.RejectEnrollmentRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RejectEnrollmentContext extends EnrollmentCourseContext {
    private RejectEnrollmentRequest request;

    public RejectEnrollmentContext(String enrollmentId, RejectEnrollmentRequest request, String operatorId) {
        super(enrollmentId, operatorId);
        this.request = request;
    }
}
