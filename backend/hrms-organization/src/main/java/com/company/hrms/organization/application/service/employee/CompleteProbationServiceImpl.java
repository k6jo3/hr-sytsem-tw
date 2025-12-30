package com.company.hrms.organization.application.service.employee;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.application.service.employee.task.CompleteProbationTask;
import com.company.hrms.organization.application.service.employee.task.LoadEmployeeTask;
import com.company.hrms.organization.application.service.employee.task.PublishProbationPassedEventTask;
import com.company.hrms.organization.application.service.employee.task.SaveEmployeeTask;
import com.company.hrms.organization.application.service.employee.task.SaveProbationHistoryTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 試用期轉正服務實作 (Pipeline 模式)
 * 
 * <p>
 * Pipeline 步驟：
 * <ol>
 * <li>LoadEmployeeTask - 載入員工資料</li>
 * <li>CompleteProbationTask - 執行轉正</li>
 * <li>SaveEmployeeTask - 儲存員工</li>
 * <li>SaveProbationHistoryTask - 記錄轉正歷程</li>
 * <li>PublishProbationPassedEventTask - 發布轉正事件</li>
 * </ol>
 */
@Service("completeProbationServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CompleteProbationServiceImpl
        implements CommandApiService<Object, Void> {

    // === Pipeline Tasks ===
    private final LoadEmployeeTask loadEmployeeTask;
    private final CompleteProbationTask completeProbationTask;
    private final SaveEmployeeTask saveEmployeeTask;
    private final SaveProbationHistoryTask saveProbationHistoryTask;
    private final PublishProbationPassedEventTask publishProbationPassedEventTask;

    @Override
    public Void execCommand(Object request,
            JWTModel currentUser,
            String... args) throws Exception {
        String employeeId = args[0];
        log.info("試用期轉正流程開始: employeeId={}", employeeId);

        // 建立 Pipeline Context
        EmployeeContext context = new EmployeeContext(employeeId);

        // 執行 Pipeline
        BusinessPipeline.start(context)
                .next(loadEmployeeTask) // 載入員工
                .next(completeProbationTask) // 執行轉正
                .next(saveEmployeeTask) // 儲存員工
                .next(saveProbationHistoryTask) // 記錄歷程
                .next(publishProbationPassedEventTask) // 發布事件
                .execute();

        log.info("試用期轉正流程完成: employeeId={}", employeeId);

        return null;
    }
}
