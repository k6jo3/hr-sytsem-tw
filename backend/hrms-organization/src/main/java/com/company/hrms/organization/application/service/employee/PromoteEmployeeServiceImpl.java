package com.company.hrms.organization.application.service.employee;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.request.employee.PromoteEmployeeRequest;
import com.company.hrms.organization.api.response.employee.PromoteEmployeeResponse;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.application.service.employee.task.LoadEmployeeTask;
import com.company.hrms.organization.application.service.employee.task.PromoteEmployeeTask;
import com.company.hrms.organization.application.service.employee.task.PublishPromotedEventTask;
import com.company.hrms.organization.application.service.employee.task.SaveEmployeeTask;
import com.company.hrms.organization.application.service.employee.task.SavePromotionHistoryTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 員工升遷服務實作 (Pipeline 模式)
 * 
 * <p>
 * Pipeline 步驟：
 * <ol>
 * <li>LoadEmployeeTask - 載入員工資料</li>
 * <li>PromoteEmployeeTask - 執行升遷</li>
 * <li>SaveEmployeeTask - 儲存員工</li>
 * <li>SavePromotionHistoryTask - 記錄升遷歷程</li>
 * <li>PublishPromotedEventTask - 發布升遷事件</li>
 * </ol>
 */
@Service("promoteEmployeeServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PromoteEmployeeServiceImpl
        implements CommandApiService<PromoteEmployeeRequest, PromoteEmployeeResponse> {

    // === Pipeline Tasks ===
    private final LoadEmployeeTask loadEmployeeTask;
    private final PromoteEmployeeTask promoteEmployeeTask;
    private final SaveEmployeeTask saveEmployeeTask;
    private final SavePromotionHistoryTask savePromotionHistoryTask;
    private final PublishPromotedEventTask publishPromotedEventTask;

    @Override
    public PromoteEmployeeResponse execCommand(PromoteEmployeeRequest request,
            JWTModel currentUser,
            String... args) throws Exception {
        String employeeId = args[0];
        log.info("升遷流程開始: employeeId={}, newJobTitle={}",
                employeeId, request.getNewJobTitle());

        // 建立 Pipeline Context
        EmployeeContext context = new EmployeeContext(employeeId, request);

        // 執行 Pipeline
        BusinessPipeline.start(context)
                .next(loadEmployeeTask) // 載入員工
                .next(promoteEmployeeTask) // 執行升遷
                .next(saveEmployeeTask) // 儲存員工
                .next(savePromotionHistoryTask) // 記錄歷程
                .next(publishPromotedEventTask) // 發布事件
                .execute();

        log.info("升遷流程完成: employeeId={}", employeeId);

        // 組裝回應
        return PromoteEmployeeResponse.success(
                employeeId,
                context.getEmployee().getEmployeeNumber(),
                context.getEmployee().getFullName(),
                context.getAttribute("oldJobTitle"),
                context.getEmployee().getJobTitle(),
                context.getAttribute("oldJobLevel"),
                context.getEmployee().getJobLevel(),
                request.getEffectiveDate());
    }
}
