package com.company.hrms.organization.application.service.employee;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.request.employee.TerminateEmployeeRequest;
import com.company.hrms.organization.api.response.employee.TerminateEmployeeResponse;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.application.service.employee.task.LoadEmployeeTask;
import com.company.hrms.organization.application.service.employee.task.PublishTerminatedEventTask;
import com.company.hrms.organization.application.service.employee.task.SaveEmployeeTask;
import com.company.hrms.organization.application.service.employee.task.SaveTerminationHistoryTask;
import com.company.hrms.organization.application.service.employee.task.TerminateEmployeeTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 員工離職服務實作 (Pipeline 模式)
 * 
 * <p>
 * Pipeline 步驟：
 * <ol>
 * <li>LoadEmployeeTask - 載入員工資料</li>
 * <li>TerminateEmployeeTask - 執行離職</li>
 * <li>SaveEmployeeTask - 儲存員工</li>
 * <li>SaveTerminationHistoryTask - 記錄離職歷程</li>
 * <li>PublishTerminatedEventTask - 發布離職事件</li>
 * </ol>
 */
@Service("terminateEmployeeServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TerminateEmployeeServiceImpl
                implements CommandApiService<TerminateEmployeeRequest, TerminateEmployeeResponse> {

        // === Pipeline Tasks ===
        private final LoadEmployeeTask loadEmployeeTask;
        private final TerminateEmployeeTask terminateEmployeeTask;
        private final SaveEmployeeTask saveEmployeeTask;
        private final SaveTerminationHistoryTask saveTerminationHistoryTask;
        private final PublishTerminatedEventTask publishTerminatedEventTask;

        @Override
        public TerminateEmployeeResponse execCommand(TerminateEmployeeRequest request,
                        JWTModel currentUser,
                        String... args) throws Exception {
                String employeeId = args[0];
                log.info("離職流程開始: employeeId={}, date={}",
                                employeeId, request.getTerminationDate());

                // 建立 Pipeline Context
                EmployeeContext context = new EmployeeContext(employeeId, request);

                // 執行 Pipeline
                BusinessPipeline.start(context)
                                .next(loadEmployeeTask) // 載入員工
                                .next(terminateEmployeeTask) // 執行離職
                                .next(saveEmployeeTask) // 儲存員工
                                .next(saveTerminationHistoryTask) // 記錄歷程
                                .next(publishTerminatedEventTask) // 發布事件
                                .execute();

                log.info("離職流程完成: employeeId={}", employeeId);

                // 組裝回應
                return TerminateEmployeeResponse.success(
                                employeeId,
                                context.getEmployee().getEmployeeNumber(),
                                context.getEmployee().getFullName(),
                                request.getTerminationDate(),
                                context.getEmployee().getTerminationType() != null
                                        ? context.getEmployee().getTerminationType().name()
                                        : null,
                                context.getEmployee().calculateNoticePeriod());
        }
}
