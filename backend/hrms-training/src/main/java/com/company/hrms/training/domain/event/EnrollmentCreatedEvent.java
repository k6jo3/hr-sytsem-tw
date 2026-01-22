package com.company.hrms.training.domain.event;

import java.math.BigDecimal;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EnrollmentCreatedEvent extends DomainEvent {

    private String aggregateId;
    private String aggregateType;

    private String enrollmentId;
    private String courseId;
    private String courseName;
    private String employeeId;
    private String employeeName;
    private String managerId;
    private String managerName;
    private BigDecimal trainingHours;
    private BigDecimal cost;
    private String reason;

    public static EnrollmentCreatedEvent create(
            String enrollmentId,
            String courseId,
            String courseName,
            String employeeId,
            String employeeName,
            String managerId,
            String managerName,
            BigDecimal trainingHours,
            BigDecimal cost,
            String reason) {

        EnrollmentCreatedEvent event = new EnrollmentCreatedEvent();
        event.setAggregateId(enrollmentId);
        event.setAggregateType("TrainingEnrollment");
        // event.setEventType("EnrollmentCreated");

        event.enrollmentId = enrollmentId;
        event.courseId = courseId;
        event.courseName = courseName;
        event.employeeId = employeeId;
        event.employeeName = employeeName;
        event.managerId = managerId;
        event.managerName = managerName;
        event.trainingHours = trainingHours;
        event.cost = cost;
        event.reason = reason;

        return event;
    }
}
