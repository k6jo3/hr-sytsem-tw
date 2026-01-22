package com.company.hrms.training.application.service.context;

import com.company.hrms.training.api.request.CancelEnrollmentRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CancelEnrollmentContext extends EnrollmentCourseContext {
    private CancelEnrollmentRequest request;

    public CancelEnrollmentContext(String enrollmentId, CancelEnrollmentRequest request, String operatorId) {
        super(enrollmentId, operatorId);
        this.request = request;
    }
}
