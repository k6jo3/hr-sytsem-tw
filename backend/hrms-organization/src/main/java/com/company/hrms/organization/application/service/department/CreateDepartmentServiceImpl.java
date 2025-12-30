package com.company.hrms.organization.application.service.department;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.request.department.CreateDepartmentRequest;
import com.company.hrms.organization.api.response.department.CreateDepartmentResponse;
import com.company.hrms.organization.application.service.department.context.DepartmentContext;
import com.company.hrms.organization.application.service.department.task.CheckDeptCodeExistenceTask;
import com.company.hrms.organization.application.service.department.task.CreateDeptAggregateTask;
import com.company.hrms.organization.application.service.department.task.SaveDeptTask;
import com.company.hrms.organization.application.service.department.task.ValidateOrgAndManagerTask;
import com.company.hrms.organization.application.service.department.task.ValidateParentDeptTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 建立部門 Application Service (Pipeline 模式)
 * 
 * <p>
 * 對應 API: POST /api/v1/departments
 * </p>
 * 
 * <p>
 * Pipeline 步驟：
 * </p>
 * <ol>
 * <li>ValidateOrgAndManagerTask - 驗證組織和主管存在</li>
 * <li>CheckDeptCodeExistenceTask - 驗證部門代碼唯一性</li>
 * <li>ValidateParentDeptTask - 驗證父部門存在（條件執行）</li>
 * <li>CreateDeptAggregateTask - 建立部門聚合根</li>
 * <li>SaveDeptTask - 儲存部門</li>
 * </ol>
 */
@Service("createDepartmentServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateDepartmentServiceImpl
        implements CommandApiService<CreateDepartmentRequest, CreateDepartmentResponse> {

    private final ValidateOrgAndManagerTask validateOrgAndManagerTask;
    private final CheckDeptCodeExistenceTask checkDeptCodeExistenceTask;
    private final ValidateParentDeptTask validateParentDeptTask;
    private final CreateDeptAggregateTask createDeptAggregateTask;
    private final SaveDeptTask saveDeptTask;

    @Override
    public CreateDepartmentResponse execCommand(CreateDepartmentRequest request,
            JWTModel currentUser, String... args) throws Exception {

        log.info("建立部門: code={}, name={}", request.getCode(), request.getName());

        // 1. 建立 Context
        DepartmentContext context = new DepartmentContext(request);

        // 2. 執行 Pipeline
        BusinessPipeline.start(context)
                .next(validateOrgAndManagerTask)
                .next(checkDeptCodeExistenceTask)
                .next(validateParentDeptTask)
                .next(createDeptAggregateTask)
                .next(saveDeptTask)
                .execute();

        // 3. 建立回應
        var department = context.getDepartment();

        log.info("部門建立成功: id={}, code={}",
                department.getId().getValue(),
                department.getCode());

        return CreateDepartmentResponse.success(
                department.getId().getValue().toString(),
                department.getCode(),
                department.getName(),
                department.getLevel());
    }
}
