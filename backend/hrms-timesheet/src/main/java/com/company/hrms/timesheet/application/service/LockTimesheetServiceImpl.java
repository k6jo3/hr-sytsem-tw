package com.company.hrms.timesheet.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.timesheet.api.request.LockTimesheetRequest;
import com.company.hrms.timesheet.api.response.LockTimesheetResponse;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.model.valueobject.TimesheetId;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

import lombok.RequiredArgsConstructor;

@Service("lockTimesheetServiceImpl")
@RequiredArgsConstructor
@Transactional
public class LockTimesheetServiceImpl implements CommandApiService<LockTimesheetRequest, LockTimesheetResponse> {

        private final ITimesheetRepository timesheetRepository;

        @Override
        public LockTimesheetResponse execCommand(LockTimesheetRequest request, JWTModel currentUser, String... args)
                        throws Exception {
                // TODO: 參數命名不符合clean code
                Timesheet t = timesheetRepository.findById(new TimesheetId(UUID.fromString(request.getTimesheetId())))
                                .orElseThrow(() -> new EntityNotFoundException("Timesheet", request.getTimesheetId()));

                t.lock();

                timesheetRepository.save(t);

                return LockTimesheetResponse.builder()
                                .timesheetId(t.getId().getValue().toString())
                                .locked(t.isLocked())
                                .build();
        }
}
