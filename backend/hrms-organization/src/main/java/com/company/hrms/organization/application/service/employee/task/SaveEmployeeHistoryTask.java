package com.company.hrms.organization.application.service.employee.task;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.api.request.employee.TransferEmployeeRequest;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.domain.model.entity.EmployeeHistory;
import com.company.hrms.organization.domain.repository.IEmployeeHistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 儲存人事歷程 Task (Infrastructure Task)
 * 建立並儲存員工人事異動歷程記錄
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SaveEmployeeHistoryTask implements PipelineTask<EmployeeContext> {

    private final IEmployeeHistoryRepository employeeHistoryRepository;

    @Override
    public void execute(EmployeeContext context) throws Exception {
        TransferEmployeeRequest request = context.getTransferRequest();

        UUID employeeId = UUID.fromString(context.getEmployeeId());
        UUID oldDeptId = context.getOldDepartmentId() != null
                ? UUID.fromString(context.getOldDepartmentId())
                : null;
        UUID newDeptId = UUID.fromString(request.getNewDepartmentId());

        // 建立調動歷程 (使用正確的工廠方法)
        EmployeeHistory history = EmployeeHistory.recordDepartmentTransfer(
                employeeId,
                request.getEffectiveDate(),
                oldDeptId,
                newDeptId,
                request.getReason(),
                null // approvedBy 暫不處理
        );

        employeeHistoryRepository.save(history);
        context.setHistory(history);

        log.debug("人事歷程記錄成功: employeeId={}", context.getEmployeeId());
    }

    @Override
    public String getName() {
        return "儲存人事歷程";
    }

    @Override
    public boolean shouldExecute(EmployeeContext context) {
        // 只在有調動請求時執行
        return context.getTransferRequest() != null;
    }
}
