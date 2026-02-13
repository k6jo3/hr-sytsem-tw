package com.company.hrms.reporting.infrastructure.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.company.hrms.reporting.domain.repository.IReportExportRepository;
import com.company.hrms.reporting.infrastructure.entity.ReportExportEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

/**
 * 報表匯出 Repository 實作
 */
@Repository
public class ReportExportRepositoryImpl implements IReportExportRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public ReportExportEntity save(ReportExportEntity entity) {
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            // Check if exists
            if (entityManager.find(ReportExportEntity.class, entity.getId()) == null) {
                entityManager.persist(entity);
            } else {
                entityManager.merge(entity);
            }
        }
        return entity;
    }

    @Override
    public Optional<ReportExportEntity> findById(String id) {
        ReportExportEntity entity = entityManager.find(ReportExportEntity.class, id);
        return Optional.ofNullable(entity);
    }
}
