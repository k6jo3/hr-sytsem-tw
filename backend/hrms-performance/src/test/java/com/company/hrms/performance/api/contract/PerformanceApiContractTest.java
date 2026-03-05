package com.company.hrms.performance.api.contract;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.common.test.contract.ContractSpec;
import com.company.hrms.performance.domain.repository.IPerformanceCycleRepository;
import com.company.hrms.performance.domain.repository.IPerformanceReviewRepository;

/**
 * HR08 績效管理服務 API 合約測試
 * 驗證 Controller -> Service -> Repository 的 QueryGroup 組裝正確性
 */
// TODO: 以下測試仍有失敗待修復：
// 1. [合約格式] PFM_C001~C003, PFM_R001, PFM_T001：performance_contracts.md 只有 pipe table 格式，缺少 JSON 合約區塊
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("HR08 績效管理服務 API 合約測試")
public class PerformanceApiContractTest extends BaseApiContractTest {

    private static final String CONTRACT = "performance";

    @MockBean
    private IPerformanceCycleRepository cycleRepository;

    @MockBean
    private IPerformanceReviewRepository reviewRepository;

    private JWTModel mockUser;

    @BeforeEach
    void setUp() throws Exception {
        mockUser = new JWTModel();
        mockUser.setUserId("00000000-0000-0000-0000-000000000001");
        mockUser.setUsername("test-user");
        mockUser.setRoles(Collections.singletonList("HR_ADMIN"));

        // 設定 SecurityContext（@CurrentUser 解析器從此取得使用者）
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockUser, null, Collections.emptyList()));

        lenient().when(cycleRepository.findAll(any(QueryGroup.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        lenient().when(cycleRepository.save(any())).thenReturn(null);

        lenient().when(reviewRepository.findAll(any(QueryGroup.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("考核週期 API 合約")
    class CycleApiContractTests {

        @Test
        @DisplayName("PFM_C001: 查詢進行中的考核週期")
        void getCycles_InProgress_ShouldIncludeFilters() throws Exception {
            // Arrange
            ContractSpec contract = loadContract(CONTRACT, "PFM_C001");
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(cycleRepository.findAll(queryCaptor.capture(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));

            // Act
            MvcResult result = mockMvc.perform(get("/api/v1/performance/cycles?status=IN_PROGRESS")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            // Assert
            QueryGroup query = queryCaptor.getValue();
            verifyQueryContract(query, result.getResponse().getContentAsString(), contract);
        }

        @Test
        @DisplayName("PFM_C002: 依類別查詢年度考核週期")
        void getCycles_Annual_ShouldIncludeFilters() throws Exception {
            // Arrange
            ContractSpec contract = loadContract(CONTRACT, "PFM_C002");
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(cycleRepository.findAll(queryCaptor.capture(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));

            // Act
            MvcResult result = mockMvc.perform(get("/api/v1/performance/cycles?cycleType=ANNUAL")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            // Assert
            QueryGroup query = queryCaptor.getValue();
            verifyQueryContract(query, result.getResponse().getContentAsString(), contract);
        }

        @Test
        @DisplayName("PFM_C003: 依年份查詢考核週期")
        void getCycles_ByYear_ShouldIncludeFilters() throws Exception {
            // Arrange
            ContractSpec contract = loadContract(CONTRACT, "PFM_C003");
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(cycleRepository.findAll(queryCaptor.capture(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));

            // Act
            MvcResult result = mockMvc.perform(get("/api/v1/performance/cycles?year=2025")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            // Assert
            QueryGroup query = queryCaptor.getValue();
            verifyQueryContract(query, result.getResponse().getContentAsString(), contract);
        }
    }

    @Nested
    @DisplayName("考核維護 API 合約")
    class ReviewApiContractTests {

        @Test
        @DisplayName("PFM_R001: 查詢我的考核")
        void getMyReviews_ShouldIncludeFilters() throws Exception {
            // Arrange
            ContractSpec contract = loadContract(CONTRACT, "PFM_R001");
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(reviewRepository.findAll(queryCaptor.capture(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));

            // Act
            MvcResult result = mockMvc.perform(get("/api/v1/performance/reviews/my")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            // Assert
            QueryGroup query = queryCaptor.getValue();
            verifyQueryContract(query, result.getResponse().getContentAsString(), contract);
        }

        @Test
        @DisplayName("PFM_T001: 查詢團隊考核")
        void getTeamReviews_ShouldIncludeFilters() throws Exception {
            // Arrange
            ContractSpec contract = loadContract(CONTRACT, "PFM_T001");
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(reviewRepository.findAll(queryCaptor.capture(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));

            // Act
            MvcResult result = mockMvc.perform(get("/api/v1/performance/reviews/team")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            // Assert
            QueryGroup query = queryCaptor.getValue();
            verifyQueryContract(query, result.getResponse().getContentAsString(), contract);
        }
    }
}
