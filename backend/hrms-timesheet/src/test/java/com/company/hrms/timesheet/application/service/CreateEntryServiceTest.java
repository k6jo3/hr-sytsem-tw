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

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.timesheet.api.request.CreateEntryRequest;
import com.company.hrms.timesheet.api.response.CreateEntryResponse;
import com.company.hrms.timesheet.application.service.task.GetOrCreateTimesheetTask;
import com.company.hrms.timesheet.application.service.task.SaveEntryTask;
import com.company.hrms.timesheet.application.service.task.ValidateEntryTask;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.model.valueobject.TimesheetStatus;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

@ExtendWith(MockitoExtension.class)
public class CreateEntryServiceTest {

    @Mock
    private ITimesheetRepository timesheetRepository;

    @Mock
    private com.company.hrms.timesheet.infrastructure.client.ProjectServiceClient projectServiceClient;

    private CreateEntryServiceImpl createEntryService;
    private GetOrCreateTimesheetTask getOrCreateTimesheetTask;
    private ValidateEntryTask validateEntryTask;
    private SaveEntryTask saveEntryTask;

    @BeforeEach
    void setUp() {
        // Initialize Real Tasks with Mocked Repo
        // ValidateEntryTask now needs ProjectServiceClient
        validateEntryTask = new ValidateEntryTask(projectServiceClient);
        getOrCreateTimesheetTask = new GetOrCreateTimesheetTask(timesheetRepository);
        saveEntryTask = new SaveEntryTask(timesheetRepository);

        createEntryService = new CreateEntryServiceImpl(
                getOrCreateTimesheetTask,
                validateEntryTask,
                saveEntryTask);
    }

    @Test
    void testCreateEntry_NewTimesheet() throws Exception {
        // Arrange
        UUID employeeId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        LocalDate workDate = LocalDate.now();

        CreateEntryRequest request = new CreateEntryRequest();
        request.setEmployeeId(employeeId);
        request.setProjectId(projectId);
        request.setWorkDate(workDate);
        request.setHours(new BigDecimal("4.5"));
        request.setDescription("Test Work");

        // Mock: No existing timesheet for this week
        when(timesheetRepository.findByEmployeeAndWeek(any(), any()))
                .thenReturn(Optional.empty());

        // Mock Project Service - Success
        com.company.hrms.timesheet.infrastructure.client.dto.ProjectDto pDto = new com.company.hrms.timesheet.infrastructure.client.dto.ProjectDto();
        pDto.setStatus("IN_PROGRESS");
        pDto.setProjectId(projectId);
        when(projectServiceClient.getProjectDetail(any())).thenReturn(org.springframework.http.ResponseEntity.ok(pDto));

        // Act
        CreateEntryResponse response = createEntryService.execCommand(request, new JWTModel(), "");

        // Assert
        assertNotNull(response);
        assertNotNull(response.getTimesheetId());
        assertEquals(new BigDecimal("4.5"), response.getTotalHours());
        assertEquals(TimesheetStatus.DRAFT, response.getStatus());

        // Verify save was called
        verify(timesheetRepository).save(any(Timesheet.class));
    }

    @Test
    void testCreateEntry_ExistingTimesheet() throws Exception {
        // Arrange
        UUID employeeId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        LocalDate workDate = LocalDate.now();
        LocalDate weekStart = workDate
                .with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));

        // Existing Timesheet
        Timesheet existingTimesheet = Timesheet.create(employeeId, weekStart);

        CreateEntryRequest request = new CreateEntryRequest();
        request.setEmployeeId(employeeId);
        request.setProjectId(projectId);
        request.setWorkDate(weekStart.plusDays(1)); // Tuesday
        request.setHours(new BigDecimal("8.0"));
        request.setDescription("Existing Timesheet Work");

        // Mock: Return existing
        when(timesheetRepository.findByEmployeeAndWeek(any(), any()))
                .thenReturn(Optional.of(existingTimesheet));

        // Mock Project Service - Success
        com.company.hrms.timesheet.infrastructure.client.dto.ProjectDto pDto = new com.company.hrms.timesheet.infrastructure.client.dto.ProjectDto();
        pDto.setStatus("IN_PROGRESS");
        pDto.setProjectId(projectId);
        when(projectServiceClient.getProjectDetail(any())).thenReturn(org.springframework.http.ResponseEntity.ok(pDto));

        // Act
        CreateEntryResponse response = createEntryService.execCommand(request, new JWTModel(), "");

        // Assert
        assertNotNull(response);
        assertEquals(existingTimesheet.getId().getValue(), response.getTimesheetId());
        assertEquals(new BigDecimal("8.0"), response.getTotalHours());

        verify(timesheetRepository).save(any(Timesheet.class));
    }
}
