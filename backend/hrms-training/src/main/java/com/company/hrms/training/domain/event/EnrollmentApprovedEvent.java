package com.company.hrms.training.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EnrollmentApprovedEvent extends DomainEvent {

    private String aggregateId;
    private String aggregateType;

    private String enrollmentId;
    private String courseId;
    private String courseName;
    private String employeeId;
    private String employeeName;
    private String employeeEmail;
    private String startDate;
    private String location;
    private String approvedBy;

    public static EnrollmentApprovedEvent create(
            String enrollmentId,
            String courseId,
            String courseName,
            String employeeId,
            String employeeName,
            String employeeEmail,
            String startDate,
            String location,
            String approvedBy) {

        EnrollmentApprovedEvent event = new EnrollmentApprovedEvent();
        event.setAggregateId(enrollmentId);
        event.setAggregateType("TrainingEnrollment");
        // event.setEventType("EnrollmentApproved");

        event.enrollmentId = enrollmentId;
        event.courseId = courseId;
        event.courseName = courseName;
        event.employeeId = employeeId;
        event.employeeName = employeeName;
        event.employeeEmail = employeeEmail;
        event.startDate = startDate;
        event.location = location;
        event.approvedBy = approvedBy;

        return event;
    }
}
