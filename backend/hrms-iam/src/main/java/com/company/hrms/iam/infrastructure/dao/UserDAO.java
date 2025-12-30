package com.company.hrms.iam.infrastructure.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.company.hrms.iam.infrastructure.mapper.UserMapper;
import com.company.hrms.iam.infrastructure.po.UserPO;

/**
 * User DAO (Data Access Object)
 * 資料存取物件，封裝 Mapper 操作
 */
@Repository
public class UserDAO {

    private final UserMapper userMapper;

    public UserDAO(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 根據 ID 查詢使用者
     */
    public UserPO selectById(String userId) {
        return userMapper.selectById(userId);
    }

    /**
     * 根據使用者名稱查詢
     */
    public UserPO selectByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    /**
     * 根據 Email 查詢
     */
    public UserPO selectByEmail(String email) {
        return userMapper.selectByEmail(email);
    }

    /**
     * 根據狀態查詢
     */
    public List<UserPO> selectByStatus(String status) {
        return userMapper.selectByStatus(status);
    }

    /**
     * 查詢所有使用者
     */
    public List<UserPO> selectAll() {
        return userMapper.selectAll();
    }

    /**
     * 新增使用者
     */
    public void insert(UserPO userPO) {
        userMapper.insert(userPO);
    }

    /**
     * 更新使用者
     */
    public void update(UserPO userPO) {
        userMapper.update(userPO);
    }

    /**
     * 刪除使用者
     */
    public void deleteById(String userId) {
        userMapper.deleteById(userId);
    }

    /**
     * 檢查使用者名稱是否存在
     */
    public boolean existsByUsername(String username) {
        return userMapper.existsByUsername(username);
    }

    /**
     * 檢查 Email 是否存在
     */
    public boolean existsByEmail(String email) {
        return userMapper.existsByEmail(email);
    }

    /**
     * 刪除使用者的所有角色關聯
     */
    public void deleteUserRoles(String userId) {
        userMapper.deleteUserRoles(userId);
    }

    /**
     * 新增使用者角色關聯
     */
    public void insertUserRole(String userId, String roleId) {
        userMapper.insertUserRole(userId, roleId);
    }
}
