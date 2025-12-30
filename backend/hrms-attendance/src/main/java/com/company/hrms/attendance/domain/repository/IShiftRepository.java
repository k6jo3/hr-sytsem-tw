package com.company.hrms.attendance.domain.repository;

import java.util.List;
import java.util.Optional;

import com.company.hrms.attendance.domain.model.aggregate.Shift;
import com.company.hrms.attendance.domain.model.valueobject.ShiftId;

public interface IShiftRepository {
    void save(Shift shift);

    Optional<Shift> findById(ShiftId id);

    List<Shift> findAll();

    void delete(ShiftId id);
}
