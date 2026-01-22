package com.company.hrms.training.application.task.enrollment;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.application.service.context.EnrollCourseContext;

/**
 * 取得員工資訊 Task
 * 負責從組織服務取得員工和主管資訊
 */
@Component
public class FetchEmployeeInfoTask implements PipelineTask<EnrollCourseContext> {

    // TODO: 注入 Organization Service Feign Client
    // private final OrganizationServiceClient organizationService;

    @Override
    public void execute(EnrollCourseContext context) {
        // TODO: 呼叫組織服務/IAM服務取得員工姓名和主管資訊
        // 目前使用預設值
        context.setEmployeeName("Employee " + context.getEmployeeId());
        context.setManagerId("ManagerID");
        context.setManagerName("Manager Name");
    }
}
