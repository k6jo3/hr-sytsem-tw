package com.company.hrms.training.application.service.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.training.domain.model.aggregate.TrainingCourse;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CourseActionContext extends PipelineContext {
    private String courseId;
    private TrainingCourse course;
    private String operatorId;
    // For close action
    private String reason;

    // For complete action statistics
    private Integer completedCount;
    private Integer noShowCount;

    public CourseActionContext(String courseId, String operatorId) {
        this.courseId = courseId;
        this.operatorId = operatorId;
    }
}
