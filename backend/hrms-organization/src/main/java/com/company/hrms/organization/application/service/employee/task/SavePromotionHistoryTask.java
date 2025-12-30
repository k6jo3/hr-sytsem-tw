package com.company.hrms.organization.application.service.employee.task;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.api.request.employee.PromoteEmployeeRequest;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.domain.model.entity.EmployeeHistory;
import com.company.hrms.organization.domain.repository.IEmployeeHistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 儲存升遷歷程 Task (Infrastructure Task)
 * 建立並儲存員工升遷歷程記錄
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SavePromotionHistoryTask implements PipelineTask<EmployeeContext> {

    private final IEmployeeHistoryRepository employeeHistoryRepository;

    @Override
    public void execute(EmployeeContext context) throws Exception {
        PromoteEmployeeRequest request = context.getPromoteRequest();

        UUID employeeId = UUID.fromString(context.getEmployeeId());
        String oldJobTitle = context.getAttribute("oldJobTitle");
        String oldJobLevel = context.getAttribute("oldJobLevel");

        // 建立升遷歷程 (使用正確的工廠方法)
        EmployeeHistory history = EmployeeHistory.recordPromotion(
                employeeId,
                request.getEffectiveDate(),
                oldJobTitle != null ? oldJobTitle : "",
                oldJobLevel != null ? oldJobLevel : "",
                request.getNewJobTitle() != null ? request.getNewJobTitle() : "",
                request.getNewJobLevel() != null ? request.getNewJobLevel() : "",
                request.getReason(),
                null // approvedBy 暫不處理
        );

        employeeHistoryRepository.save(history);
        context.setHistory(history);

        log.debug("升遷歷程記錄成功: employeeId={}", context.getEmployeeId());
    }

    @Override
    public String getName() {
        return "儲存升遷歷程";
    }

    @Override
    public boolean shouldExecute(EmployeeContext context) {
        return context.getPromoteRequest() != null;
    }
}
