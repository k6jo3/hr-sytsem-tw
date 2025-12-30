package com.company.hrms.organization.application.service.employee;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.request.employee.CreateEmployeeRequest;
import com.company.hrms.organization.api.response.employee.CreateEmployeeResponse;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.application.service.employee.task.CreateEmployeeTask;
import com.company.hrms.organization.application.service.employee.task.PublishCreatedEventTask;
import com.company.hrms.organization.application.service.employee.task.SaveEmployeeTask;
import com.company.hrms.organization.application.service.employee.task.ValidateEmployeeTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 新增員工服務實作 (Pipeline 模式)
 * 
 * <p>
 * Pipeline 步驟：
 * <ol>
 * <li>ValidateEmployeeTask - 驗證員工資料唯一性</li>
 * <li>CreateEmployeeTask - 建立員工聚合根</li>
 * <li>SaveEmployeeTask - 儲存員工</li>
 * <li>PublishCreatedEventTask - 發布員工建立事件</li>
 * </ol>
 */
@Service("createEmployeeServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateEmployeeServiceImpl
                implements CommandApiService<CreateEmployeeRequest, CreateEmployeeResponse> {

        // === Pipeline Tasks ===
        private final ValidateEmployeeTask validateEmployeeTask;
        private final CreateEmployeeTask createEmployeeTask;
        private final SaveEmployeeTask saveEmployeeTask;
        private final PublishCreatedEventTask publishCreatedEventTask;

        @Override
        public CreateEmployeeResponse execCommand(CreateEmployeeRequest request,
                        JWTModel currentUser,
                        String... args) throws Exception {
                log.info("新增員工流程開始: {}", request.getEmployeeNumber());

                // 建立 Pipeline Context
                EmployeeContext context = new EmployeeContext(request, currentUser.getTenantId());

                // 執行 Pipeline
                BusinessPipeline.start(context)
                                .next(validateEmployeeTask) // 驗證資料
                                .next(createEmployeeTask) // 建立員工
                                .next(saveEmployeeTask) // 儲存員工
                                .next(publishCreatedEventTask) // 發布事件
                                .execute();

                log.info("新增員工流程完成: {}", context.getEmployee().getId().getValue());

                // 組裝回應
                return CreateEmployeeResponse.success(
                                context.getEmployee().getId().getValue().toString(),
                                context.getEmployee().getEmployeeNumber(),
                                context.getEmployee().getFullName());
        }
}
