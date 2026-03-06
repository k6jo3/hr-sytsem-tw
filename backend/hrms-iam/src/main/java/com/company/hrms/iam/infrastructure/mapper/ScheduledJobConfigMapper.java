package com.company.hrms.iam.infrastructure.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.company.hrms.iam.infrastructure.po.ScheduledJobConfigPO;

/**
 * 排程任務配置 MyBatis Mapper
 */
@Mapper
public interface ScheduledJobConfigMapper {

    /**
     * 查詢所有排程任務
     */
    @Select("SELECT id, job_code, job_name, module, cron_expression, enabled, description, " +
            "last_executed_at, last_execution_status, last_error_message, consecutive_failures, " +
            "tenant_id, updated_at, updated_by, created_at " +
            "FROM scheduled_job_configs ORDER BY module, job_code")
    List<ScheduledJobConfigPO> selectAll();

    /**
     * 根據任務代碼查詢
     */
    @Select("SELECT id, job_code, job_name, module, cron_expression, enabled, description, " +
            "last_executed_at, last_execution_status, last_error_message, consecutive_failures, " +
            "tenant_id, updated_at, updated_by, created_at " +
            "FROM scheduled_job_configs WHERE job_code = #{jobCode}")
    ScheduledJobConfigPO selectByJobCode(@Param("jobCode") String jobCode);

    /**
     * 更新排程任務配置
     */
    @Update("UPDATE scheduled_job_configs SET cron_expression = #{cronExpression}, " +
            "enabled = #{enabled}, updated_at = #{updatedAt}, updated_by = #{updatedBy} " +
            "WHERE job_code = #{jobCode}")
    void updateConfig(ScheduledJobConfigPO po);
}
