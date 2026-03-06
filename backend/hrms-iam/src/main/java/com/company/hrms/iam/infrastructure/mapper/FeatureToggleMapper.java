package com.company.hrms.iam.infrastructure.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.company.hrms.iam.infrastructure.po.FeatureTogglePO;

/**
 * 功能開關 MyBatis Mapper
 */
@Mapper
public interface FeatureToggleMapper {

    /**
     * 查詢所有功能開關
     */
    @Select("SELECT id, feature_code, feature_name, module, enabled, description, " +
            "tenant_id, updated_at, updated_by, created_at " +
            "FROM feature_toggles ORDER BY module, feature_code")
    List<FeatureTogglePO> selectAll();

    /**
     * 根據功能代碼查詢
     */
    @Select("SELECT id, feature_code, feature_name, module, enabled, description, " +
            "tenant_id, updated_at, updated_by, created_at " +
            "FROM feature_toggles WHERE feature_code = #{featureCode}")
    FeatureTogglePO selectByFeatureCode(@Param("featureCode") String featureCode);

    /**
     * 更新功能開關狀態
     */
    @Update("UPDATE feature_toggles SET enabled = #{enabled}, " +
            "updated_at = #{updatedAt}, updated_by = #{updatedBy} " +
            "WHERE feature_code = #{featureCode}")
    void updateToggle(FeatureTogglePO po);
}
