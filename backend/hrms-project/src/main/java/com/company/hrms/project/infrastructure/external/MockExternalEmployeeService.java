package com.company.hrms.project.infrastructure.external;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.company.hrms.project.domain.service.external.IExternalEmployeeService;

/**
 * 模擬外部員工服務 (暫時實現，待對接真正的微服務)
 */
@Service
public class MockExternalEmployeeService implements IExternalEmployeeService {

    @Override
    public Map<UUID, String> getEmployeeNames(Set<UUID> employeeIds) {
        Map<UUID, String> names = new HashMap<>();
        for (UUID id : employeeIds) {
            // 模擬返回姓名
            names.put(id, "員工_" + id.toString().substring(0, 4));
        }
        return names;
    }

    @Override
    public BigDecimal getEmployeeHourlyRate(UUID employeeId) {
        // 模擬返回預算時薪
        return BigDecimal.valueOf(800);
    }
}
