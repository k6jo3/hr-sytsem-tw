package com.company.hrms.attendance.domain.repository;

import java.util.List;
import java.util.Optional;

import com.company.hrms.attendance.domain.model.aggregate.RotationPattern;
import com.company.hrms.attendance.domain.model.valueobject.RotationPatternId;
import com.company.hrms.common.query.QueryGroup;

/**
 * 輪班模式 Repository
 */
public interface IRotationPatternRepository {

    void save(RotationPattern pattern);

    Optional<RotationPattern> findById(RotationPatternId id);

    List<RotationPattern> findAll();

    List<RotationPattern> findByQuery(QueryGroup query);

    void delete(RotationPatternId id);
}
