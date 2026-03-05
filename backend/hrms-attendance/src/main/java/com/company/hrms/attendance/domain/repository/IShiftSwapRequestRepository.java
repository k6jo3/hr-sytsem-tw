package com.company.hrms.attendance.domain.repository;

import java.util.List;
import java.util.Optional;

import com.company.hrms.attendance.domain.model.aggregate.ShiftSwapRequest;
import com.company.hrms.attendance.domain.model.valueobject.SwapRequestId;
import com.company.hrms.common.query.QueryGroup;

/**
 * 換班申請 Repository
 */
public interface IShiftSwapRequestRepository {

    void save(ShiftSwapRequest request);

    Optional<ShiftSwapRequest> findById(SwapRequestId id);

    List<ShiftSwapRequest> findByRequesterId(String requesterId);

    List<ShiftSwapRequest> findByCounterpartId(String counterpartId);

    List<ShiftSwapRequest> findByQuery(QueryGroup query);
}
