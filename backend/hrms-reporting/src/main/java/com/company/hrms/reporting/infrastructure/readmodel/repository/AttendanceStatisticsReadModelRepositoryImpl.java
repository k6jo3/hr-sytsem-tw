package com.company.hrms.reporting.infrastructure.readmodel.repository;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.reporting.infrastructure.readmodel.AttendanceStatisticsReadModel;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 差勤統計讀模型 Repository 實作
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Repository
public class AttendanceStatisticsReadModelRepositoryImpl
        extends BaseRepository<AttendanceStatisticsReadModel, String> {

    public AttendanceStatisticsReadModelRepositoryImpl(JPAQueryFactory factory) {
        super(factory, AttendanceStatisticsReadModel.class);
    }
}
