package com.company.hrms.reporting.infrastructure.readmodel.repository;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.reporting.infrastructure.readmodel.PayrollSummaryReadModel;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 薪資匯總讀模型 Repository 實作
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Repository
public class PayrollSummaryReadModelRepositoryImpl
        extends BaseRepository<PayrollSummaryReadModel, String> {

    public PayrollSummaryReadModelRepositoryImpl(JPAQueryFactory factory) {
        super(factory, PayrollSummaryReadModel.class);
    }
}
