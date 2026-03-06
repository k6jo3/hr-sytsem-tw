package com.company.hrms.iam.domain.repository;

import java.util.List;
import java.util.Optional;

import com.company.hrms.iam.domain.model.aggregate.FeatureToggle;

/**
 * 功能開關 Repository 介面
 * 定義於 Domain 層，實作於 Infrastructure 層
 */
public interface IFeatureToggleRepository {

    List<FeatureToggle> findAll();

    Optional<FeatureToggle> findByFeatureCode(String featureCode);

    void update(FeatureToggle toggle);
}
