package com.company.hrms.timesheet.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.timesheet.api.request.LockTimesheetRequest;
import com.company.hrms.timesheet.api.response.LockTimesheetResponse;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.model.valueobject.TimesheetId;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

@ExtendWith(MockitoExtension.class)
public class LockTimesheetServiceTest {

    @Mock
    private ITimesheetRepository timesheetRepository;

    @InjectMocks
    private LockTimesheetServiceImpl lockTimesheetService;

    private JWTModel currentUser;
    private Timesheet timesheet;
    private final UUID timesheetId = UUID.randomUUID();

    @BeforeEach
    public void setUp() {
        currentUser = new JWTModel();
        currentUser.setUserId(UUID.randomUUID().toString());
        // currentUser.setUserName("Admin User");

        timesheet = Timesheet.reconstitute(
                new TimesheetId(timesheetId),
                UUID.randomUUID(),
                null,
                LocalDate.now(),
                LocalDate.now().plusDays(6),
                null,
                java.math.BigDecimal.ZERO,
                com.company.hrms.timesheet.domain.model.valueobject.TimesheetStatus.APPROVED,
                null,
                null,
                null,
                null,
                false); // Initially unlocked
    }

    @Test
    public void execCommand_ShouldLockTimesheet() throws Exception {
        LockTimesheetRequest request = new LockTimesheetRequest();
        request.setTimesheetId(timesheetId.toString());

        when(timesheetRepository.findById(any(TimesheetId.class))).thenReturn(Optional.of(timesheet));
        when(timesheetRepository.save(any(Timesheet.class))).thenReturn(timesheet);

        LockTimesheetResponse response = lockTimesheetService.execCommand(request, currentUser);

        assertTrue(response.isLocked());
        assertTrue(timesheet.isLocked());
        verify(timesheetRepository).save(timesheet);
    }
}
