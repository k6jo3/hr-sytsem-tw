package com.company.hrms.training.application.service.context;

import com.company.hrms.training.domain.model.aggregate.TrainingCourse;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 包含課程資訊的報名操作 Context 基類
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class EnrollmentCourseContext extends EnrollmentActionContext {
    private TrainingCourse course;

    public EnrollmentCourseContext() {
        super();
    }

    public EnrollmentCourseContext(String enrollmentId, String operatorId) {
        super(enrollmentId, operatorId);
    }
}
