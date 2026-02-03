package com.company.hrms.organization.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.organization.domain.model.entity.EmployeeHistory;
import com.company.hrms.organization.domain.model.valueobject.EmployeeHistoryEventType;
import com.company.hrms.organization.domain.model.valueobject.HistoryId;
import com.company.hrms.organization.domain.repository.IEmployeeHistoryRepository;
import com.company.hrms.organization.infrastructure.dao.EmployeeHistoryDAO;
import com.company.hrms.organization.infrastructure.po.EmployeeHistoryPO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 員工人事歷程 Repository 實作
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class EmployeeHistoryRepositoryImpl implements IEmployeeHistoryRepository {

    private final EmployeeHistoryDAO historyDAO;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<EmployeeHistory> findById(HistoryId id) {
        return findById(id.getValue());
    }

    @Override
    public Optional<EmployeeHistory> findById(UUID id) {
        return historyDAO.findById(id.toString()).map(this::toAggregate);
    }

    @Override
    public List<EmployeeHistory> findByEmployeeId(UUID employeeId) {
        return historyDAO.findByEmployeeId(employeeId.toString()).stream()
                .map(this::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeHistory> findByEmployeeIdAndEventType(UUID employeeId, EmployeeHistoryEventType eventType) {
        // 這裡可以使用 Query Engine，目前先以簡單方式實作
        return historyDAO.findByEmployeeId(employeeId.toString()).stream()
                .map(this::toAggregate)
                .filter(h -> h.getEventType() == eventType)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeHistory> findByEmployeeIdAndDateRange(UUID employeeId, LocalDate startDate, LocalDate endDate) {
        return historyDAO.findByDateRange(startDate, endDate).stream()
                .filter(po -> po.getEmployeeId().equals(employeeId))
                .map(this::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public void save(EmployeeHistory history) {
        EmployeeHistoryPO po = toPO(history);
        if (historyDAO.findById(po.getHistoryId().toString()).isPresent()) {
            historyDAO.update(po);
        } else {
            historyDAO.insert(po);
        }
    }

    // ==================== Mappings ====================

    private EmployeeHistory toAggregate(EmployeeHistoryPO po) {
        Map<String, Object> oldValue = null;
        Map<String, Object> newValue = null;
        try {
            if (po.getOldValue() != null && !po.getOldValue().isBlank()) {
                oldValue = objectMapper.readValue(po.getOldValue(), new TypeReference<Map<String, Object>>() {
                });
            }
            if (po.getNewValue() != null && !po.getNewValue().isBlank()) {
                newValue = objectMapper.readValue(po.getNewValue(), new TypeReference<Map<String, Object>>() {
                });
            }
        } catch (JsonProcessingException e) {
            log.error("Error parsing history JSON data", e);
        }

        return EmployeeHistory.reconstitute(
                new HistoryId(po.getHistoryId()),
                po.getEmployeeId(),
                EmployeeHistoryEventType.valueOf(po.getEventType()),
                po.getEffectiveDate(),
                oldValue,
                newValue,
                po.getReason(),
                po.getApprovedBy(),
                po.getCreatedAt());
    }

    private EmployeeHistoryPO toPO(EmployeeHistory history) {
        EmployeeHistoryPO po = new EmployeeHistoryPO();
        po.setHistoryId(history.getId().getValue());
        po.setEmployeeId(history.getEmployeeId());
        po.setEventType(history.getEventType().name());
        po.setEffectiveDate(history.getEffectiveDate());
        po.setReason(history.getReason());
        po.setApprovedBy(history.getApprovedBy());
        po.setCreatedAt(history.getCreatedAt());

        try {
            if (history.getOldValue() != null) {
                po.setOldValue(objectMapper.writeValueAsString(history.getOldValue()));
            }
            if (history.getNewValue() != null) {
                po.setNewValue(objectMapper.writeValueAsString(history.getNewValue()));
            }
        } catch (JsonProcessingException e) {
            log.error("Error serializing history JSON data", e);
        }

        return po;
    }
}
