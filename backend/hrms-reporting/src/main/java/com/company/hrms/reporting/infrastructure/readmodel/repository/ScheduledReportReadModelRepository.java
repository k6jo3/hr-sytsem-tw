package com.company.hrms.reporting.infrastructure.readmodel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.IQueryRepository;
import com.company.hrms.reporting.infrastructure.readmodel.ScheduledReportReadModel;

@Repository
public interface ScheduledReportReadModelRepository
        extends JpaRepository<ScheduledReportReadModel, String>, IQueryRepository<ScheduledReportReadModel, String> {
}
