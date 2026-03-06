package com.company.hrms.iam.infrastructure.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.company.hrms.iam.domain.model.aggregate.ScheduledJobConfig;
import com.company.hrms.iam.domain.repository.IScheduledJobConfigRepository;
import com.company.hrms.iam.infrastructure.dao.ScheduledJobConfigDAO;
import com.company.hrms.iam.infrastructure.po.ScheduledJobConfigPO;

/**
 * 排程任務配置 Repository 實作
 * 負責 PO 與 Domain Object 之間的轉換
 */
@Component
public class ScheduledJobConfigRepositoryImpl implements IScheduledJobConfigRepository {

    private final ScheduledJobConfigDAO dao;

    public ScheduledJobConfigRepositoryImpl(ScheduledJobConfigDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<ScheduledJobConfig> findAll() {
        return dao.selectAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ScheduledJobConfig> findByJobCode(String jobCode) {
        ScheduledJobConfigPO po = dao.selectByJobCode(jobCode);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public void update(ScheduledJobConfig jobConfig) {
        ScheduledJobConfigPO po = ScheduledJobConfigPO.builder()
                .jobCode(jobConfig.getJobCode())
                .cronExpression(jobConfig.getCronExpression())
                .enabled(jobConfig.isEnabled())
                .updatedAt(Timestamp.valueOf(jobConfig.getUpdatedAt()))
                .updatedBy(jobConfig.getUpdatedBy())
                .build();
        dao.updateConfig(po);
    }

    private ScheduledJobConfig toDomain(ScheduledJobConfigPO po) {
        return ScheduledJobConfig.builder()
                .id(po.getId())
                .jobCode(po.getJobCode())
                .jobName(po.getJobName())
                .module(po.getModule())
                .cronExpression(po.getCronExpression())
                .enabled(po.getEnabled() != null && po.getEnabled())
                .description(po.getDescription())
                .lastExecutedAt(po.getLastExecutedAt() != null ? po.getLastExecutedAt().toLocalDateTime() : null)
                .lastExecutionStatus(po.getLastExecutionStatus())
                .lastErrorMessage(po.getLastErrorMessage())
                .consecutiveFailures(po.getConsecutiveFailures() != null ? po.getConsecutiveFailures() : 0)
                .tenantId(po.getTenantId())
                .updatedAt(po.getUpdatedAt() != null ? po.getUpdatedAt().toLocalDateTime() : null)
                .updatedBy(po.getUpdatedBy())
                .build();
    }
}
