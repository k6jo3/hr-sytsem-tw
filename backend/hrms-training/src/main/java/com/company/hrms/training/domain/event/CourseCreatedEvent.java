package com.company.hrms.training.domain.event;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.company.hrms.common.domain.event.DomainEvent;
import com.company.hrms.training.domain.model.valueobject.CourseId;
import com.company.hrms.training.domain.model.valueobject.CourseType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CourseCreatedEvent extends DomainEvent {

    private String aggregateId;
    private String aggregateType;

    private String courseId;
    private String courseCode;
    private String courseName;
    private String courseType;
    private BigDecimal durationHours;
    private String startDate;
    private String createdBy;

    public static CourseCreatedEvent create(
            CourseId courseId,
            String courseCode,
            String courseName,
            CourseType courseType,
            BigDecimal durationHours,
            LocalDate startDate,
            String createdBy) {

        CourseCreatedEvent event = new CourseCreatedEvent();
        event.setAggregateId(courseId.toString());
        event.setAggregateType("TrainingCourse");
        // event.setEventType("CourseCreated"); // Final in base class

        event.courseId = courseId.toString();
        event.courseCode = courseCode;
        event.courseName = courseName;
        event.courseType = courseType.name();
        event.durationHours = durationHours;
        event.startDate = startDate.toString();
        event.createdBy = createdBy;

        return event;
    }
}
