package com.company.hrms.training.application.service.task.course;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.application.service.context.CourseActionContext;

/**
 * 關閉課程報名 Task
 */
@Component
public class CloseCourseTask implements PipelineTask<CourseActionContext> {

    @Override
    public void execute(CourseActionContext context) {
        context.getCourse().close(context.getReason());
    }
}
