package com.company.hrms.training.domain.event;

import java.math.BigDecimal;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TrainingCompletedEvent extends DomainEvent {

    private String aggregateId;
    private String aggregateType;

    private String enrollmentId;
    private String employeeId;
    private String employeeName;
    private String courseId;
    private String courseName;
    private String courseCategory;
    private BigDecimal completedHours;
    private String completedDate;
    private BigDecimal score;
    private Boolean passed;

    public static TrainingCompletedEvent create(
            String enrollmentId,
            String employeeId,
            String employeeName,
            String courseId,
            String courseName,
            String courseCategory,
            BigDecimal completedHours,
            String completedDate,
            BigDecimal score,
            Boolean passed) {

        TrainingCompletedEvent event = new TrainingCompletedEvent();
        event.setAggregateId(enrollmentId);
        event.setAggregateType("TrainingEnrollment");
        // event.setEventType("TrainingCompleted");

        event.enrollmentId = enrollmentId;
        event.employeeId = employeeId;
        event.employeeName = employeeName;
        event.courseId = courseId;
        event.courseName = courseName;
        event.courseCategory = courseCategory;
        event.completedHours = completedHours;
        event.completedDate = completedDate;
        event.score = score;
        event.passed = passed;

        return event;
    }
}
