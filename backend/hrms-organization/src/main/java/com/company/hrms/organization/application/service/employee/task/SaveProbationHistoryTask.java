package com.company.hrms.organization.application.service.employee.task;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.domain.model.entity.EmployeeHistory;
import com.company.hrms.organization.domain.repository.IEmployeeHistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 儲存試用期轉正歷程 Task (Infrastructure Task)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SaveProbationHistoryTask implements PipelineTask<EmployeeContext> {

    private final IEmployeeHistoryRepository employeeHistoryRepository;

    @Override
    public void execute(EmployeeContext context) throws Exception {
        UUID employeeId = UUID.fromString(context.getEmployeeId());

        // 建立轉正歷程
        EmployeeHistory history = EmployeeHistory.recordProbationPassed(
                employeeId,
                LocalDate.now(),
                null // approvedBy 暫不處理
        );

        employeeHistoryRepository.save(history);
        context.setHistory(history);

        log.debug("轉正歷程記錄成功: employeeId={}", context.getEmployeeId());
    }

    @Override
    public String getName() {
        return "儲存轉正歷程";
    }
}
