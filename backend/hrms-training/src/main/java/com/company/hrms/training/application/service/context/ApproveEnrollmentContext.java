package com.company.hrms.training.application.service.context;

import com.company.hrms.training.api.request.ApproveEnrollmentRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ApproveEnrollmentContext extends EnrollmentCourseContext {
    private ApproveEnrollmentRequest request;

    public ApproveEnrollmentContext(String enrollmentId, ApproveEnrollmentRequest request, String operatorId) {
        super(enrollmentId, operatorId);
        this.request = request;
    }
}
