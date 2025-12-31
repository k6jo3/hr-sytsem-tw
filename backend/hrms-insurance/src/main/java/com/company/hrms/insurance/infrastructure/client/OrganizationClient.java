package com.company.hrms.insurance.infrastructure.client;

import java.util.Optional;

/**
 * 組織服務客戶端介面
 * 用於從 Organization 服務取得員工資料
 */
public interface OrganizationClient {

    /**
     * 根據員工ID取得員工基本資訊
     */
    Optional<EmployeeBasicInfo> getEmployeeById(String employeeId);

    /**
     * 根據員工ID取得員工姓名
     */
    default String getEmployeeName(String employeeId) {
        return getEmployeeById(employeeId)
                .map(EmployeeBasicInfo::getEmployeeName)
                .orElse("未知");
    }

    /**
     * 根據員工ID取得身分證字號
     */
    default String getIdNumber(String employeeId) {
        return getEmployeeById(employeeId)
                .map(EmployeeBasicInfo::getIdNumber)
                .orElse("");
    }
}
