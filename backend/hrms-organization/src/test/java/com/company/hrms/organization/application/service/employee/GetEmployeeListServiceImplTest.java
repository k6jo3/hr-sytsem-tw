package com.company.hrms.organization.application.service.employee;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import com.company.hrms.common.test.base.BaseServiceTest;
import com.company.hrms.organization.api.request.employee.GetEmployeeListRequest;
import com.company.hrms.organization.application.service.employee.assembler.EmployeeQueryAssembler;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

@DisplayName("GetEmployeeListService 快照測試")
class GetEmployeeListServiceImplTest extends BaseServiceTest<GetEmployeeListServiceImpl> {

    @Mock
    private IEmployeeRepository repository;

    @Spy
    private EmployeeQueryAssembler assembler = new EmployeeQueryAssembler();

    @InjectMocks
    private GetEmployeeListServiceImpl service;

    @Test
    @DisplayName("依部門查詢應產生正確的 QueryGroup")
    void searchByDepartment_ShouldMatchSnapshot() throws Exception {
        // Given
        GetEmployeeListRequest request = GetEmployeeListRequest.builder()
                .deptId("D001")
                .status("ACTIVE")
                .build();

        // Mock Repository behavior to avoid NPE if service calls it
        when(repository.findByQuery(any(), any())).thenReturn(Collections.emptyList());
        when(repository.countByQuery(any())).thenReturn(0L);

        // When
        executeAndCapture(() -> {
            try {
                service.getResponse(request, mockUser);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // Then
        verifyCapturedQuery("employee_search_by_dept.json");
    }

    @Test
    @DisplayName("依姓名模糊查詢應產生正確的 QueryGroup")
    void searchByName_ShouldMatchSnapshot() throws Exception {
        // Given
        GetEmployeeListRequest request = GetEmployeeListRequest.builder()
                .name("John")
                .build();

        when(repository.findByQuery(any(), any())).thenReturn(Collections.emptyList());

        // When
        executeAndCapture(() -> {
            try {
                service.getResponse(request, mockUser);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // Then
        verifyCapturedQuery("employee_search_by_name.json");
    }
}
