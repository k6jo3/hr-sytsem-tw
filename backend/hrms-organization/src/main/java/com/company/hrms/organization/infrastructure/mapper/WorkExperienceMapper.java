package com.company.hrms.organization.infrastructure.mapper;

import com.company.hrms.organization.infrastructure.po.WorkExperiencePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工作經歷 MyBatis Mapper
 */
@Mapper
public interface WorkExperienceMapper {

    /**
     * 根據 ID 查詢工作經歷
     */
    WorkExperiencePO selectById(@Param("id") String id);

    /**
     * 根據員工 ID 查詢工作經歷
     */
    List<WorkExperiencePO> selectByEmployeeId(@Param("employeeId") String employeeId);

    /**
     * 新增工作經歷
     */
    int insert(WorkExperiencePO experience);

    /**
     * 更新工作經歷
     */
    int update(WorkExperiencePO experience);

    /**
     * 刪除工作經歷
     */
    int deleteById(@Param("id") String id);

    /**
     * 根據員工 ID 刪除所有工作經歷
     */
    int deleteByEmployeeId(@Param("employeeId") String employeeId);

    /**
     * 檢查工作經歷 ID 是否存在
     */
    boolean existsById(@Param("id") String id);
}
