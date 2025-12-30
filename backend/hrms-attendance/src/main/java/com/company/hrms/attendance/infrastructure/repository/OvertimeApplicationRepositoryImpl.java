package com.company.hrms.attendance.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.attendance.domain.model.aggregate.OvertimeApplication;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationStatus;
import com.company.hrms.attendance.domain.model.valueobject.OvertimeId;
import com.company.hrms.attendance.domain.model.valueobject.OvertimeType;
import com.company.hrms.attendance.domain.repository.IOvertimeApplicationRepository;
import com.company.hrms.attendance.infrastructure.dao.OvertimeApplicationDAO;
import com.company.hrms.attendance.infrastructure.po.OvertimeApplicationPO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OvertimeApplicationRepositoryImpl implements IOvertimeApplicationRepository {

    private final OvertimeApplicationDAO overtimeApplicationDAO;

    @Override
    public Optional<OvertimeApplication> findById(OvertimeId id) {
        return overtimeApplicationDAO.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public List<OvertimeApplication> findByEmployeeId(String employeeId) {
        return overtimeApplicationDAO.findByEmployeeId(employeeId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<OvertimeApplication> findByEmployeeIdAndMonth(String employeeId, int year, int month) {
        return overtimeApplicationDAO.findByEmployeeIdAndMonth(employeeId, year, month).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(OvertimeApplication application) {
        OvertimeApplicationPO po = toPO(application);
        if (overtimeApplicationDAO.findById(po.getId()).isPresent()) {
            po.setUpdatedAt(LocalDateTime.now());
            overtimeApplicationDAO.update(po);
        } else {
            po.setCreatedAt(LocalDateTime.now());
            po.setUpdatedAt(LocalDateTime.now());
            overtimeApplicationDAO.insert(po);
        }
    }

    @Override
    public void delete(OvertimeId id) {
        overtimeApplicationDAO.deleteById(id.getValue());
    }

    private OvertimeApplication toDomain(OvertimeApplicationPO po) {
        return OvertimeApplication.reconstitute(
                new OvertimeId(po.getId()),
                po.getEmployeeId(),
                po.getDate(),
                po.getHours(),
                OvertimeType.valueOf(po.getOvertimeType()),
                po.getReason(),
                ApplicationStatus.valueOf(po.getStatus()),
                po.getRejectionReason());
    }

    private OvertimeApplicationPO toPO(OvertimeApplication application) {
        OvertimeApplicationPO po = new OvertimeApplicationPO();
        po.setId(application.getId().getValue());
        po.setEmployeeId(application.getEmployeeId());
        po.setDate(application.getDate());
        po.setHours(application.getHours());
        po.setStatus(application.getStatus().name());
        po.setReason(application.getReason());
        po.setOvertimeType(application.getOvertimeType().name());
        po.setRejectionReason(application.getRejectionReason());
        return po;
    }
}
