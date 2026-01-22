package com.company.hrms.training.domain.event;

import java.time.LocalDate;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CoursePublishedEvent extends DomainEvent {

    private String aggregateId;
    private String aggregateType;

    private String courseId;
    private String courseName;
    private Boolean isMandatory;
    private String targetAudience;
    private String startDate;
    private String enrollmentDeadline;

    public static CoursePublishedEvent create(
            String courseId,
            String courseName,
            Boolean isMandatory,
            String targetAudience,
            LocalDate startDate,
            LocalDate enrollmentDeadline) {

        CoursePublishedEvent event = new CoursePublishedEvent();
        event.setAggregateId(courseId);
        event.setAggregateType("TrainingCourse");
        // event.setEventType("CoursePublished");

        event.courseId = courseId;
        event.courseName = courseName;
        event.isMandatory = isMandatory != null ? isMandatory : false;
        event.targetAudience = targetAudience;
        event.startDate = startDate.toString();
        event.enrollmentDeadline = enrollmentDeadline != null ? enrollmentDeadline.toString() : null;

        return event;
    }
}
