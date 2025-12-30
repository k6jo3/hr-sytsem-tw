package com.company.hrms.attendance.domain.repository;

import java.util.Optional;

import com.company.hrms.attendance.domain.model.aggregate.AnnualLeavePolicy;
import com.company.hrms.attendance.domain.model.valueobject.PolicyId;

public interface IAnnualLeavePolicyRepository {
    void save(AnnualLeavePolicy policy);

    Optional<AnnualLeavePolicy> findById(PolicyId id);
}
