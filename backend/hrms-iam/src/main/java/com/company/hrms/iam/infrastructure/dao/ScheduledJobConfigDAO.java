package com.company.hrms.iam.infrastructure.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.company.hrms.iam.infrastructure.mapper.ScheduledJobConfigMapper;
import com.company.hrms.iam.infrastructure.po.ScheduledJobConfigPO;

/**
 * 排程任務配置 DAO
 * 封裝 Mapper 操作
 */
@Repository
public class ScheduledJobConfigDAO {

    private final ScheduledJobConfigMapper mapper;

    public ScheduledJobConfigDAO(ScheduledJobConfigMapper mapper) {
        this.mapper = mapper;
    }

    public List<ScheduledJobConfigPO> selectAll() {
        return mapper.selectAll();
    }

    public ScheduledJobConfigPO selectByJobCode(String jobCode) {
        return mapper.selectByJobCode(jobCode);
    }

    public void updateConfig(ScheduledJobConfigPO po) {
        mapper.updateConfig(po);
    }
}
