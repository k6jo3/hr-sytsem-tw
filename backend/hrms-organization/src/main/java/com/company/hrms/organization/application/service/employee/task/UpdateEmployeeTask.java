package com.company.hrms.organization.application.service.employee.task;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.api.request.employee.UpdateEmployeeRequest;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.Address;
import com.company.hrms.organization.domain.model.valueobject.BankAccount;
import com.company.hrms.organization.domain.model.valueobject.EmergencyContact;
import com.company.hrms.organization.domain.model.valueobject.MaritalStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 更新員工資料 Task (Domain Task)
 * 更新員工的個人資料、聯絡方式、銀行帳戶等
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateEmployeeTask implements PipelineTask<EmployeeContext> {

    @Override
    public void execute(EmployeeContext context) throws Exception {
        Employee employee = context.getEmployee();
        UpdateEmployeeRequest request = context.getUpdateRequest();
        log.debug("更新員工資料: {}", employee.getEmployeeNumber());

        // 記錄舊 Email（用於事件發布）
        if (employee.getCompanyEmail() != null) {
            context.setAttribute("oldEmail", employee.getCompanyEmail().getValue());
        }

        // 更新婚姻狀況
        if (request.getMaritalStatus() != null) {
            employee.setMaritalStatus(MaritalStatus.valueOf(request.getMaritalStatus()));
        }

        // 更新地址
        Address address = null;
        if (request.getAddress() != null) {
            address = new Address(
                    null,
                    request.getAddress().getCity(),
                    request.getAddress().getDistrict(),
                    request.getAddress().getStreet());
        }

        // 更新緊急聯絡人
        EmergencyContact emergencyContact = null;
        if (request.getEmergencyContact() != null) {
            emergencyContact = new EmergencyContact(
                    request.getEmergencyContact().getName(),
                    request.getEmergencyContact().getRelationship(),
                    request.getEmergencyContact().getPhoneNumber());
        }

        // 更新個人資料
        employee.updatePersonalInfo(
                request.getPersonalEmail(),
                request.getMobilePhone(),
                address != null ? address : employee.getAddress(),
                emergencyContact != null ? emergencyContact : employee.getEmergencyContact());

        // 更新公司 Email (如果有提供)
        if (request.getCompanyEmail() != null) {
            employee.updateCompanyEmail(request.getCompanyEmail());
        }

        // 更新銀行帳戶
        if (request.getBankAccount() != null) {
            employee.updateBankAccount(new BankAccount(
                    request.getBankAccount().getBankCode(),
                    request.getBankAccount().getBankName(),
                    request.getBankAccount().getBranchCode(),
                    request.getBankAccount().getAccountNumber(),
                    request.getBankAccount().getAccountHolderName()));
        }

        // 更新職務資訊
        if (request.getJobTitle() != null || request.getJobLevel() != null || request.getManagerId() != null) {
            UUID managerId = request.getManagerId() != null ? UUID.fromString(request.getManagerId()) : null;
            employee.updateJobInfo(request.getJobTitle(), request.getJobLevel(), managerId);
        }

        // 更新照片
        if (request.getPhotoUrl() != null) {
            employee.updatePhoto(request.getPhotoUrl());
        }

        log.info("員工資料更新完成: {}", employee.getEmployeeNumber());
    }

    @Override
    public String getName() {
        return "更新員工資料";
    }

    @Override
    public boolean shouldExecute(EmployeeContext context) {
        return context.getUpdateRequest() != null;
    }
}
