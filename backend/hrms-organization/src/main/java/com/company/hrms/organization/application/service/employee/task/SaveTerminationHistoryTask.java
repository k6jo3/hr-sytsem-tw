package com.company.hrms.organization.application.service.employee.task;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.api.request.employee.TerminateEmployeeRequest;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.domain.model.entity.EmployeeHistory;
import com.company.hrms.organization.domain.repository.IEmployeeHistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 儲存離職歷程 Task (Infrastructure Task)
 * 建立並儲存員工離職歷程記錄
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SaveTerminationHistoryTask implements PipelineTask<EmployeeContext> {

    private final IEmployeeHistoryRepository employeeHistoryRepository;

    @Override
    public void execute(EmployeeContext context) throws Exception {
        TerminateEmployeeRequest request = context.getTerminateRequest();

        UUID employeeId = UUID.fromString(context.getEmployeeId());

        // 建立離職歷程 (使用正確的工廠方法)
        EmployeeHistory history = EmployeeHistory.recordTermination(
                employeeId,
                request.getTerminationDate(),
                request.getReason(),
                null // approvedBy 暫不處理
        );

        employeeHistoryRepository.save(history);
        context.setHistory(history);

        log.debug("離職歷程記錄成功: employeeId={}", context.getEmployeeId());
    }

    @Override
    public String getName() {
        return "儲存離職歷程";
    }

    @Override
    public boolean shouldExecute(EmployeeContext context) {
        return context.getTerminateRequest() != null;
    }
}
