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
import com.company.hrms.timesheet.api.request.GetTimesheetSummaryRequest;
import com.company.hrms.timesheet.api.response.GetTimesheetSummaryResponse;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.model.entity.TimesheetEntry;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class GetTimesheetSummaryServiceTest {

    @Mock
    private ITimesheetRepository timesheetRepository;

    private GetTimesheetSummaryServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new GetTimesheetSummaryServiceImpl(timesheetRepository);
    }

    @Test
    @DisplayName("Should aggregate total hours correctly")
    void shouldAggregateTotalHours() throws Exception {
        // Given
        UUID employeeId = UUID.randomUUID();

        // Create Timesheet 1 (APPROVED)
        Timesheet ts1 = Timesheet.create(employeeId, LocalDate.of(2025, 1, 1));
        ts1.addEntry(TimesheetEntry.create(UUID.randomUUID(), UUID.randomUUID(), LocalDate.of(2025, 1, 1),
                new BigDecimal("8"), "Work"));
        ts1.submit();
        ts1.approve(UUID.randomUUID());

        // Create Timesheet 2 (DRAFT)
        Timesheet ts2 = Timesheet.create(employeeId, LocalDate.of(2025, 1, 8));
        ts2.addEntry(TimesheetEntry.create(UUID.randomUUID(), UUID.randomUUID(), LocalDate.of(2025, 1, 8),
                new BigDecimal("4"), "More Work"));

        when(timesheetRepository.findAll(any(QueryGroup.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(ts1, ts2)));

        GetTimesheetSummaryRequest request = new GetTimesheetSummaryRequest();
        request.setStartDate(LocalDate.of(2025, 1, 1));
        request.setEndDate(LocalDate.of(2025, 1, 31));

        // When
        GetTimesheetSummaryResponse response = service.getResponse(request, null);

        // Then
        assertThat(response.getTotalHours()).isEqualByComparingTo(new BigDecimal("12")); // 8 + 4
        assertThat(response.getProjectHours()).isEqualByComparingTo(new BigDecimal("8")); // Only APPROVED
        // Average daily hours: 12 hours / 31 days = 0.39
        assertThat(response.getAverageDailyHours()).isEqualByComparingTo(new BigDecimal("0.39"));
    }
}
