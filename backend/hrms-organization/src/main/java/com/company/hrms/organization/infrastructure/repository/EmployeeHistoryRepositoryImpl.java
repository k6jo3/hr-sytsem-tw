package com.company.hrms.organization.infrastructure.repository;

import com.company.hrms.organization.domain.model.entity.EmployeeHistory;
import com.company.hrms.organization.domain.model.valueobject.*;
import com.company.hrms.organization.domain.repository.IEmployeeHistoryRepository;
import com.company.hrms.organization.infrastructure.dao.EmployeeHistoryDAO;
import com.company.hrms.organization.infrastructure.po.EmployeeHistoryPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 員工人事歷程倉儲實作
 */
@Repository
@RequiredArgsConstructor
public class EmployeeHistoryRepositoryImpl implements IEmployeeHistoryRepository {

    private final EmployeeHistoryDAO historyDAO;

    @Override
    public Optional<EmployeeHistory> findById(HistoryId id) {
        return historyDAO.findById(id.getValue())
                .map(this::toDomain);
    }

    @Override
    public List<EmployeeHistory> findByEmployeeId(EmployeeId employeeId) {
        return historyDAO.findByEmployeeId(employeeId.getValue()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(EmployeeHistory history) {
        EmployeeHistoryPO po = toPO(history);
        po.setCreatedAt(LocalDateTime.now());
        historyDAO.insert(po);
    }

    @Override
    public void delete(HistoryId id) {
        historyDAO.deleteById(id.getValue());
    }

    @Override
    public void deleteByEmployeeId(EmployeeId employeeId) {
        historyDAO.deleteByEmployeeId(employeeId.getValue());
    }

    private EmployeeHistory toDomain(EmployeeHistoryPO po) {
        return EmployeeHistory.reconstitute(
                new HistoryId(po.getId()),
                new EmployeeId(po.getEmployeeId()),
                EmployeeHistoryEventType.valueOf(po.getEventType()),
                po.getEventDate(),
                po.getDescription(),
                po.getOldValue(),
                po.getNewValue(),
                po.getRemarks(),
                po.getCreatedAt()
        );
    }

    private EmployeeHistoryPO toPO(EmployeeHistory history) {
        EmployeeHistoryPO po = new EmployeeHistoryPO();
        po.setId(history.getId().getValue());
        po.setEmployeeId(history.getEmployeeId().getValue());
        po.setEventType(history.getEventType().name());
        po.setEventDate(history.getEventDate());
        po.setDescription(history.getDescription());
        po.setOldValue(history.getOldValue());
        po.setNewValue(history.getNewValue());
        po.setRemarks(history.getRemarks());
        return po;
    }
}
