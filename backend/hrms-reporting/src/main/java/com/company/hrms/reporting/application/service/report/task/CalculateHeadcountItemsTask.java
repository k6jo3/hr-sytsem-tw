package com.company.hrms.reporting.application.service.report.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.reporting.api.response.HeadcountReportResponse.HeadcountItem;
import com.company.hrms.reporting.application.service.report.context.GetHeadcountReportContext;
import com.company.hrms.reporting.infrastructure.readmodel.EmployeeRosterReadModel;

/**
 * 計算人力盤點各組統計指標任務
 */
@Component
public class CalculateHeadcountItemsTask implements PipelineTask<GetHeadcountReportContext> {

    @Override
    public void execute(GetHeadcountReportContext ctx) {
        List<HeadcountItem> items = new ArrayList<>();
        Map<String, List<EmployeeRosterReadModel>> groupedData = ctx.getGroupedData();

        for (Map.Entry<String, List<EmployeeRosterReadModel>> entry : groupedData.entrySet()) {
            String groupName = entry.getKey();
            List<EmployeeRosterReadModel> groupEmps = entry.getValue();

            long total = groupEmps.size();
            long active = groupEmps.stream().filter(e -> "ACTIVE".equalsIgnoreCase(e.getStatus())).count();
            long probation = groupEmps.stream().filter(e -> "PROBATION".equalsIgnoreCase(e.getStatus())).count();
            long leave = groupEmps.stream().filter(e -> "LEAVE".equalsIgnoreCase(e.getStatus())).count();
            long male = 0;
            long female = 0;

            double avgServiceYears = groupEmps.stream()
                    .mapToDouble(e -> e.getServiceYears() != null ? e.getServiceYears() : 0.0)
                    .average().orElse(0.0);

            items.add(HeadcountItem.builder()
                    .dimensionName(groupName)
                    .totalCount((int) total)
                    .activeCount((int) active)
                    .probationCount((int) probation)
                    .leaveCount((int) leave)
                    .maleCount((int) male)
                    .femaleCount((int) female)
                    .avgServiceYears(Math.round(avgServiceYears * 10.0) / 10.0)
                    .avgAge(0.0)
                    .build());
        }
        ctx.setItems(items);
    }
}
