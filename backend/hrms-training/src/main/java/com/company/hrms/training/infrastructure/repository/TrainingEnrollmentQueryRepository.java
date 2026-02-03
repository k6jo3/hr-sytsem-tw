package com.company.hrms.training.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.QueryBaseRepository;
import com.company.hrms.training.domain.model.valueobject.EnrollmentStatus;
import com.company.hrms.training.infrastructure.entity.QTrainingEnrollmentEntity;
import com.company.hrms.training.infrastructure.entity.TrainingEnrollmentEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class TrainingEnrollmentQueryRepository extends QueryBaseRepository<TrainingEnrollmentEntity, String> {

    private final JPAQueryFactory queryFactory;

    public TrainingEnrollmentQueryRepository(JPAQueryFactory factory) {
        super(factory, TrainingEnrollmentEntity.class);
        this.queryFactory = factory;
    }

    /**
     * 查詢指定期間內已完成的報名記錄
     */
    public List<TrainingEnrollmentEntity> findCompletedInPeriod(LocalDate startDate, LocalDate endDate) {
        QTrainingEnrollmentEntity qe = QTrainingEnrollmentEntity.trainingEnrollmentEntity;
        return queryFactory.selectFrom(qe)
                .leftJoin(qe.course).fetchJoin()
                .where(qe.status.eq(EnrollmentStatus.COMPLETED)
                        .and(qe.completedAt.between(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay())))
                .fetch();
    }
}
