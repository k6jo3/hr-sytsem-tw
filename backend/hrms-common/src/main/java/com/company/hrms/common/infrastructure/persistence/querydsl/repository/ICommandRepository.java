package com.company.hrms.common.infrastructure.persistence.querydsl.repository;

import java.util.Optional;

/**
 * 命令倉庫介面
 * 提供基本的 CRUD 操作
 *
 * @param <T>  實體類型
 * @param <ID> 主鍵類型
 */
public interface ICommandRepository<T, ID> {

    /**
     * 根據 ID 查找實體
     *
     * @param id 主鍵
     * @return Optional 包裝的結果
     */
    Optional<T> findById(ID id);

    /**
     * 儲存實體
     *
     * @param entity 實體
     * @return 儲存後的實體
     */
    T save(T entity);

    /**
     * 更新實體
     *
     * @param entity 實體
     * @return 更新後的實體
     */
    T update(T entity);

    /**
     * 刪除實體
     *
     * @param entity 實體
     */
    void delete(T entity);

    /**
     * 根據 ID 刪除實體
     *
     * @param id 主鍵
     */
    void deleteById(ID id);

    /**
     * 檢查 ID 是否存在
     *
     * @param id 主鍵
     * @return 是否存在
     */
    boolean existsById(ID id);
}
