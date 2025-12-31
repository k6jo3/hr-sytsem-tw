package com.company.hrms.common.infrastructure.persistence.querydsl.repository;

import com.company.hrms.common.query.GroupByClause;
import com.company.hrms.common.query.QueryGroup;
import com.querydsl.core.Tuple;

import java.util.List;

/**
 * 聚合查詢倉庫介面
 * 提供 GROUP BY、HAVING、聚合函數等統計查詢操作
 *
 * @param <T> 實體類型
 */
public interface IAggregateRepository<T> {

    /**
     * 執行聚合查詢，返回 Tuple 結果
     *
     * @param where   WHERE 條件
     * @param groupBy GROUP BY 子句
     * @return Tuple 列表
     */
    List<Tuple> aggregate(QueryGroup where, GroupByClause groupBy);

    /**
     * 執行聚合查詢，返回 DTO 結果
     *
     * @param where    WHERE 條件
     * @param groupBy  GROUP BY 子句
     * @param dtoClass DTO 類型
     * @param <R>      DTO 類型參數
     * @return DTO 列表
     */
    <R> List<R> aggregateToDto(QueryGroup where, GroupByClause groupBy, Class<R> dtoClass);
}
