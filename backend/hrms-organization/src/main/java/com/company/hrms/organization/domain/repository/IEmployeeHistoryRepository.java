package com.company.hrms.organization.domain.repository;

import com.company.hrms.organization.domain.model.entity.EmployeeHistory;
import com.company.hrms.organization.domain.model.valueobject.EmployeeHistoryEventType;
import com.company.hrms.organization.domain.model.valueobject.HistoryId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 員工人事歷程 Repository 介面
 */
public interface IEmployeeHistoryRepository {

    /**
     * 依 ID 查詢
     * @param id 歷程 ID
     * @return 歷程
     */
    Optional<EmployeeHistory> findById(HistoryId id);

    /**
     * 依 ID 查詢
     * @param id 歷程 ID
     * @return 歷程
     */
    Optional<EmployeeHistory> findById(UUID id);

    /**
     * 依員工 ID 查詢歷程
     * @param employeeId 員工 ID
     * @return 歷程列表 (依生效日期降序)
     */
    List<EmployeeHistory> findByEmployeeId(UUID employeeId);

    /**
     * 依員工 ID 和事件類型查詢
     * @param employeeId 員工 ID
     * @param eventType 事件類型
     * @return 歷程列表
     */
    List<EmployeeHistory> findByEmployeeIdAndEventType(UUID employeeId, EmployeeHistoryEventType eventType);

    /**
     * 依日期範圍查詢
     * @param employeeId 員工 ID
     * @param startDate 開始日期
     * @param endDate 結束日期
     * @return 歷程列表
     */
    List<EmployeeHistory> findByEmployeeIdAndDateRange(UUID employeeId, LocalDate startDate, LocalDate endDate);

    /**
     * 儲存歷程
     * @param history 歷程
     */
    void save(EmployeeHistory history);
}
