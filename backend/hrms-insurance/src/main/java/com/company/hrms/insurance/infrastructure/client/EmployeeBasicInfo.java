package com.company.hrms.insurance.infrastructure.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 員工基本資訊 DTO
 * 用於從 Organization 服務取得員工資料
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeBasicInfo {

    /** 員工ID */
    private String employeeId;

    /** 員工姓名 */
    private String employeeName;

    /** 身分證字號 */
    private String idNumber;

    /** 出生日期 */
    private String birthDate;

    /** 性別 */
    private String gender;

    /** 到職日期 */
    private String hireDate;

    /** 部門名稱 */
    private String departmentName;

    /** 職稱 */
    private String jobTitle;
}
