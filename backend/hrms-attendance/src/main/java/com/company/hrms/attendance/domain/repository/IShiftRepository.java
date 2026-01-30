package com.company.hrms.attendance.domain.repository;

import java.util.List;
import java.util.Optional;

import com.company.hrms.attendance.domain.model.aggregate.Shift;
import com.company.hrms.attendance.domain.model.valueobject.ShiftId;
import com.company.hrms.common.query.QueryGroup;

public interface IShiftRepository {
    void save(Shift shift);

    Optional<Shift> findById(ShiftId id);

    List<Shift> findAll();

    List<Shift> findByQuery(QueryGroup query);

    void delete(ShiftId id);
}
