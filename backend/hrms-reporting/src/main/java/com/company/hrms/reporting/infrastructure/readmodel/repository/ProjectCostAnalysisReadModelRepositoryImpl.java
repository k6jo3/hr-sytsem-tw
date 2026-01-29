package com.company.hrms.reporting.infrastructure.readmodel.repository;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.reporting.infrastructure.readmodel.ProjectCostAnalysisReadModel;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 專案成本分析讀模型 Repository 實作
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Repository
public class ProjectCostAnalysisReadModelRepositoryImpl
        extends BaseRepository<ProjectCostAnalysisReadModel, String> {

    public ProjectCostAnalysisReadModelRepositoryImpl(JPAQueryFactory factory) {
        super(factory, ProjectCostAnalysisReadModel.class);
    }
}
