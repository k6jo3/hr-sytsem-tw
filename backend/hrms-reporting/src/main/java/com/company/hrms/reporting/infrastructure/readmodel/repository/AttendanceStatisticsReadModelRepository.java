package com.company.hrms.reporting.infrastructure.readmodel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.IQueryRepository;
import com.company.hrms.reporting.infrastructure.readmodel.AttendanceStatisticsReadModel;

/**
 * 差勤統計讀模型 Repository
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Repository
public interface AttendanceStatisticsReadModelRepository
                extends JpaRepository<AttendanceStatisticsReadModel, String>,
                IQueryRepository<AttendanceStatisticsReadModel, String> {
}
