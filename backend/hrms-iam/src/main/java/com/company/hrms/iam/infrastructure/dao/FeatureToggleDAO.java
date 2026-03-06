package com.company.hrms.iam.infrastructure.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.company.hrms.iam.infrastructure.mapper.FeatureToggleMapper;
import com.company.hrms.iam.infrastructure.po.FeatureTogglePO;

/**
 * 功能開關 DAO
 * 封裝 Mapper 操作
 */
@Repository
public class FeatureToggleDAO {

    private final FeatureToggleMapper mapper;

    public FeatureToggleDAO(FeatureToggleMapper mapper) {
        this.mapper = mapper;
    }

    public List<FeatureTogglePO> selectAll() {
        return mapper.selectAll();
    }

    public FeatureTogglePO selectByFeatureCode(String featureCode) {
        return mapper.selectByFeatureCode(featureCode);
    }

    public void updateToggle(FeatureTogglePO po) {
        mapper.updateToggle(po);
    }
}
