package com.company.hrms.project.domain.service.external;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 外部員工服務介面
 * 用於跨服務獲取員工資訊
 */
public interface IExternalEmployeeService {

    /**
     * 批量獲取員工姓名
     * 
     * @param employeeIds 員工 ID 集合
     * @return Map<employeeId, fullName>
     */
    Map<UUID, String> getEmployeeNames(Set<UUID> employeeIds);

    /**
     * 獲取員工目前時薪
     * 
     * @param employeeId 員工 ID
     * @return 時薪
     */
    BigDecimal getEmployeeHourlyRate(UUID employeeId);
}
