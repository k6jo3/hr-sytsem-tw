package com.company.hrms.reporting.infrastructure.readmodel.repository;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.QueryBaseRepository;
import com.company.hrms.reporting.infrastructure.readmodel.ScheduledReportReadModel;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class ScheduledReportReadModelRepositoryImpl extends QueryBaseRepository<ScheduledReportReadModel, String> {

    public ScheduledReportReadModelRepositoryImpl(JPAQueryFactory factory) {
        super(factory, ScheduledReportReadModel.class);
    }
}
