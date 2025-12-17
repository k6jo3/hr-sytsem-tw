package com.company.hrms.organization.infrastructure.mapper;

import com.company.hrms.organization.infrastructure.po.DepartmentPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部門 MyBatis Mapper
 */
@Mapper
public interface DepartmentMapper {

    /**
     * 根據 ID 查詢部門
     */
    DepartmentPO selectById(@Param("id") String id);

    /**
     * 根據部門代碼查詢
     */
    DepartmentPO selectByCode(@Param("code") String code);

    /**
     * 根據組織 ID 查詢部門
     */
    List<DepartmentPO> selectByOrganizationId(@Param("organizationId") String organizationId);

    /**
     * 根據父部門 ID 查詢子部門
     */
    List<DepartmentPO> selectByParentId(@Param("parentId") String parentId);

    /**
     * 查詢根部門 (無父部門)
     */
    List<DepartmentPO> selectRootDepartments(@Param("organizationId") String organizationId);

    /**
     * 根據主管 ID 查詢部門
     */
    List<DepartmentPO> selectByManagerId(@Param("managerId") String managerId);

    /**
     * 根據狀態查詢部門
     */
    List<DepartmentPO> selectByStatus(@Param("status") String status);

    /**
     * 新增部門
     */
    int insert(DepartmentPO department);

    /**
     * 更新部門
     */
    int update(DepartmentPO department);

    /**
     * 刪除部門 (軟刪除)
     */
    int deleteById(@Param("id") String id);

    /**
     * 檢查部門代碼是否存在
     */
    boolean existsByCode(@Param("code") String code);

    /**
     * 檢查部門 ID 是否存在
     */
    boolean existsById(@Param("id") String id);

    /**
     * 計算子部門數量
     */
    int countByParentId(@Param("parentId") String parentId);
}
