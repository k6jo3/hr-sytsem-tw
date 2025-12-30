package com.company.hrms.organization.application.service.employee;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.request.employee.TransferEmployeeRequest;
import com.company.hrms.organization.api.response.employee.TransferEmployeeResponse;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.application.service.employee.task.LoadDepartmentsTask;
import com.company.hrms.organization.application.service.employee.task.LoadEmployeeTask;
import com.company.hrms.organization.application.service.employee.task.PublishDepartmentChangedEventTask;
import com.company.hrms.organization.application.service.employee.task.SaveEmployeeHistoryTask;
import com.company.hrms.organization.application.service.employee.task.SaveEmployeeTask;
import com.company.hrms.organization.application.service.employee.task.TransferDepartmentTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 員工部門調動服務實作 (Pipeline 模式)
 * 
 * <p>
 * Pipeline 步驟：
 * <ol>
 * <li>LoadEmployeeTask - 載入員工資料</li>
 * <li>LoadDepartmentsTask - 載入新舊部門資料</li>
 * <li>TransferDepartmentTask - 執行部門調動</li>
 * <li>SaveEmployeeTask - 儲存員工</li>
 * <li>SaveEmployeeHistoryTask - 記錄人事歷程</li>
 * <li>PublishDepartmentChangedEventTask - 發布領域事件</li>
 * </ol>
 */
@Service("transferEmployeeServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransferEmployeeServiceImpl
                implements CommandApiService<TransferEmployeeRequest, TransferEmployeeResponse> {

        // === Pipeline Tasks ===
        private final LoadEmployeeTask loadEmployeeTask;
        private final LoadDepartmentsTask loadDepartmentsTask;
        private final TransferDepartmentTask transferDepartmentTask;
        private final SaveEmployeeTask saveEmployeeTask;
        private final SaveEmployeeHistoryTask saveEmployeeHistoryTask;
        private final PublishDepartmentChangedEventTask publishDepartmentChangedEventTask;

        @Override
        public TransferEmployeeResponse execCommand(TransferEmployeeRequest request,
                        JWTModel currentUser,
                        String... args) throws Exception {
                String employeeId = args[0];
                log.info("部門調動開始: employeeId={}, newDeptId={}",
                                employeeId, request.getNewDepartmentId());

                // 建立 Pipeline Context
                EmployeeContext context = new EmployeeContext(employeeId, request);

                // 執行 Pipeline
                BusinessPipeline.start(context)
                                .next(loadEmployeeTask) // 載入員工
                                .next(loadDepartmentsTask) // 載入部門
                                .next(transferDepartmentTask) // 執行調動
                                .next(saveEmployeeTask) // 儲存員工
                                .next(saveEmployeeHistoryTask) // 記錄歷程
                                .next(publishDepartmentChangedEventTask) // 發布事件
                                .execute();

                log.info("部門調動完成: employeeId={}", employeeId);

                // 組裝回應
                return TransferEmployeeResponse.success(
                                employeeId,
                                context.getEmployee().getEmployeeNumber(),
                                context.getEmployee().getFullName(),
                                context.getOldDepartment() != null ? context.getOldDepartment().getName() : "未知",
                                context.getNewDepartment() != null ? context.getNewDepartment().getName() : "未知",
                                request.getEffectiveDate());
        }
}
