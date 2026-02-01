package com.company.hrms.recruitment.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryCondition;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.recruitment.application.service.context.DashboardContext;
import com.company.hrms.recruitment.domain.repository.IJobOpeningRepository;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * 統計開啟中職缺
 */
@Component
@RequiredArgsConstructor
public class CountOpenJobsTask implements PipelineTask<DashboardContext> {

    private final IJobOpeningRepository jobOpeningRepository;

    @Override
    public void execute(DashboardContext context) throws Exception {
        // 使用宣告式 Condition
        OpenJobCondition condition = new OpenJobCondition();
        condition.setStatus("OPEN");
        // 如果 request 有 departmentId 也可以加 cond.setDepartmentId(...)
        if (context.getRequest().getDepartmentId() != null) {
            condition.setDepartmentId(context.getRequest().getDepartmentId());
        }

        QueryGroup query = QueryBuilder.where().fromDto(condition).build();

        long count = jobOpeningRepository.count(query);
        context.setOpenJobsCount(count);
    }

    @Override
    public String getName() {
        return "統計開啟職缺";
    }

    @Data
    public static class OpenJobCondition {
        @QueryCondition.EQ
        private String status;

        @QueryCondition.EQ("department_id")
        private String departmentId;
    }
}
