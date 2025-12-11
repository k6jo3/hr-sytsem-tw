package com.company.hrms.iam.infrastructure.dao;

import com.company.hrms.iam.infrastructure.po.PermissionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Permission DAO
 * MyBatis Mapper 介面
 */
@Mapper
public interface PermissionDAO {

    /**
     * 根據 ID 查詢權限
     */
    PermissionPO selectById(@Param("permissionId") String permissionId);

    /**
     * 根據權限代碼查詢權限
     */
    PermissionPO selectByPermissionCode(@Param("permissionCode") String permissionCode);

    /**
     * 根據資源查詢權限列表
     */
    List<PermissionPO> selectByResource(@Param("resource") String resource);

    /**
     * 查詢所有權限
     */
    List<PermissionPO> selectAll();

    /**
     * 根據角色 ID 查詢權限列表
     */
    List<PermissionPO> selectByRoleId(@Param("roleId") String roleId);

    /**
     * 根據權限 ID 列表查詢權限
     */
    List<PermissionPO> selectByIds(@Param("ids") List<String> ids);

    /**
     * 新增權限
     */
    int insert(PermissionPO permission);

    /**
     * 更新權限
     */
    int update(PermissionPO permission);

    /**
     * 刪除權限
     */
    int deleteById(@Param("permissionId") String permissionId);

    /**
     * 檢查權限代碼是否存在
     */
    boolean existsByPermissionCode(@Param("permissionCode") String permissionCode);
}
