package com.company.hrms.organization.infrastructure.mapper;

import com.company.hrms.organization.infrastructure.po.OrganizationPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 組織 MyBatis Mapper
 */
@Mapper
public interface OrganizationMapper {

    /**
     * 根據 ID 查詢組織
     */
    OrganizationPO selectById(@Param("id") String id);

    /**
     * 根據組織代碼查詢
     */
    OrganizationPO selectByCode(@Param("code") String code);

    /**
     * 查詢所有組織
     */
    List<OrganizationPO> selectAll();

    /**
     * 根據父組織 ID 查詢子組織
     */
    List<OrganizationPO> selectByParentId(@Param("parentId") String parentId);

    /**
     * 根據狀態查詢組織
     */
    List<OrganizationPO> selectByStatus(@Param("status") String status);

    /**
     * 新增組織
     */
    int insert(OrganizationPO organization);

    /**
     * 更新組織
     */
    int update(OrganizationPO organization);

    /**
     * 刪除組織 (軟刪除)
     */
    int deleteById(@Param("id") String id);

    /**
     * 檢查組織代碼是否存在
     */
    boolean existsByCode(@Param("code") String code);

    /**
     * 檢查組織 ID 是否存在
     */
    boolean existsById(@Param("id") String id);
}
