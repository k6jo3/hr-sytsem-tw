package com.company.hrms.payroll.application.service.task;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.payroll.application.dto.request.CreateSalaryStructureRequest;
import com.company.hrms.payroll.application.dto.request.SalaryItemRequest;
import com.company.hrms.payroll.application.service.context.CreateSalaryStructureContext;
import com.company.hrms.payroll.domain.model.aggregate.SalaryStructure;
import com.company.hrms.payroll.domain.model.entity.SalaryItem;
import com.company.hrms.payroll.domain.model.valueobject.ItemType;
import com.company.hrms.payroll.domain.model.valueobject.PayrollCycle;
import com.company.hrms.payroll.domain.model.valueobject.PayrollSystem;

import lombok.RequiredArgsConstructor;

/**
 * 初始化薪資結構任務
 * 根據請求建立薪資結構聚合根
 */
@Component
@RequiredArgsConstructor
public class InitSalaryStructureTask implements PipelineTask<CreateSalaryStructureContext> {

    @Override
    public void execute(CreateSalaryStructureContext context) {
        CreateSalaryStructureRequest request = context.getRequest();

        PayrollSystem system = PayrollSystem.valueOf(request.getPayrollSystem());
        PayrollCycle cycle = PayrollCycle.valueOf(request.getPayrollCycle());
        LocalDate effectiveDate = request.getEffectiveDate() != null ? request.getEffectiveDate() : LocalDate.now();

        SalaryStructure structure;
        if (system == PayrollSystem.MONTHLY) {
            structure = SalaryStructure.createMonthly(
                    request.getEmployeeId(),
                    request.getMonthlySalary(),
                    cycle,
                    effectiveDate);
        } else {
            structure = SalaryStructure.createHourly(
                    request.getEmployeeId(),
                    request.getHourlyRate(),
                    cycle,
                    effectiveDate);
        }

        // 新增薪資項目
        if (request.getItems() != null) {
            for (SalaryItemRequest itemReq : request.getItems()) {
                SalaryItem item = SalaryItem.reconstruct(
                        UUID.randomUUID().toString(),
                        itemReq.getCode(),
                        itemReq.getName(),
                        ItemType.valueOf(itemReq.getType()),
                        itemReq.getAmount(),
                        itemReq.isFixedAmount(),
                        itemReq.isTaxable(),
                        itemReq.isInsurable());
                structure.addSalaryItem(item);
            }
        }

        context.setSalaryStructure(structure);
    }

    @Override
    public String getName() {
        return "InitSalaryStructureTask";
    }
}
