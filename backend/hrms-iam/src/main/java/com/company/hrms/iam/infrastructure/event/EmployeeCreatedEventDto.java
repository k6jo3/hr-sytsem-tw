package com.company.hrms.iam.infrastructure.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 員工建立事件 DTO
 *
 * <p>用於接收來自 Organization 服務的 EmployeeCreatedEvent。
 * IAM 服務不直接依賴 Organization 的 Domain Event 類別，
 * 而是透過此 DTO 解耦跨服務事件的資料結構。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCreatedEventDto {

    /** 員工 ID */
    private String employeeId;

    /** 員工編號（如 E001） */
    private String employeeNumber;

    /** 員工全名 */
    private String fullName;

    /** 公司 Email */
    private String companyEmail;

    /** 組織 ID */
    private String organizationId;

    /** 部門 ID */
    private String departmentId;

    /** 職稱 */
    private String jobTitle;

    /** 到職日期 */
    private String hireDate;
}
