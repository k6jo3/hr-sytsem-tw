package com.company.hrms.payroll.application.service.task;

import java.util.List;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.payroll.application.service.context.CalculatePayrollContext;
import com.company.hrms.payroll.domain.model.aggregate.PayrollRun;
import com.company.hrms.payroll.domain.model.aggregate.SalaryStructure;
import com.company.hrms.payroll.domain.repository.ISalaryStructureRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入符合條件的薪資結構任務
 * 查詢有效的員工薪資結構，用於後續計算
 *
 * <h3>重構說明</h3>
 * <p>
 * 使用 Repository 封裝的 {@code findAllActiveByPayrollSystem()} 方法，
 * 取代手動建構 QueryGroup 的方式。
 * </p>
 *
 * <h4>重構前：</h4>
 * <pre>
 * QueryGroup filter = QueryBuilder.where()
 *         .and("active", Operator.EQ, true)
 *         .and("payrollSystem", Operator.EQ, run.getPayrollSystem().name())
 *         .build();
 * Page&lt;SalaryStructure&gt; structures = structureRepository.findAll(filter, PageRequest.of(0, 1000));
 * </pre>
 *
 * <h4>重構後：</h4>
 * <pre>
 * List&lt;SalaryStructure&gt; structures = structureRepository
 *         .findAllActiveByPayrollSystem(run.getPayrollSystem().name());
 * </pre>
 */
@Component
@RequiredArgsConstructor
public class FetchEligibleStructuresTask implements PipelineTask<CalculatePayrollContext> {

    private final ISalaryStructureRepository structureRepository;

    @Override
    public void execute(CalculatePayrollContext context) {
        PayrollRun run = context.getPayrollRun();

        // 使用 Repository 封裝的查詢方法
        List<SalaryStructure> structures = structureRepository
                .findAllActiveByPayrollSystem(run.getPayrollSystem().name());

        context.setEligibleStructures(structures);
    }

    @Override
    public String getName() {
        return "FetchEligibleStructuresTask";
    }
}
