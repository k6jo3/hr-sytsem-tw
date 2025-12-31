package com.company.hrms.common.infrastructure.persistence.querydsl.repository;

import java.util.List;

/**
 * 批次命令倉庫介面
 * 提供批次寫入操作
 *
 * @param <T> 實體類型
 */
public interface ICommandBatchRepository<T> {

    /**
     * 批次儲存實體
     * 使用 JPA 標準方式，每 50 筆執行一次 flush
     *
     * @param entities 實體列表
     */
    void saveAll(List<T> entities);

    /**
     * 批次儲存實體 (Native SQL)
     * 使用 Native Multi-values Insert，效能提升 10 倍以上
     *
     * <p>注意：此方法需要 Entity 標註 @TableMeta</p>
     *
     * @param entities  實體列表
     * @param batchSize 每批次的筆數 (建議 1000)
     */
    void saveAllNative(List<T> entities, int batchSize);

    /**
     * 批次刪除實體
     *
     * @param entities 實體列表
     */
    void deleteAll(List<T> entities);
}
