package com.company.hrms.training.application.service.context;

import com.company.hrms.training.api.request.CompleteTrainingRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CompleteTrainingContext extends EnrollmentCourseContext {
    private CompleteTrainingRequest request;

    public CompleteTrainingContext(String enrollmentId, CompleteTrainingRequest request, String operatorId) {
        super(enrollmentId, operatorId);
        this.request = request;
    }
}
