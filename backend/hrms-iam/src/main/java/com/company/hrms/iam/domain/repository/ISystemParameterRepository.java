package com.company.hrms.iam.domain.repository;

import java.util.List;
import java.util.Optional;

import com.company.hrms.iam.domain.model.aggregate.SystemParameter;

/**
 * 系統參數 Repository 介面
 * 定義於 Domain 層，實作於 Infrastructure 層
 */
public interface ISystemParameterRepository {

    List<SystemParameter> findAll();

    Optional<SystemParameter> findByParamCode(String paramCode);

    void update(SystemParameter parameter);
}
