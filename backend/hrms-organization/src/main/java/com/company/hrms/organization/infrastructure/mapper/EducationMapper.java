package com.company.hrms.organization.infrastructure.mapper;

import com.company.hrms.organization.infrastructure.po.EducationPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 學歷 MyBatis Mapper
 */
@Mapper
public interface EducationMapper {

    /**
     * 根據 ID 查詢學歷
     */
    EducationPO selectById(@Param("id") String id);

    /**
     * 根據員工 ID 查詢學歷
     */
    List<EducationPO> selectByEmployeeId(@Param("employeeId") String employeeId);

    /**
     * 根據學歷等級查詢
     */
    List<EducationPO> selectByDegree(@Param("degree") String degree);

    /**
     * 新增學歷
     */
    int insert(EducationPO education);

    /**
     * 更新學歷
     */
    int update(EducationPO education);

    /**
     * 刪除學歷
     */
    int deleteById(@Param("id") String id);

    /**
     * 根據員工 ID 刪除所有學歷
     */
    int deleteByEmployeeId(@Param("employeeId") String employeeId);

    /**
     * 檢查學歷 ID 是否存在
     */
    boolean existsById(@Param("id") String id);
}
