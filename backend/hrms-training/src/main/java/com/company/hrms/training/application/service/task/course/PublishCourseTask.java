package com.company.hrms.training.application.service.task.course;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.application.service.context.CourseActionContext;

/**
 * 發布課程 Task
 */
@Component
public class PublishCourseTask implements PipelineTask<CourseActionContext> {

    @Override
    public void execute(CourseActionContext context) {
        context.getCourse().publish();
    }
}
