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

import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.timesheet.api.request.SubmitTimesheetRequest;
import com.company.hrms.timesheet.api.response.SubmitTimesheetResponse;
import com.company.hrms.timesheet.application.service.task.LoadTimesheetForSubmissionTask;
import com.company.hrms.timesheet.application.service.task.SubmitTimesheetTask;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.model.entity.TimesheetEntry;
import com.company.hrms.timesheet.domain.model.valueobject.TimesheetStatus;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

@ExtendWith(MockitoExtension.class)
public class SubmitTimesheetServiceTest {

    @Mock
    private ITimesheetRepository timesheetRepository;

    private SubmitTimesheetServiceImpl service;

    @BeforeEach
    void setUp() {
        LoadTimesheetForSubmissionTask loadTask = new LoadTimesheetForSubmissionTask(timesheetRepository);
        SubmitTimesheetTask submitTask = new SubmitTimesheetTask(timesheetRepository);
        service = new SubmitTimesheetServiceImpl(loadTask, submitTask);
    }

    @Test
    void testSubmit_Success() throws Exception {
        // Arrange
        UUID timesheetId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        LocalDate weekStart = LocalDate.now();
        Timesheet timesheet = Timesheet.create(employeeId, weekStart);
        // Add entry to allow submission
        timesheet.addEntry(TimesheetEntry.create(UUID.randomUUID(), weekStart, new BigDecimal("8"), "test"));

        when(timesheetRepository.findById(any())).thenReturn(Optional.of(timesheet));

        SubmitTimesheetRequest request = new SubmitTimesheetRequest();
        request.setTimesheetId(timesheetId);

        // Act
        SubmitTimesheetResponse response = service.execCommand(request, new JWTModel(), "");

        // Assert
        assertEquals(TimesheetStatus.SUBMITTED, response.getStatus());
        assertNotNull(response.getSubmittedAt());

        verify(timesheetRepository).save(any(Timesheet.class));
    }

    @Test
    void testSubmit_NotFound() {
        UUID timesheetId = UUID.randomUUID();
        when(timesheetRepository.findById(any())).thenReturn(Optional.empty());

        SubmitTimesheetRequest request = new SubmitTimesheetRequest();
        request.setTimesheetId(timesheetId);

        Exception exception = assertThrows(Exception.class, () -> {
            service.execCommand(request, new JWTModel(), "");
        });

        // Pipeline wraps exceptions
        assertEquals(com.company.hrms.common.application.pipeline.PipelineExecutionException.class,
                exception.getClass());
        assertEquals(EntityNotFoundException.class, exception.getCause().getClass());
    }
}
