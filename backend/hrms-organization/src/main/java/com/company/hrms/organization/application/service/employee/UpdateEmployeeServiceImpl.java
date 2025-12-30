package com.company.hrms.organization.application.service.employee;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.request.employee.UpdateEmployeeRequest;
import com.company.hrms.organization.api.response.employee.EmployeeDetailResponse;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.application.service.employee.task.LoadEmployeeTask;
import com.company.hrms.organization.application.service.employee.task.SaveEmployeeTask;
import com.company.hrms.organization.application.service.employee.task.UpdateEmployeeTask;
import com.company.hrms.organization.domain.model.aggregate.Employee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 更新員工服務實作 (Pipeline 模式)
 * 
 * <p>
 * Pipeline 步驟：
 * <ol>
 * <li>LoadEmployeeTask - 載入員工資料</li>
 * <li>UpdateEmployeeTask - 更新員工資料</li>
 * <li>SaveEmployeeTask - 儲存員工</li>
 * </ol>
 */
@Service("updateEmployeeServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateEmployeeServiceImpl
        implements CommandApiService<UpdateEmployeeRequest, EmployeeDetailResponse> {

    // === Pipeline Tasks ===
    private final LoadEmployeeTask loadEmployeeTask;
    private final UpdateEmployeeTask updateEmployeeTask;
    private final SaveEmployeeTask saveEmployeeTask;

    @Override
    public EmployeeDetailResponse execCommand(UpdateEmployeeRequest request,
            JWTModel currentUser,
            String... args) throws Exception {
        String employeeId = args[0];
        log.info("更新員工流程開始: employeeId={}", employeeId);

        // 建立 Pipeline Context
        EmployeeContext context = new EmployeeContext(employeeId, request);

        // 執行 Pipeline
        BusinessPipeline.start(context)
                .next(loadEmployeeTask) // 載入員工
                .next(updateEmployeeTask) // 更新資料
                .next(saveEmployeeTask) // 儲存員工
                .execute();

        log.info("更新員工流程完成: employeeId={}", employeeId);

        // 組裝回應
        return buildEmployeeDetailResponse(context.getEmployee());
    }

    private EmployeeDetailResponse buildEmployeeDetailResponse(Employee employee) {
        return EmployeeDetailResponse.builder()
                .employeeId(employee.getId().getValue().toString())
                .employeeNumber(employee.getEmployeeNumber())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .fullName(employee.getFullName())
                .gender(employee.getGender().name())
                .dateOfBirth(employee.getBirthDate())
                .companyEmail(employee.getCompanyEmail() != null ? employee.getCompanyEmail().getValue() : null)
                .mobilePhone(employee.getMobilePhone())
                .jobTitle(employee.getJobTitle())
                .jobLevel(employee.getJobLevel())
                .employmentType(employee.getEmploymentType().name())
                .employmentStatus(employee.getEmploymentStatus().name())
                .hireDate(employee.getHireDate())
                .maritalStatus(employee.getMaritalStatus() != null ? employee.getMaritalStatus().name() : null)
                .build();
    }
}
