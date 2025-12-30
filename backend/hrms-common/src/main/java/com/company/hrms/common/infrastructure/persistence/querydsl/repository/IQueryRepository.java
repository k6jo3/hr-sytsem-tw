package com.company.hrms.common.infrastructure.persistence.querydsl.repository;

import com.company.hrms.common.query.Condition;
import com.company.hrms.common.query.QueryGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 查詢倉庫介面
 * 提供基於 QueryGroup 或 Condition 的查詢操作
 *
 * <p>支援兩種使用方式:</p>
 *
 * <h3>方式一：使用 QueryGroup (手動建構)</h3>
 * <pre>
 * QueryGroup group = QueryBuilder.where()
 *     .eq("status", "ACTIVE")
 *     .like("name", "John")
 *     .build();
 * Page&lt;Employee&gt; result = repository.findPage(group, pageable);
 * </pre>
 *
 * <h3>方式二：使用 Condition (註解式宣告)</h3>
 * <pre>
 * // 定義條件物件
 * public class EmployeeSearchCondition {
 *     &#64;EQ
 *     private String status;
 *
 *     &#64;LIKE
 *     private String name;
 * }
 *
 * // 使用
 * EmployeeSearchCondition cond = new EmployeeSearchCondition();
 * cond.setStatus("ACTIVE");
 * cond.setName("John");
 *
 * Condition&lt;EmployeeSearchCondition&gt; condition = Condition.of(cond).page(0).size(20);
 * Page&lt;Employee&gt; result = repository.findPage(condition);
 * </pre>
 *
 * @param <T>  實體類型
 * @param <ID> 主鍵類型
 */
public interface IQueryRepository<T, ID> {

    // ==================== Condition 方式 (推薦) ====================

    /**
     * 分頁查詢 (使用 Condition)
     *
     * @param condition 條件包裝器 (包含查詢條件與分頁參數)
     * @param <C>       條件 DTO 類型
     * @return 分頁結果
     */
    <C> Page<T> findPage(Condition<C> condition);

    /**
     * 查詢所有符合條件的資料 (使用 Condition)
     *
     * @param condition 條件包裝器
     * @param <C>       條件 DTO 類型
     * @return 結果列表
     */
    <C> List<T> findAll(Condition<C> condition);

    /**
     * 查詢單筆資料 (使用 Condition)
     *
     * @param condition 條件包裝器
     * @param <C>       條件 DTO 類型
     * @return Optional 包裝的結果
     */
    <C> Optional<T> findOne(Condition<C> condition);

    // ==================== QueryGroup 方式 ====================

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
