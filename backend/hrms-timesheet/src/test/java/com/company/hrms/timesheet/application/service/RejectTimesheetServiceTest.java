package com.company.hrms.timesheet.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.common.application.pipeline.PipelineExecutionException;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.timesheet.api.request.RejectTimesheetRequest;
import com.company.hrms.timesheet.api.response.RejectTimesheetResponse;
import com.company.hrms.timesheet.application.service.task.LoadTimesheetForRejectionTask;
import com.company.hrms.timesheet.application.service.task.RejectTimesheetTask;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.model.entity.TimesheetEntry;
import com.company.hrms.timesheet.domain.model.valueobject.TimesheetStatus;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

@ExtendWith(MockitoExtension.class)
public class RejectTimesheetServiceTest {

    @Mock
    private ITimesheetRepository timesheetRepository;

    private RejectTimesheetServiceImpl service;

    @BeforeEach
    void setUp() {
        LoadTimesheetForRejectionTask loadTask = new LoadTimesheetForRejectionTask(timesheetRepository);
        RejectTimesheetTask rejectTask = new RejectTimesheetTask(timesheetRepository);
        service = new RejectTimesheetServiceImpl(loadTask, rejectTask);
    }

    @Test
    void testReject_Success() throws Exception {
        // Arrange
        UUID timesheetId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        UUID rejectorId = UUID.randomUUID();
        String reason = "Incomplete";
        LocalDate weekStart = LocalDate.now();

        Timesheet timesheet = Timesheet.create(employeeId, weekStart);
        timesheet.addEntry(TimesheetEntry.create(UUID.randomUUID(), null, weekStart, new BigDecimal("8"), "test"));
        timesheet.submit(); // Must be submitted

        when(timesheetRepository.findById(any())).thenReturn(Optional.of(timesheet));

        RejectTimesheetRequest request = new RejectTimesheetRequest();
        request.setTimesheetId(timesheetId);
        request.setReason(reason);

        JWTModel currentUser = JWTModel.builder().userId(rejectorId.toString()).build();

        // Act
        RejectTimesheetResponse response = service.execCommand(request, currentUser, "");

        // Assert
        assertEquals(TimesheetStatus.REJECTED, response.getStatus());
        assertEquals(reason, response.getReason());

        verify(timesheetRepository).save(timesheet);
    }

    @Test
    void testReject_Fail_WrongStatus() {
        // Arrange
        UUID timesheetId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        Timesheet timesheet = Timesheet.create(employeeId, LocalDate.now());
        // Status is DRAFT

        when(timesheetRepository.findById(any())).thenReturn(Optional.of(timesheet));

        RejectTimesheetRequest request = new RejectTimesheetRequest();
        request.setTimesheetId(timesheetId);
        request.setReason("test");
        JWTModel currentUser = JWTModel.builder().userId(UUID.randomUUID().toString()).build();

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            service.execCommand(request, currentUser, "");
        });

        // Pipeline wraps exceptions
        assertEquals(PipelineExecutionException.class, exception.getClass());
        assertEquals(DomainException.class, exception.getCause().getClass());
    }
}
