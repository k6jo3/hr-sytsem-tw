package com.company.hrms.organization.api.contract;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import com.company.hrms.organization.domain.repository.IOrganizationRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("HR02 組織員工服務 API 合約測試")
public class OrganizationApiContractTest extends BaseApiContractTest {

    private String contractSpec;

    @MockBean
    private IEmployeeRepository employeeRepository;

    @MockBean
    private IDepartmentRepository departmentRepository;

    @MockBean
    private IOrganizationRepository organizationRepository;

    @BeforeEach
    void setUp() throws Exception {
        contractSpec = loadContractSpec("organization");
    }

    @Test
    @DisplayName("ORG_QRY_E001: 查詢在職員工")
    @WithMockUser(roles = "HR")
    void ORG_QRY_E001_searchActiveEmployees() throws Exception {
        ArgumentCaptor<QueryGroup> queryCaptor = createQueryGroupCaptor();

        when(employeeRepository.findByQuery(queryCaptor.capture(), any(Pageable.class)))
                .thenReturn(Collections.emptyList());
        when(employeeRepository.countByQuery(any(QueryGroup.class)))
                .thenReturn(0L);

        mockMvc.perform(get("/api/v1/employees?status=ACTIVE"))
                .andExpect(status().isOk());

        assertContract(queryCaptor.getValue(), contractSpec, "ORG_QRY_E001");
    }

    @Test
    @DisplayName("Dummy Test")
    void dummy() {
        assertThat(true).isTrue();
    }
}
