package com.company.hrms.reporting.infrastructure.readmodel.repository;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.QueryBaseRepository;
import com.company.hrms.reporting.infrastructure.readmodel.AttendanceStatisticsReadModel;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class AttendanceStatisticsReadModelRepositoryImpl
        extends QueryBaseRepository<AttendanceStatisticsReadModel, String> {

    public AttendanceStatisticsReadModelRepositoryImpl(JPAQueryFactory factory) {
        super(factory, AttendanceStatisticsReadModel.class);
    }
}
