package com.company.hrms.iam.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.iam.domain.model.aggregate.User;
import com.company.hrms.iam.domain.model.valueobject.Email;
import com.company.hrms.iam.domain.model.valueobject.UserId;
import com.company.hrms.iam.domain.model.valueobject.UserStatus;

/**
 * User Repository 介面
 * 定義於 Domain 層，實作於 Infrastructure 層
 * 
 * <p>
 * 遵循依賴反轉原則 (DIP)：Domain 層不依賴 Infrastructure 層
 * </p>
 */
public interface IUserRepository {

    /**
     * 根據 ID 查找使用者
     * 
     * @param id 使用者 ID
     * @return 使用者 Optional
     */
    Optional<User> findById(UserId id);

    /**
     * 根據使用者名稱查找使用者
     * 
     * @param username 使用者名稱
     * @return 使用者 Optional
     */
    Optional<User> findByUsername(String username);

    /**
     * 根據 Email 查找使用者
     * 
     * @param email Email
     * @return 使用者 Optional
     */
    Optional<User> findByEmail(Email email);

    /**
     * 根據狀態查找使用者列表
     * 
     * @param status 使用者狀態
     * @return 使用者列表
     */
    List<User> findByStatus(UserStatus status);

    /**
     * 查詢所有使用者
     * 
     * @return 使用者列表
     */
    List<User> findAll();

    /**
     * 動態分頁查詢
     */
    Page<User> findPage(QueryGroup query, Pageable pageable);

    /**
     * 動態查詢所有符合條件的人員
     */
    List<User> findAll(QueryGroup query);

    /**
     * 動態查詢符合條件的人數
     */
    long count(QueryGroup query);

    /**
     * 儲存使用者
     * 
     * @param user 使用者
     */
    void save(User user);

    /**
     * 更新使用者
     * <p>
     * 注意：此方法用於更新已存在的使用者資料
     * </p>
     * 
     * @param user 使用者
     */
    void update(User user);

    /**
     * 刪除使用者
     * 
     * @param id 使用者 ID
     */
    void deleteById(UserId id);

    /**
     * 根據員工 ID 查找使用者
     *
     * @param employeeId 員工 ID
     * @return 使用者 Optional
     */
    Optional<User> findByEmployeeId(String employeeId);

    /**
     * 檢查使用者名稱是否存在
     *
     * @param username 使用者名稱
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 檢查 Email 是否存在
     * 
     * @param email Email
     * @return 是否存在
     */
    boolean existsByEmail(Email email);

    /**
     * 更新使用者的角色列表
     * 
     * @param userId  使用者 ID
     * @param roleIds 角色 ID 列表
     */
    void updateUserRoles(UserId userId, java.util.List<String> roleIds);
}
