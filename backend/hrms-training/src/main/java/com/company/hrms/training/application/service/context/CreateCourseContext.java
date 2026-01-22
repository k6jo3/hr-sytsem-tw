package com.company.hrms.training.application.service.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.training.api.request.CreateCourseRequest;
import com.company.hrms.training.domain.model.aggregate.TrainingCourse;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CreateCourseContext extends PipelineContext {
    private CreateCourseRequest request;
    private TrainingCourse trainingCourse;
    private String createdBy;

    public CreateCourseContext(CreateCourseRequest request, String createdBy) {
        this.request = request;
        this.createdBy = createdBy;
    }
}
