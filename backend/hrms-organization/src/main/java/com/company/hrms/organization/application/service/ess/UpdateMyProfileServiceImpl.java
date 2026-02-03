package com.company.hrms.organization.application.service.ess;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.request.ess.UpdateMyProfileRequest;
import com.company.hrms.organization.api.response.ess.MyProfileResponse;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.Address;
import com.company.hrms.organization.domain.model.valueobject.BankAccount;
import com.company.hrms.organization.domain.model.valueobject.EmergencyContact;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.model.valueobject.MaritalStatus;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 更新個人資料服務實作 (員工自助)
 */
@Service("updateMyProfileServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateMyProfileServiceImpl implements CommandApiService<UpdateMyProfileRequest, MyProfileResponse> {

    private final IEmployeeRepository employeeRepository;
    private final GetMyProfileServiceImpl getMyProfileService;

    @Override
    public MyProfileResponse execCommand(UpdateMyProfileRequest request, JWTModel currentUser, String... args)
            throws Exception {
        String employeeIdStr = currentUser.getUserId();
        log.info("更新個人資料: userId={}", employeeIdStr);

        EmployeeId employeeId = new EmployeeId(employeeIdStr);

        // 1. 查詢員工
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new DomainException("EMPLOYEE_NOT_FOUND", "找不到對應的員工資料: " + employeeIdStr));

        // 2. 更新資料
        // 地址
        Address domainAddress = null;
        if (request.getAddress() != null) {
            domainAddress = new Address(
                    request.getAddress().getPostalCode(),
                    request.getAddress().getCity(),
                    request.getAddress().getDistrict(),
                    request.getAddress().getStreet());
        }

        // 緊急聯絡人
        EmergencyContact domainContact = null;
        if (request.getEmergencyContact() != null) {
            domainContact = new EmergencyContact(
                    request.getEmergencyContact().getName(),
                    request.getEmergencyContact().getRelationship(),
                    request.getEmergencyContact().getPhone());
        }

        // 銀行帳戶
        if (request.getBankAccount() != null) {
            BankAccount domainBank = new BankAccount(
                    request.getBankAccount().getBankCode(),
                    null, // bankName
                    request.getBankAccount().getBranchCode(),
                    request.getBankAccount().getAccountNumber(),
                    request.getBankAccount().getAccountHolderName());
            employee.updateBankAccount(domainBank);
        }

        // 婚姻狀況
        if (request.getMaritalStatus() != null) {
            employee.setMaritalStatus(MaritalStatus.valueOf(request.getMaritalStatus()));
        }

        // 基本個人資料 (手機、地址、緊急聯絡人)
        employee.updatePersonalInfo(null, request.getPhone(), domainAddress, domainContact);

        // 3. 儲存
        employeeRepository.save(employee);

        log.info("個人資料更新成功: userId={}", employeeIdStr);

        // 4. 返回最新的個人資料
        return getMyProfileService.getResponse(null, currentUser);
    }
}
