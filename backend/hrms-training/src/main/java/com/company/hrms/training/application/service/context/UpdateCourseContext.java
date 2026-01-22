package com.company.hrms.training.application.service.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.training.api.request.UpdateCourseRequest;
import com.company.hrms.training.domain.model.aggregate.TrainingCourse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCourseContext extends PipelineContext {
    private final String courseId;
    private final UpdateCourseRequest request;
    private final String updatedBy;

    private TrainingCourse trainingCourse;

    public UpdateCourseContext(String courseId, UpdateCourseRequest request, String updatedBy) {
        this.courseId = courseId;
        this.request = request;
        this.updatedBy = updatedBy;
    }
}
