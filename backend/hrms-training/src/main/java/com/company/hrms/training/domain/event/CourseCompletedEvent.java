package com.company.hrms.training.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CourseCompletedEvent extends DomainEvent {

    private String aggregateId;
    private String aggregateType;

    private String courseId;
    private String courseName;
    private Integer completedCount;
    private Integer noShowCount;

    public static CourseCompletedEvent create(
            String courseId,
            String courseName,
            Integer completedCount,
            Integer noShowCount) {

        CourseCompletedEvent event = new CourseCompletedEvent();
        event.setAggregateId(courseId);
        event.setAggregateType("TrainingCourse");
        // event.setEventType("CourseCompleted");

        event.courseId = courseId;
        event.courseName = courseName;
        event.completedCount = completedCount;
        event.noShowCount = noShowCount;

        return event;
    }
}
