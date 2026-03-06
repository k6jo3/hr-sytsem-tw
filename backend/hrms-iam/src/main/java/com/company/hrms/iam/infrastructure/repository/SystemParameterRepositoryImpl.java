package com.company.hrms.iam.infrastructure.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.company.hrms.iam.domain.model.aggregate.SystemParameter;
import com.company.hrms.iam.domain.repository.ISystemParameterRepository;
import com.company.hrms.iam.infrastructure.dao.SystemParameterDAO;
import com.company.hrms.iam.infrastructure.po.SystemParameterPO;

/**
 * 系統參數 Repository 實作
 * 負責 PO 與 Domain Object 之間的轉換
 */
@Component
public class SystemParameterRepositoryImpl implements ISystemParameterRepository {

    private final SystemParameterDAO dao;

    public SystemParameterRepositoryImpl(SystemParameterDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<SystemParameter> findAll() {
        return dao.selectAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SystemParameter> findByParamCode(String paramCode) {
        SystemParameterPO po = dao.selectByParamCode(paramCode);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public void update(SystemParameter parameter) {
        SystemParameterPO po = SystemParameterPO.builder()
                .paramCode(parameter.getParamCode())
                .paramValue(parameter.getParamValue())
                .updatedAt(Timestamp.valueOf(parameter.getUpdatedAt()))
                .updatedBy(parameter.getUpdatedBy())
                .build();
        dao.updateValue(po);
    }

    private SystemParameter toDomain(SystemParameterPO po) {
        return SystemParameter.builder()
                .id(po.getId())
                .paramCode(po.getParamCode())
                .paramName(po.getParamName())
                .paramValue(po.getParamValue())
                .paramType(po.getParamType())
                .module(po.getModule())
                .category(po.getCategory())
                .description(po.getDescription())
                .defaultValue(po.getDefaultValue())
                .tenantId(po.getTenantId())
                .isEncrypted(po.getIsEncrypted() != null && po.getIsEncrypted())
                .updatedAt(po.getUpdatedAt() != null ? po.getUpdatedAt().toLocalDateTime() : null)
                .updatedBy(po.getUpdatedBy())
                .build();
    }
}
