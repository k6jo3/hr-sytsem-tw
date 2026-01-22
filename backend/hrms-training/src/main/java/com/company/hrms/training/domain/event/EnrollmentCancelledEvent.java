package com.company.hrms.training.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EnrollmentCancelledEvent extends DomainEvent {

    private String aggregateId;
    private String aggregateType;

    private String enrollmentId;
    private String employeeId;
    private String courseId;
    private String courseName;
    private String cancelledBy;
    private String reason;

    public static EnrollmentCancelledEvent create(
            String enrollmentId,
            String employeeId,
            String courseId,
            String courseName,
            String cancelledBy,
            String reason) {

        EnrollmentCancelledEvent event = new EnrollmentCancelledEvent();
        event.setAggregateId(enrollmentId);
        event.setAggregateType("TrainingEnrollment");
        // event.setEventType("EnrollmentCancelled");

        event.enrollmentId = enrollmentId;
        event.employeeId = employeeId;
        event.courseId = courseId;
        event.courseName = courseName;
        event.cancelledBy = cancelledBy;
        event.reason = reason;

        return event;
    }
}
