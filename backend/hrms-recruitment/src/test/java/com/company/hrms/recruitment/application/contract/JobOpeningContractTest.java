package com.company.hrms.recruitment.application.contract;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.recruitment.application.service.GetJobOpeningsServiceImpl;
import com.company.hrms.recruitment.domain.repository.IJobOpeningRepository;

public class JobOpeningContractTest extends BaseApiContractTest {

    @Autowired
    private GetJobOpeningsServiceImpl getJobOpeningsService;

    @MockBean
    private IJobOpeningRepository jobOpeningRepository;

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "admin", roles = { "ADMIN" })
    void searchJobOpening_ByTitle_ShouldMeetContract() throws Exception {
        // 1. Load Contract
        String contractSpec = loadContractSpec("hr09_job");

        // 2. Mock Repository - 使用 PageImpl 而非 Page.empty() 以避免 JSON 序列化問題
        when(jobOpeningRepository.findAll(any(QueryGroup.class), any(Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(
                        java.util.Collections.emptyList(),
                        org.springframework.data.domain.PageRequest.of(0, 10),
                        0));

        // 3. Perform Request (JO_SC_001: jobTitle LIKE 'Engineer')
        performGet("/api/v1/recruitment/jobs?keyword=Engineer")
                .andExpect(status().isOk());

        // 4. Capture QueryGroup
        ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
        verify(jobOpeningRepository).findAll(queryCaptor.capture(), any(Pageable.class));

        // 5. Assert Contract
        assertContract(queryCaptor.getValue(), contractSpec, "JO_SC_001");
    }
}
