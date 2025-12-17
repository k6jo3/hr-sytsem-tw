package com.company.hrms.organization.infrastructure.dao;

import com.company.hrms.organization.infrastructure.mapper.EmployeeHistoryMapper;
import com.company.hrms.organization.infrastructure.po.EmployeeHistoryPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 員工人事歷程 DAO
 */
@Repository
@RequiredArgsConstructor
public class EmployeeHistoryDAO {

    private final EmployeeHistoryMapper historyMapper;

    public Optional<EmployeeHistoryPO> findById(String id) {
        return Optional.ofNullable(historyMapper.selectById(id));
    }

    public List<EmployeeHistoryPO> findByEmployeeId(String employeeId) {
        return historyMapper.selectByEmployeeId(employeeId);
    }

    public List<EmployeeHistoryPO> findByEventType(String eventType) {
        return historyMapper.selectByEventType(eventType);
    }

    public List<EmployeeHistoryPO> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return historyMapper.selectByDateRange(startDate, endDate);
    }

    public void insert(EmployeeHistoryPO history) {
        historyMapper.insert(history);
    }

    public void update(EmployeeHistoryPO history) {
        historyMapper.update(history);
    }

    public void deleteById(String id) {
        historyMapper.deleteById(id);
    }

    public void deleteByEmployeeId(String employeeId) {
        historyMapper.deleteByEmployeeId(employeeId);
    }
}
