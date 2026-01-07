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
import com.company.hrms.timesheet.api.request.ApproveTimesheetRequest;
import com.company.hrms.timesheet.api.response.ApproveTimesheetResponse;
import com.company.hrms.timesheet.application.service.task.ApproveTimesheetTask;
import com.company.hrms.timesheet.application.service.task.LoadTimesheetForApprovalTask;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.model.entity.TimesheetEntry;
import com.company.hrms.timesheet.domain.model.valueobject.TimesheetStatus;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

@ExtendWith(MockitoExtension.class)
public class ApproveTimesheetServiceTest {

    @Mock
    private ITimesheetRepository timesheetRepository;

    private ApproveTimesheetServiceImpl service;

    @BeforeEach
    void setUp() {
        LoadTimesheetForApprovalTask loadTask = new LoadTimesheetForApprovalTask(timesheetRepository);
        ApproveTimesheetTask approveTask = new ApproveTimesheetTask(timesheetRepository);
        service = new ApproveTimesheetServiceImpl(loadTask, approveTask);
    }

    @Test
    void testApprove_Success() throws Exception {
        // Arrange
        UUID timesheetId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        UUID approverId = UUID.randomUUID();
        LocalDate weekStart = LocalDate.now();

        Timesheet timesheet = Timesheet.create(employeeId, weekStart);
        timesheet.addEntry(TimesheetEntry.create(UUID.randomUUID(), null, weekStart, new BigDecimal("8"), "test"));
        timesheet.submit(); // Must be submitted

        when(timesheetRepository.findById(any())).thenReturn(Optional.of(timesheet));

        ApproveTimesheetRequest request = new ApproveTimesheetRequest();
        request.setTimesheetId(timesheetId);

        JWTModel currentUser = JWTModel.builder().userId(approverId.toString()).build();

        // Act
        ApproveTimesheetResponse response = service.execCommand(request, currentUser, "");

        // Assert
        assertEquals(TimesheetStatus.APPROVED, response.getStatus());
        assertEquals(approverId, response.getApprovedBy());
        assertNotNull(response.getApprovedAt());

        verify(timesheetRepository).save(timesheet);
    }

    @Test
    void testApprove_Fail_WrongStatus() {
        // Arrange
        UUID timesheetId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        Timesheet timesheet = Timesheet.create(employeeId, LocalDate.now());
        // Status is DRAFT

        when(timesheetRepository.findById(any())).thenReturn(Optional.of(timesheet));

        ApproveTimesheetRequest request = new ApproveTimesheetRequest();
        request.setTimesheetId(timesheetId);
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
