package com.company.hrms.iam.domain.repository;

import java.util.List;
import java.util.Optional;

import com.company.hrms.iam.domain.model.aggregate.ScheduledJobConfig;

/**
 * 排程任務配置 Repository 介面
 * 定義於 Domain 層，實作於 Infrastructure 層
 */
public interface IScheduledJobConfigRepository {

    List<ScheduledJobConfig> findAll();

    Optional<ScheduledJobConfig> findByJobCode(String jobCode);

    void update(ScheduledJobConfig jobConfig);
}
