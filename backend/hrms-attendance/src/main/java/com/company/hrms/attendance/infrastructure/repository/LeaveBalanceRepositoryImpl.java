package com.company.hrms.attendance.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.attendance.domain.model.aggregate.LeaveBalance;
import com.company.hrms.attendance.domain.model.valueobject.BalanceId;
import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;
import com.company.hrms.attendance.domain.repository.ILeaveBalanceRepository;
import com.company.hrms.attendance.infrastructure.dao.LeaveBalanceDAO;
import com.company.hrms.attendance.infrastructure.po.LeaveBalancePO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LeaveBalanceRepositoryImpl implements ILeaveBalanceRepository {

    private final LeaveBalanceDAO leaveBalanceDAO;

    @Override
    public Optional<LeaveBalance> findById(BalanceId id) {
        return leaveBalanceDAO.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeIdAndYear(String employeeId, LeaveTypeId leaveTypeId,
            int year) {
        return leaveBalanceDAO.findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId.getValue(), year)
                .map(this::toDomain);
    }

    @Override
    public List<LeaveBalance> findByEmployeeIdAndYear(String employeeId, int year) {
        return leaveBalanceDAO.findByEmployeeIdAndYear(employeeId, year).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(LeaveBalance balance) {
        LeaveBalancePO po = toPO(balance);
        if (leaveBalanceDAO.findById(po.getId()).isPresent()) {
            po.setUpdatedAt(LocalDateTime.now());
            leaveBalanceDAO.update(po);
        } else {
            po.setCreatedAt(LocalDateTime.now());
            po.setUpdatedAt(LocalDateTime.now());
            leaveBalanceDAO.insert(po);
        }
    }

    @Override
    public void delete(BalanceId id) {
        leaveBalanceDAO.deleteById(id.getValue());
    }

    private LeaveBalance toDomain(LeaveBalancePO po) {
        return LeaveBalance.reconstitute(
                new BalanceId(po.getId()),
                po.getEmployeeId(),
                new LeaveTypeId(po.getLeaveTypeId()),
                po.getYear(),
                po.getTotalDays(),
                po.getUsedDays(),
                po.getCarryOverDays(),
                po.getExpiryDate());
    }

    private LeaveBalancePO toPO(LeaveBalance balance) {
        LeaveBalancePO po = new LeaveBalancePO();
        po.setId(balance.getId().getValue());
        po.setEmployeeId(balance.getEmployeeId());
        po.setLeaveTypeId(balance.getLeaveTypeId().getValue());
        po.setYear(balance.getYear());
        po.setTotalDays(balance.getTotalDays());
        po.setUsedDays(balance.getUsedDays());
        po.setCarryOverDays(balance.getCarryOverDays());
        po.setExpiryDate(balance.getExpiryDate());
        return po;
    }
}
