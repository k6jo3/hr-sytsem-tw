package com.company.hrms.training.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CourseClosedEvent extends DomainEvent {

    private String aggregateId;
    private String aggregateType;

    private String courseId;
    private String courseName;
    private String closeReason;

    public static CourseClosedEvent create(
            String courseId,
            String courseName,
            String closeReason) {

        CourseClosedEvent event = new CourseClosedEvent();
        event.setAggregateId(courseId);
        event.setAggregateType("TrainingCourse");
        // event.setEventType("CourseClosed");

        event.courseId = courseId;
        event.courseName = courseName;
        event.closeReason = closeReason;

        return event;
    }
}
