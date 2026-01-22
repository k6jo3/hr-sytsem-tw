package com.company.hrms.training.domain.repository;

import java.util.Optional;

import com.company.hrms.training.domain.model.aggregate.TrainingCourse;
import com.company.hrms.training.domain.model.valueobject.CourseId;

public interface ITrainingCourseRepository {

    TrainingCourse save(TrainingCourse course);

    Optional<TrainingCourse> findById(CourseId id);

    boolean existsByCourseCode(String courseCode);

    // Additional methods for validation if needed
}
