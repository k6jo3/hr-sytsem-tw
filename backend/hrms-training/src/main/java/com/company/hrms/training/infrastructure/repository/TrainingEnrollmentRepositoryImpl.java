package com.company.hrms.training.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.CommandBaseRepository;
import com.company.hrms.training.domain.model.aggregate.TrainingEnrollment;
import com.company.hrms.training.domain.model.valueobject.EnrollmentId;
import com.company.hrms.training.domain.model.valueobject.EnrollmentStatus;
import com.company.hrms.training.domain.repository.ITrainingEnrollmentRepository;
import com.company.hrms.training.infrastructure.entity.QTrainingEnrollmentEntity;
import com.company.hrms.training.infrastructure.entity.TrainingEnrollmentEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class TrainingEnrollmentRepositoryImpl
        extends CommandBaseRepository<TrainingEnrollmentEntity, String>
        implements ITrainingEnrollmentRepository {

    private final EventPublisher eventPublisher;

    public TrainingEnrollmentRepositoryImpl(JPAQueryFactory factory, EventPublisher eventPublisher) {
        super(factory, TrainingEnrollmentEntity.class);
        this.eventPublisher = eventPublisher;
    }

    @Override
    public TrainingEnrollment save(TrainingEnrollment enrollment) {
        TrainingEnrollmentEntity entity = toEntity(enrollment);
        super.save(entity);

        enrollment.getDomainEvents().forEach(eventPublisher::publish);
        enrollment.clearDomainEvents();

        return enrollment;
    }

    @Override
    public Optional<TrainingEnrollment> findById(EnrollmentId id) {
        return super.findById(id.toString()).map(this::toDomain);
    }

    @Override
    public boolean existsByCourseIdAndEmployeeId(String courseId, String employeeId) {
        QTrainingEnrollmentEntity qEnrollment = QTrainingEnrollmentEntity.trainingEnrollmentEntity;
        return factory.selectFrom(qEnrollment)
                .where(qEnrollment.course_id.eq(courseId)
                        .and(qEnrollment.employee_id.eq(employeeId)))
                .fetchFirst() != null;
    }

    @Override
    public java.math.BigDecimal sumCompletedHours(String employeeId, java.time.LocalDate startDate,
            java.time.LocalDate endDate) {
        QTrainingEnrollmentEntity qEnrollment = QTrainingEnrollmentEntity.trainingEnrollmentEntity;

        java.math.BigDecimal sum = factory.select(qEnrollment.completedHours.sum())
                .from(qEnrollment)
                .where(qEnrollment.employee_id.eq(employeeId)
                        .and(qEnrollment.status
                                .eq(EnrollmentStatus.COMPLETED)))
                .fetchOne();

        return sum != null ? sum : java.math.BigDecimal.ZERO;
    }

    @Override
    public java.util.List<TrainingEnrollment> findByDateRange(java.time.LocalDate startDate,
            java.time.LocalDate endDate) {
        QTrainingEnrollmentEntity qEnrollment = QTrainingEnrollmentEntity.trainingEnrollmentEntity;

        // Assume createdAt or completedAt. Let's filter by completedAt for stats.
        // Or generic date range if specified. Let's use createdAt for general range.
        List<TrainingEnrollmentEntity> entities = factory.selectFrom(qEnrollment)
                .where(qEnrollment.createdAt.between(startDate.atStartOfDay(), endDate.atTime(java.time.LocalTime.MAX)))
                .fetch();

        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    private TrainingEnrollmentEntity toEntity(TrainingEnrollment domain) {
        TrainingEnrollmentEntity entity = new TrainingEnrollmentEntity();
        entity.setEnrollmentId(domain.getId().toString());
        entity.setCourse_id(domain.getCourseId());
        entity.setEmployee_id(domain.getEmployeeId());
        entity.setStatus(domain.getStatus());
        entity.setReason(domain.getReason());
        entity.setRemarks(domain.getRemarks());
        entity.setApprovedBy(domain.getApprovedBy());
        entity.setApprovedAt(domain.getApprovedAt());
        entity.setRejectedBy(domain.getRejectedBy());
        entity.setRejectedAt(domain.getRejectedAt());
        entity.setRejectReason(domain.getRejectReason());
        entity.setCancelledBy(domain.getCancelledBy());
        entity.setCancelledAt(domain.getCancelledAt());
        entity.setCancelReason(domain.getCancelReason());
        entity.setAttendance(domain.isAttendance());
        entity.setAttendedHours(domain.getAttendedHours());
        entity.setAttendedAt(domain.getAttendedAt());
        entity.setCompletedHours(domain.getCompletedHours());
        entity.setScore(domain.getScore());
        entity.setPassed(domain.getPassed());
        entity.setFeedback(domain.getFeedback());
        entity.setCompletedAt(domain.getCompletedAt());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private TrainingEnrollment toDomain(TrainingEnrollmentEntity entity) {
        return TrainingEnrollment.reconstitute(
                EnrollmentId.from(entity.getEnrollmentId()),
                entity.getCourse_id(),
                entity.getEmployee_id(),
                entity.getStatus(),
                entity.getReason(),
                entity.getRemarks(),
                entity.getApprovedBy(),
                entity.getApprovedAt(),
                entity.getRejectedBy(),
                entity.getRejectedAt(),
                entity.getRejectReason(),
                entity.getCancelledBy(),
                entity.getCancelledAt(),
                entity.getCancelReason(),
                entity.isAttendance(),
                entity.getAttendedHours(),
                entity.getAttendedAt(),
                entity.getCompletedHours(),
                entity.getScore(),
                entity.getPassed(),
                entity.getFeedback(),
                entity.getCompletedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
