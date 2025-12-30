package com.company.hrms.common.infrastructure.persistence.querydsl.repository;

import com.company.hrms.common.query.QueryGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 查詢倉庫介面
 * 提供基於 QueryGroup 的查詢操作
 *
 * @param <T>  實體類型
 * @param <ID> 主鍵類型
 */
public interface IQueryRepository<T, ID> {

    /**
     * 分頁查詢
     *
     * @param group    查詢條件群組
     * @param pageable 分頁參數
     * @return 分頁結果
     */
    Page<T> findPage(QueryGroup group, Pageable pageable);

    /**
     * 分頁查詢 (使用 DISTINCT 去重)
     * 適用於 LEFT JOIN 造成資料重複的情況
     *
     * @param group    查詢條件群組
     * @param pageable 分頁參數
     * @return 分頁結果
     */
    Page<T> findPageDistinct(QueryGroup group, Pageable pageable);

    /**
     * 查詢單筆資料
     *
     * @param group 查詢條件群組
     * @return Optional 包裝的結果
     */
    Optional<T> findOne(QueryGroup group);

    /**
     * 查詢所有符合條件的資料
     *
     * @param group 查詢條件群組
     * @return 結果列表
     */
    List<T> findAll(QueryGroup group);

    /**
     * 查詢符合條件的資料筆數
     *
     * @param group 查詢條件群組
     * @return 資料筆數
     */
    long count(QueryGroup group);

    /**
     * 檢查是否存在符合條件的資料
     *
     * @param group 查詢條件群組
     * @return 是否存在
     */
    boolean exists(QueryGroup group);
}
