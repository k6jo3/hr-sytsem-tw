package com.company.hrms.training.infrastructure.repository;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.QueryBaseRepository;
import com.company.hrms.training.infrastructure.entity.TrainingCourseEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class TrainingCourseQueryRepository extends QueryBaseRepository<TrainingCourseEntity, String> {

    public TrainingCourseQueryRepository(JPAQueryFactory factory) {
        super(factory, TrainingCourseEntity.class);
    }
}
