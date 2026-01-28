package com.company.hrms.timesheet.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.timesheet.api.request.BatchApproveTimesheetRequest;
import com.company.hrms.timesheet.api.response.BatchApproveTimesheetResponse;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.model.valueobject.TimesheetId;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

import lombok.RequiredArgsConstructor;
import com.company.hrms.common.exception.EntityNotFoundException;

@Service("batchApproveTimesheetServiceImpl")
@RequiredArgsConstructor
public class BatchApproveTimesheetServiceImpl
        implements CommandApiService<BatchApproveTimesheetRequest, BatchApproveTimesheetResponse> {

    private final ITimesheetRepository timesheetRepository;

    @Override
    @Transactional
    public BatchApproveTimesheetResponse execCommand(BatchApproveTimesheetRequest request, JWTModel currentUser,
            String... args)
            throws Exception {
        UUID approverId = UUID.fromString(currentUser.getUserId());

        int successCount = 0;
        int failureCount = 0;

        for (UUID timesheetId : request.getTimesheetIds()) {
            try {
                Timesheet timesheet = timesheetRepository.findById(new TimesheetId(timesheetId))
                        .orElseThrow(() -> new EntityNotFoundException("Timesheet",
                                timesheetId.toString()));

                timesheet.approve(approverId);
                timesheetRepository.save(timesheet);

                successCount++;
            } catch (Exception e) {
                failureCount++;
                // 記錄錯誤但繼續處理其他工時表
            }
        }

        return BatchApproveTimesheetResponse.builder()
                .successCount(successCount)
                .failureCount(failureCount)
                .build();
    }
}
