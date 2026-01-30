package com.company.hrms.reporting.infrastructure.readmodel.repository;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.QueryBaseRepository;
import com.company.hrms.reporting.infrastructure.readmodel.PayrollSummaryReadModel;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class PayrollSummaryReadModelRepositoryImpl extends QueryBaseRepository<PayrollSummaryReadModel, String> {

    public PayrollSummaryReadModelRepositoryImpl(JPAQueryFactory factory) {
        super(factory, PayrollSummaryReadModel.class);
    }
}
