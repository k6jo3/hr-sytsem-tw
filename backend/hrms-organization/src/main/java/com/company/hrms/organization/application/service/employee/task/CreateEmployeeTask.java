package com.company.hrms.organization.application.service.employee.task;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.api.request.employee.CreateEmployeeRequest;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.Address;
import com.company.hrms.organization.domain.model.valueobject.BankAccount;
import com.company.hrms.organization.domain.model.valueobject.EmergencyContact;
import com.company.hrms.organization.domain.model.valueobject.EmploymentType;
import com.company.hrms.organization.domain.model.valueobject.Gender;
import com.company.hrms.organization.domain.model.valueobject.MaritalStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 建立員工 Task (Domain Task)
 * 使用 Employee.onboard 工廠方法建立新員工
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CreateEmployeeTask implements PipelineTask<EmployeeContext> {

    @Override
    public void execute(EmployeeContext context) throws Exception {
        CreateEmployeeRequest request = context.getCreateRequest();
        log.debug("建立員工: {}", request.getEmployeeNumber());

        // 建立員工 Aggregate
        Employee employee = Employee.onboard(
                request.getEmployeeNumber(),
                request.getFirstName(),
                request.getLastName(),
                request.getNationalId(),
                request.getDateOfBirth(),
                Gender.valueOf(request.getGender()),
                request.getCompanyEmail(),
                request.getMobilePhone(),
                UUID.fromString(request.getOrganizationId()),
                UUID.fromString(request.getDepartmentId()),
                request.getJobTitle(),
                EmploymentType.valueOf(request.getEmploymentType()),
                request.getHireDate(),
                request.getProbationMonths() != null ? request.getProbationMonths() : 3);

        // 設定其他可選欄位
        if (request.getMaritalStatus() != null) {
            employee.setMaritalStatus(MaritalStatus.valueOf(request.getMaritalStatus()));
        }

        if (request.getAddress() != null) {
            employee.updatePersonalInfo(
                    request.getPersonalEmail(),
                    request.getMobilePhone(),
                    new Address(
                            request.getAddress().getPostalCode(),
                            request.getAddress().getCity(),
                            request.getAddress().getDistrict(),
                            request.getAddress().getStreet()),
                    null);
        }

        if (request.getEmergencyContact() != null) {
            employee.updatePersonalInfo(
                    null, null, null,
                    new EmergencyContact(
                            request.getEmergencyContact().getName(),
                            request.getEmergencyContact().getRelationship(),
                            request.getEmergencyContact().getPhoneNumber()));
        }

        if (request.getBankAccount() != null) {
            employee.updateBankAccount(new BankAccount(
                    request.getBankAccount().getBankCode(),
                    request.getBankAccount().getBankName(),
                    request.getBankAccount().getBranchCode(),
                    request.getBankAccount().getAccountNumber(),
                    request.getBankAccount().getAccountHolderName()));
        }

        // 設定主管
        if (request.getManagerId() != null) {
            employee.updateJobInfo(
                    request.getJobTitle(),
                    request.getJobLevel(),
                    UUID.fromString(request.getManagerId()));
        }

        context.setEmployee(employee);
        log.info("員工建立成功: {}", employee.getId().getValue());
    }

    @Override
    public String getName() {
        return "建立員工";
    }

    @Override
    public boolean shouldExecute(EmployeeContext context) {
        return context.getCreateRequest() != null;
    }
}
