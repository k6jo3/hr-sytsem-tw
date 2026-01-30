package com.company.hrms.training.infrastructure.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.CommandBaseRepository;
import com.company.hrms.training.domain.model.aggregate.TrainingCourse;
import com.company.hrms.training.domain.model.valueobject.CourseId;
import com.company.hrms.training.domain.repository.ITrainingCourseRepository;
import com.company.hrms.training.infrastructure.entity.QTrainingCourseEntity;
import com.company.hrms.training.infrastructure.entity.TrainingCourseEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class TrainingCourseRepositoryImpl
        extends CommandBaseRepository<TrainingCourseEntity, String>
        implements ITrainingCourseRepository {

    private final EventPublisher eventPublisher;

    public TrainingCourseRepositoryImpl(JPAQueryFactory factory, EventPublisher eventPublisher) {
        super(factory, TrainingCourseEntity.class);
        this.eventPublisher = eventPublisher;
    }

    @Override
    public TrainingCourse save(TrainingCourse course) {
        TrainingCourseEntity entity = toEntity(course);
        super.save(entity);

        course.getDomainEvents().forEach(eventPublisher::publish);
        course.clearDomainEvents();

        return course;
    }

    @Override
    public Optional<TrainingCourse> findById(CourseId id) {
        return super.findById(id.toString()).map(this::toDomain);
    }

    @Override
    public boolean existsByCourseCode(String courseCode) {
        QTrainingCourseEntity qCourse = QTrainingCourseEntity.trainingCourseEntity;
        return factory.selectFrom(qCourse)
                .where(qCourse.courseCode.eq(courseCode))
                .fetchFirst() != null;
    }

    private TrainingCourseEntity toEntity(TrainingCourse domain) {
        TrainingCourseEntity entity = new TrainingCourseEntity();
        entity.setCourseId(domain.getId().toString());
        entity.setCourseCode(domain.getCourseCode());
        entity.setName(domain.getCourseName());
        entity.setType(domain.getCourseType());
        entity.setMode(domain.getDeliveryMode());
        entity.setCategory(domain.getCategory());
        entity.setDescription(domain.getDescription());
        entity.setInstructor(domain.getInstructor());
        entity.setInstructorInfo(domain.getInstructorInfo());
        entity.setDurationHours(domain.getDurationHours());
        entity.setMaxParticipants(domain.getMaxParticipants());
        entity.setMinParticipants(domain.getMinParticipants());
        entity.setCurrentEnrollments(domain.getCurrentEnrollments());
        entity.setStartDate(domain.getStartDate());
        entity.setEndDate(domain.getEndDate());
        entity.setStartTime(domain.getStartTime());
        entity.setEndTime(domain.getEndTime());
        entity.setLocation(domain.getLocation());
        entity.setCost(domain.getCost());
        entity.setIsMandatory(domain.getIsMandatory());
        entity.setTargetAudience(domain.getTargetAudience());
        entity.setPrerequisites(domain.getPrerequisites());
        entity.setEnrollmentDeadline(domain.getEnrollmentDeadline());
        entity.setStatus(domain.getStatus());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private TrainingCourse toDomain(TrainingCourseEntity entity) {
        return TrainingCourse.reconstitute(
                CourseId.from(entity.getCourseId()),
                entity.getCourseCode(),
                entity.getName(),
                entity.getType(),
                entity.getMode(),
                entity.getCategory(),
                entity.getDescription(),
                entity.getInstructor(),
                entity.getInstructorInfo(),
                entity.getDurationHours(),
                entity.getMaxParticipants(),
                entity.getMinParticipants(),
                entity.getCurrentEnrollments(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getLocation(),
                entity.getCost(),
                entity.getIsMandatory(),
                entity.getTargetAudience(),
                entity.getPrerequisites(),
                entity.getEnrollmentDeadline(),
                entity.getStatus(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
