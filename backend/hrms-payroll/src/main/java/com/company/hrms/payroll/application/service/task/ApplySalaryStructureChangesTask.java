package com.company.hrms.payroll.application.service.task;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.payroll.application.dto.request.SalaryItemRequest;
import com.company.hrms.payroll.application.dto.request.UpdateSalaryStructureRequest;
import com.company.hrms.payroll.application.service.context.UpdateSalaryStructureContext;
import com.company.hrms.payroll.domain.model.aggregate.SalaryStructure;
import com.company.hrms.payroll.domain.model.entity.SalaryItem;
import com.company.hrms.payroll.domain.model.valueobject.ItemType;

import lombok.RequiredArgsConstructor;

/**
 * 執行薪資結構更新任務
 */
@Component
@RequiredArgsConstructor
public class ApplySalaryStructureChangesTask implements PipelineTask<UpdateSalaryStructureContext> {

    @Override
    public void execute(UpdateSalaryStructureContext context) {
        SalaryStructure structure = context.getSalaryStructure();
        UpdateSalaryStructureRequest request = context.getRequest();

        // 更新月薪
        if (request.getMonthlySalary() != null) {
            structure.adjustMonthlySalary(request.getMonthlySalary());
        }

        // 更新時薪
        if (request.getHourlyRate() != null) {
            structure.adjustHourlyRate(request.getHourlyRate());
        }

        // 停用處理
        if (request.getActive() != null && !request.getActive()) {
            structure.deactivate(request.getEndDate() != null ? request.getEndDate() : java.time.LocalDate.now());
        }

        // 更新項目 (完全替換策略)
        if (request.getItems() != null) {
            // 移除所有現有項目
            List<SalaryItem> existing = new ArrayList<>(structure.getItems());
            existing.forEach(item -> structure.removeSalaryItem(item.getItemId()));

            // 新增項目
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
    }

    @Override
    public String getName() {
        return "ApplySalaryStructureChangesTask";
    }
}
