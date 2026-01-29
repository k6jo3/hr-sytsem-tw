package com.company.hrms.reporting.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.reporting.domain.model.dashboard.Dashboard;
import com.company.hrms.reporting.domain.model.dashboard.DashboardId;

/**
 * Dashboard Repository 介面
 * 
 * @author SA Team
 * @since 2026-01-29
 */
public interface IDashboardRepository {

    /**
     * 儲存儀表板
     */
    Dashboard save(Dashboard dashboard);

    /**
     * 根據 ID 查詢儀表板
     */
    Optional<Dashboard> findById(DashboardId id);

    /**
     * 根據條件查詢儀表板（分頁）
     */
    Page<Dashboard> findPage(QueryGroup query, Pageable pageable);

    /**
     * 根據條件查詢單一儀表板
     */
    Optional<Dashboard> findOne(QueryGroup query);

    /**
     * 刪除儀表板
     */
    void delete(DashboardId id);

    /**
     * 檢查儀表板是否存在
     */
    boolean exists(DashboardId id);
}
