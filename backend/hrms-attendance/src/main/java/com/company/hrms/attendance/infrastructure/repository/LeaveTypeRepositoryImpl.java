package com.company.hrms.attendance.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.attendance.domain.model.aggregate.LeaveType;
import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;
import com.company.hrms.attendance.domain.model.valueobject.LeaveUnit;
import com.company.hrms.attendance.domain.repository.ILeaveTypeRepository;
import com.company.hrms.attendance.infrastructure.dao.LeaveTypeDAO;
import com.company.hrms.attendance.infrastructure.po.LeaveTypePO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LeaveTypeRepositoryImpl implements ILeaveTypeRepository {

    private final LeaveTypeDAO leaveTypeDAO;

    @Override
    public Optional<LeaveType> findById(LeaveTypeId id) {
        return leaveTypeDAO.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public Optional<LeaveType> findByCode(String code) {
        return leaveTypeDAO.findByCode(code).map(this::toDomain);
    }

    @Override
    public List<LeaveType> findAll() {
        return leaveTypeDAO.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(LeaveType leaveType) {
        LeaveTypePO po = toPO(leaveType);
        if (leaveTypeDAO.findById(po.getId()).isPresent()) {
            po.setUpdatedAt(LocalDateTime.now());
            leaveTypeDAO.update(po);
        } else {
            po.setCreatedAt(LocalDateTime.now());
            po.setUpdatedAt(LocalDateTime.now());
            leaveTypeDAO.insert(po);
        }
    }

    @Override
    public void delete(LeaveTypeId id) {
        leaveTypeDAO.deleteById(id.getValue());
    }

    private LeaveType toDomain(LeaveTypePO po) {
        return LeaveType.reconstitute(
                new LeaveTypeId(po.getId()),
                po.getName(),
                po.getCode(),
                LeaveUnit.valueOf(po.getUnit()),
                po.getIsPaid() != null ? po.getIsPaid() : false);
    }

    private LeaveTypePO toPO(LeaveType leaveType) {
        LeaveTypePO po = new LeaveTypePO();
        po.setId(leaveType.getId().getValue());
        po.setName(leaveType.getName());
        po.setCode(leaveType.getCode());
        po.setUnit(leaveType.getUnit().name());
        po.setIsPaid(leaveType.isPaid());
        return po;
    }
}
