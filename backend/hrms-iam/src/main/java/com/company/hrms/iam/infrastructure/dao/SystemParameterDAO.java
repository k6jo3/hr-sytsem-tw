package com.company.hrms.iam.infrastructure.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.company.hrms.iam.infrastructure.mapper.SystemParameterMapper;
import com.company.hrms.iam.infrastructure.po.SystemParameterPO;

/**
 * 系統參數 DAO
 * 封裝 Mapper 操作
 */
@Repository
public class SystemParameterDAO {

    private final SystemParameterMapper mapper;

    public SystemParameterDAO(SystemParameterMapper mapper) {
        this.mapper = mapper;
    }

    public List<SystemParameterPO> selectAll() {
        return mapper.selectAll();
    }

    public SystemParameterPO selectByParamCode(String paramCode) {
        return mapper.selectByParamCode(paramCode);
    }

    public void updateValue(SystemParameterPO po) {
        mapper.updateValue(po);
    }
}
