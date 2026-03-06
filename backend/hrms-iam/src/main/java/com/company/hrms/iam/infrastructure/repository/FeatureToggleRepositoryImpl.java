package com.company.hrms.iam.infrastructure.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.company.hrms.iam.domain.model.aggregate.FeatureToggle;
import com.company.hrms.iam.domain.repository.IFeatureToggleRepository;
import com.company.hrms.iam.infrastructure.dao.FeatureToggleDAO;
import com.company.hrms.iam.infrastructure.po.FeatureTogglePO;

/**
 * 功能開關 Repository 實作
 * 負責 PO 與 Domain Object 之間的轉換
 */
@Component
public class FeatureToggleRepositoryImpl implements IFeatureToggleRepository {

    private final FeatureToggleDAO dao;

    public FeatureToggleRepositoryImpl(FeatureToggleDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<FeatureToggle> findAll() {
        return dao.selectAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<FeatureToggle> findByFeatureCode(String featureCode) {
        FeatureTogglePO po = dao.selectByFeatureCode(featureCode);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public void update(FeatureToggle toggle) {
        FeatureTogglePO po = FeatureTogglePO.builder()
                .featureCode(toggle.getFeatureCode())
                .enabled(toggle.isEnabled())
                .updatedAt(Timestamp.valueOf(toggle.getUpdatedAt()))
                .updatedBy(toggle.getUpdatedBy())
                .build();
        dao.updateToggle(po);
    }

    private FeatureToggle toDomain(FeatureTogglePO po) {
        return FeatureToggle.builder()
                .id(po.getId())
                .featureCode(po.getFeatureCode())
                .featureName(po.getFeatureName())
                .module(po.getModule())
                .enabled(po.getEnabled() != null && po.getEnabled())
                .description(po.getDescription())
                .tenantId(po.getTenantId())
                .updatedAt(po.getUpdatedAt() != null ? po.getUpdatedAt().toLocalDateTime() : null)
                .updatedBy(po.getUpdatedBy())
                .build();
    }
}
