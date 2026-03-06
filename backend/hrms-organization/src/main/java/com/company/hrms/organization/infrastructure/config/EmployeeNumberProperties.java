package com.company.hrms.organization.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 員工編號生成規則配置
 * 由 application.yml 的 hrms.employee-number 設定載入
 * 可透過系統管理頁面發送事件動態更新
 */
@Data
@Component
@ConfigurationProperties(prefix = "hrms.employee-number")
public class EmployeeNumberProperties {

    /** 員工編號前綴（如 EMP, E, HR） */
    private String prefix = "EMP";

    /** 編號格式：YYYYMM-NNNN 或 NNNN */
    private String format = "YYYYMM-NNNN";

    /** 流水號補零位數 */
    private int seqDigits = 4;
}
