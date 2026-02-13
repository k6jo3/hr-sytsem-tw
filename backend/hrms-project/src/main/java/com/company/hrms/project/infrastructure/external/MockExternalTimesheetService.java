package com.company.hrms.project.infrastructure.external;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.company.hrms.project.domain.service.external.IExternalTimesheetService;

/**
 * 模擬外部工時服務
 */
@Service
public class MockExternalTimesheetService implements IExternalTimesheetService {

    @Override
    public List<MonthlyCostData> getMonthlyCosts(UUID projectId) {
        List<MonthlyCostData> data = new ArrayList<>();

        // 模擬最近三個月的資料
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        for (int i = 2; i >= 0; i--) {
            LocalDate date = now.minusMonths(i);
            data.add(MonthlyCostData.builder()
                    .yearMonth(date.format(formatter))
                    .hours(BigDecimal.valueOf(160 + (i * 10)))
                    .cost(BigDecimal.valueOf(128000 + (i * 8000)))
                    .build());
        }

        return data;
    }
}
