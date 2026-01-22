package com.company.hrms.training.application.service.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.training.api.request.EnrollCourseRequest;
import com.company.hrms.training.domain.model.aggregate.TrainingCourse;
import com.company.hrms.training.domain.model.aggregate.TrainingEnrollment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnrollCourseContext extends PipelineContext {
    private final EnrollCourseRequest request;
    private final String employeeId; // The employee being enrolled
    private final String requestedBy; // Operator/Manager ID

    // Loaded Data
    private TrainingCourse course;
    private String employeeName; // Need to fetch from IAM/Org service
    private String managerId; // Need to fetch from Org service
    private String managerName; // Need to fetch from Org service

    // Result
    private TrainingEnrollment enrollment;

    public EnrollCourseContext(EnrollCourseRequest request, String employeeId, String requestedBy) {
        this.request = request;
        this.employeeId = employeeId;
        this.requestedBy = requestedBy;
    }
}
