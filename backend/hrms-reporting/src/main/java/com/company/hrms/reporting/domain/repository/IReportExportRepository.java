package com.company.hrms.reporting.domain.repository;

import java.util.Optional;

import com.company.hrms.reporting.infrastructure.entity.ReportExportEntity;

/**
 * 報表匯出 Repository 介面
 */
public interface IReportExportRepository {

    /**
     * 儲存匯出記錄
     * 
     * @param entity 實體
     * @return 儲存後的實體
     */
    ReportExportEntity save(ReportExportEntity entity);

    /**
     * 查詢匯出記錄
     * 
     * @param id ID
     * @return Optional<實體>
     */
    Optional<ReportExportEntity> findById(String id);
}
