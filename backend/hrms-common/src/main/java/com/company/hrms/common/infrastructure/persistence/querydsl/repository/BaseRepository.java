package com.company.hrms.common.infrastructure.persistence.querydsl.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 基礎倉庫實作 (Legacy)
 * 整合 UltimateQueryEngine 與 AggregateQueryEngine
 * 實作所有基礎介面，保持向後相容性
 * 
 * <p>
 * 建議新開發的功能根據需求繼承:
 * <ul>
 * <li>{@link QueryBaseRepository} - 僅查詢</li>
 * <li>{@link CommandBaseRepository} - 查詢 + 單筆 CRUD</li>
 * <li>{@link CommandBatchBaseRepository} - 查詢 + 單筆 CRUD + 批次操作</li>
 * </ul>
 * </p>
 *
 * @param <T>  實體類型
 * @param <ID> 主鍵類型
 */
public abstract class BaseRepository<T, ID> extends CommandBatchBaseRepository<T, ID> {

    protected BaseRepository(JPAQueryFactory factory, Class<T> clazz) {
        super(factory, clazz);
    }

    // 所有方法已在父類別實作

    // IAggregateRepository 的實作已在 QueryBaseRepository 中完成
    // 但因為 Java 繼承的一些限制，如果有需要覆寫或特殊的聚合邏輯可以在這裡處理
    // 目前保持簡單繼承即可
}
