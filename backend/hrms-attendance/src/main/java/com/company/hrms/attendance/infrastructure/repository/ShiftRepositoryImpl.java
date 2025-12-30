package com.company.hrms.attendance.infrastructure.repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.attendance.domain.model.aggregate.Shift;
import com.company.hrms.attendance.domain.model.valueobject.ShiftId;
import com.company.hrms.attendance.domain.model.valueobject.ShiftType;
import com.company.hrms.attendance.domain.repository.IShiftRepository;
import com.company.hrms.attendance.infrastructure.dao.ShiftDAO;
import com.company.hrms.attendance.infrastructure.po.ShiftPO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShiftRepositoryImpl implements IShiftRepository {

    private final ShiftDAO shiftDAO;

    @Override
    public Optional<Shift> findById(ShiftId id) {
        return shiftDAO.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public List<Shift> findAll() {
        return shiftDAO.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Shift shift) {
        ShiftPO po = toPO(shift);
        if (shiftDAO.findById(po.getId()).isPresent()) {
            po.setUpdatedAt(LocalDateTime.now());
            // createdBy/At should keep original? DAO logic implies overwrite or fetch
            // first.
            // Simplified: Just update fields.
            // Better: use update specific method which doesn't touch created_at.
            // My Mapper update method updates updated_at.
            shiftDAO.update(po);
        } else {
            po.setCreatedAt(LocalDateTime.now());
            po.setUpdatedAt(LocalDateTime.now());
            shiftDAO.insert(po);
        }
    }

    @Override
    public void delete(ShiftId id) {
        shiftDAO.deleteById(id.getValue());
    }

    private Shift toDomain(ShiftPO po) {
        return Shift.reconstitute(
                new ShiftId(po.getId()),
                po.getName(),
                ShiftType.valueOf(po.getType()),
                LocalTime.parse(po.getStartTime()),
                LocalTime.parse(po.getEndTime()),
                po.getBreakStartTime() != null ? LocalTime.parse(po.getBreakStartTime()) : null,
                po.getBreakEndTime() != null ? LocalTime.parse(po.getBreakEndTime()) : null,
                po.getLateToleranceMinutes() != null ? po.getLateToleranceMinutes() : 0,
                po.getEarlyLeaveToleranceMinutes() != null ? po.getEarlyLeaveToleranceMinutes() : 0);
    }

    private ShiftPO toPO(Shift shift) {
        ShiftPO po = new ShiftPO();
        po.setId(shift.getId().getValue());
        po.setName(shift.getName());
        po.setType(shift.getType().name());
        po.setStartTime(shift.getWorkStartTime().toString());
        po.setEndTime(shift.getWorkEndTime().toString());
        if (shift.getBreakStartTime() != null) {
            po.setBreakStartTime(shift.getBreakStartTime().toString());
        }
        if (shift.getBreakEndTime() != null) {
            po.setBreakEndTime(shift.getBreakEndTime().toString());
        }
        po.setLateToleranceMinutes(shift.getLateToleranceMinutes());
        po.setEarlyLeaveToleranceMinutes(shift.getEarlyLeaveToleranceMinutes());
        // Auditing handled in save
        return po;
    }
}
