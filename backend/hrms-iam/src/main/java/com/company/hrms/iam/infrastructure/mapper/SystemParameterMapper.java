package com.company.hrms.iam.infrastructure.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.company.hrms.iam.infrastructure.po.SystemParameterPO;

/**
 * 系統參數 MyBatis Mapper
 */
@Mapper
public interface SystemParameterMapper {

    /**
     * 查詢所有系統參數
     */
    @Select("SELECT id, param_code, param_name, param_value, param_type, module, category, " +
            "description, default_value, tenant_id, is_encrypted, updated_at, updated_by, created_at " +
            "FROM system_parameters ORDER BY module, param_code")
    List<SystemParameterPO> selectAll();

    /**
     * 根據參數代碼查詢
     */
    @Select("SELECT id, param_code, param_name, param_value, param_type, module, category, " +
            "description, default_value, tenant_id, is_encrypted, updated_at, updated_by, created_at " +
            "FROM system_parameters WHERE param_code = #{paramCode}")
    SystemParameterPO selectByParamCode(@Param("paramCode") String paramCode);

    /**
     * 更新參數值
     */
    @Update("UPDATE system_parameters SET param_value = #{paramValue}, " +
            "updated_at = #{updatedAt}, updated_by = #{updatedBy} " +
            "WHERE param_code = #{paramCode}")
    void updateValue(SystemParameterPO po);
}
