package com.company.hrms.training.domain.repository;

import java.util.Optional;

import com.company.hrms.training.domain.model.aggregate.TrainingEnrollment;
import com.company.hrms.training.domain.model.valueobject.EnrollmentId;

public interface ITrainingEnrollmentRepository {

    TrainingEnrollment save(TrainingEnrollment enrollment);

    Optional<TrainingEnrollment> findById(EnrollmentId id);

    boolean existsByCourseIdAndEmployeeId(String courseId, String employeeId);

    java.math.BigDecimal sumCompletedHours(String employeeId, java.time.LocalDate startDate,
            java.time.LocalDate endDate);

    // For statistics, we might need a list of enrollments to aggregate
    java.util.List<TrainingEnrollment> findByDateRange(
            java.time.LocalDate startDate, java.time.LocalDate endDate);
}
