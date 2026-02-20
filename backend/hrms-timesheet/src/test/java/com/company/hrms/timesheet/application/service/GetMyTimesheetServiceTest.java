package com.company.hrms.timesheet.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.timesheet.api.request.GetMyTimesheetRequest;
import com.company.hrms.timesheet.api.response.GetMyTimesheetResponse;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class GetMyTimesheetServiceTest {

    @Mock
    private ITimesheetRepository timesheetRepository;

    @InjectMocks
    private GetMyTimesheetServiceImpl getMyTimesheetService;

    private JWTModel currentUser;
    private Timesheet timesheet;

    @BeforeEach
    public void setUp() {
        currentUser = new JWTModel();
        currentUser.setUserId(UUID.randomUUID().toString());
        // currentUser.setUserName("Test User");

        timesheet = Timesheet.create(UUID.fromString(currentUser.getUserId()), java.time.LocalDate.now());
    }

    @Test
    public void getResponse_ShouldReturnList() throws Exception {
        GetMyTimesheetRequest request = new GetMyTimesheetRequest();
        request.setPage(1);
        request.setSize(10);

        Page<Timesheet> page = new PageImpl<>(Collections.singletonList(timesheet));
        when(timesheetRepository.findAll(any(QueryGroup.class), any(Pageable.class))).thenReturn(page);

        GetMyTimesheetResponse response = getMyTimesheetService.getResponse(request, currentUser);

        assertEquals(1, response.getItems().size());
        assertEquals(timesheet.getId().toString(), response.getItems().get(0).getTimesheetId());
    }
}
