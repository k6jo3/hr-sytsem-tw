package com.company.hrms.workflow.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.company.hrms.workflow.domain.model.aggregate.UserDelegation;
import com.company.hrms.workflow.domain.model.valueobject.UserDelegationId;

public interface IUserDelegationRepository {

    UserDelegation save(UserDelegation delegation);

    Optional<UserDelegation> findById(UserDelegationId id);

    List<UserDelegation> findActiveByDelegator(String delegatorId, LocalDate date);

    List<UserDelegation> findAll();
}
