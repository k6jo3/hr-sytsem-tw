package com.company.hrms.timesheet.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.timesheet.api.request.GetProjectTimesheetSummaryRequest;
import com.company.hrms.timesheet.api.response.GetProjectTimesheetSummaryResponse;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.model.entity.TimesheetEntry;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

@ExtendWith(MockitoExtension.class)
class GetProjectTimesheetSummaryServiceTest {

    @Mock
    private ITimesheetRepository timesheetRepository;

    private GetProjectTimesheetSummaryServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new GetProjectTimesheetSummaryServiceImpl(timesheetRepository);
    }

    @Test
    @DisplayName("Should aggregate hours by project correctly")
    void shouldAggregateHoursByProject() throws Exception {
        // Given
        UUID project1Id = UUID.randomUUID();
        UUID project2Id = UUID.randomUUID();
        UUID employee1Id = UUID.randomUUID();

        // Create Timesheet 1
        Timesheet ts1 = Timesheet.create(employee1Id, LocalDate.of(2025, 1, 1));
        ts1.addEntry(TimesheetEntry.create(project1Id, UUID.randomUUID(), LocalDate.of(2025, 1, 1), new BigDecimal("4"),
                "Work P1"));
        ts1.addEntry(TimesheetEntry.create(project2Id, UUID.randomUUID(), LocalDate.of(2025, 1, 1), new BigDecimal("4"),
                "Work P2"));

        // Create Timesheet 2 (same employee, next day - simplified for test, usually
        // strict weekly)
        // Just adding entry to same TS logic simulation or new TS
        Timesheet ts2 = Timesheet.create(employee1Id, LocalDate.of(2025, 1, 8)); // different week
        ts2.addEntry(TimesheetEntry.create(project1Id, UUID.randomUUID(), LocalDate.of(2025, 1, 8), new BigDecimal("2"),
                "More Work P1"));

        when(timesheetRepository.findAll(any(QueryGroup.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(ts1, ts2)));

        GetProjectTimesheetSummaryRequest request = new GetProjectTimesheetSummaryRequest();
        request.setStartDate(LocalDate.of(2025, 1, 1));
        request.setEndDate(LocalDate.of(2025, 1, 31));

        // When
        GetProjectTimesheetSummaryResponse response = service.getResponse(request, null);

        // Then
        assertThat(response.getProjects()).hasSize(2);

        var p1Summary = response.getProjects().stream().filter(p -> p.getProjectId().equals(project1Id)).findFirst()
                .get();
        assertThat(p1Summary.getTotalHours()).isEqualByComparingTo(new BigDecimal("6"));

        var p2Summary = response.getProjects().stream().filter(p -> p.getProjectId().equals(project2Id)).findFirst()
                .get();
        assertThat(p2Summary.getTotalHours()).isEqualByComparingTo(new BigDecimal("4"));
    }

    @Test
    @DisplayName("Should filter by projectId if specified")
    void shouldFilterByProjectId() throws Exception {
        // Given
        UUID project1Id = UUID.randomUUID();
        UUID project2Id = UUID.randomUUID();
        UUID employee1Id = UUID.randomUUID();

        Timesheet ts1 = Timesheet.create(employee1Id, LocalDate.of(2025, 1, 1));
        ts1.addEntry(TimesheetEntry.create(project1Id, UUID.randomUUID(), LocalDate.of(2025, 1, 1), new BigDecimal("4"),
                "Work P1"));
        ts1.addEntry(TimesheetEntry.create(project2Id, UUID.randomUUID(), LocalDate.of(2025, 1, 1), new BigDecimal("4"),
                "Work P2"));

        // When filtering by Project 1, we still get the whole Timesheet from DB (in
        // mock), but service aggregation should skip P2
        when(timesheetRepository.findAll(any(QueryGroup.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(ts1)));

        GetProjectTimesheetSummaryRequest request = new GetProjectTimesheetSummaryRequest();
        request.setProjectId(project1Id);

        // When
        GetProjectTimesheetSummaryResponse response = service.getResponse(request, null);

        // Then
        assertThat(response.getProjects()).hasSize(1);
        assertThat(response.getProjects().get(0).getProjectId()).isEqualTo(project1Id);
        assertThat(response.getProjects().get(0).getTotalHours()).isEqualByComparingTo(new BigDecimal("4"));
    }
}
