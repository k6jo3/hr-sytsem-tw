package com.company.hrms.training.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EnrollmentRejectedEvent extends DomainEvent {

    private String aggregateId;
    private String aggregateType;

    private String enrollmentId;
    private String employeeId;
    private String employeeName;
    private String employeeEmail;
    private String courseName;
    private String rejectedBy;
    private String reason;

    public static EnrollmentRejectedEvent create(
            String enrollmentId,
            String employeeId,
            String employeeName,
            String employeeEmail,
            String courseName,
            String rejectedBy,
            String reason) {

        EnrollmentRejectedEvent event = new EnrollmentRejectedEvent();
        event.setAggregateId(enrollmentId);
        event.setAggregateType("TrainingEnrollment");
        // event.setEventType("EnrollmentRejected");

        event.enrollmentId = enrollmentId;
        event.employeeId = employeeId;
        event.employeeName = employeeName;
        event.employeeEmail = employeeEmail;
        event.courseName = courseName;
        event.rejectedBy = rejectedBy;
        event.reason = reason;

        return event;
    }
}
