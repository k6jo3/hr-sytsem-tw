package com.company.hrms.performance.api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.company.hrms.common.api.response.PageResponse;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.performance.api.request.GetMyReviewsRequest;
import com.company.hrms.performance.api.request.GetReviewDetailRequest;
import com.company.hrms.performance.api.request.GetTeamReviewsRequest;
import com.company.hrms.performance.api.response.GetReviewsResponse;
import com.company.hrms.performance.application.service.GetMyReviewsServiceImpl;
import com.company.hrms.performance.application.service.GetReviewDetailServiceImpl;
import com.company.hrms.performance.application.service.GetTeamReviewsServiceImpl;
import com.company.hrms.performance.domain.model.valueobject.ReviewStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("HR08 考核查詢 Controller 測試")
class HR08ReviewQryControllerTest extends BaseApiContractTest {

        @MockBean(name = "getMyReviewsServiceImpl")
        private GetMyReviewsServiceImpl getMyReviewsService;

        @MockBean(name = "getTeamReviewsServiceImpl")
        private GetTeamReviewsServiceImpl getTeamReviewsService;

        @MockBean(name = "getReviewDetailServiceImpl")
        private GetReviewDetailServiceImpl getReviewDetailService;

        @BeforeEach
        void setupSecurity() {
                JWTModel mockUser = new JWTModel();
                mockUser.setUserId("admin-user");
                mockUser.setUsername("admin");
                mockUser.setRoles(Collections.singletonList("ADMIN"));

                List<SimpleGrantedAuthority> authorities = mockUser.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(mockUser,
                                null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        @Test
        @DisplayName("查詢我的考核 - 成功")
        void getMyReviews_ShouldReturnOk() throws Exception {
                // Arrange
                GetReviewsResponse.ReviewSummary summary = GetReviewsResponse.ReviewSummary.builder()
                                .reviewId("REVIEW-001")
                                .status(ReviewStatus.PENDING_SELF)
                                .build();

                PageResponse<GetReviewsResponse.ReviewSummary> response = PageResponse.of(
                                Collections.singletonList(summary), 1, 20, 1);

                when(getMyReviewsService.getResponse(any(GetMyReviewsRequest.class), any(JWTModel.class)))
                                .thenReturn(response);

                // Act & Assert
                performGet("/api/v1/performance/reviews/my?page=1&size=20")
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("查詢團隊考核 - 成功")
        void getTeamReviews_ShouldReturnOk() throws Exception {
                // Arrange
                GetReviewsResponse.ReviewSummary summary = GetReviewsResponse.ReviewSummary.builder()
                                .reviewId("REVIEW-002")
                                .status(ReviewStatus.PENDING_MANAGER)
                                .build();

                PageResponse<GetReviewsResponse.ReviewSummary> response = PageResponse.of(
                                Collections.singletonList(summary), 1, 20, 1);

                when(getTeamReviewsService.getResponse(any(GetTeamReviewsRequest.class), any(JWTModel.class)))
                                .thenReturn(response);

                // Act & Assert
                performGet("/api/v1/performance/reviews/team?page=1&size=20")
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("查詢考核詳情 - 成功")
        void getReviewDetail_ShouldReturnOk() throws Exception {
                // Arrange
                GetReviewsResponse.ReviewSummary response = GetReviewsResponse.ReviewSummary.builder()
                                .reviewId("REVIEW-001")
                                .status(ReviewStatus.PENDING_SELF)
                                .build();

                when(getReviewDetailService.getResponse(any(GetReviewDetailRequest.class), any(JWTModel.class)))
                                .thenReturn(response);

                // Act & Assert
                performGet("/api/v1/performance/reviews/REVIEW-001")
                                .andExpect(status().isOk());
        }
}
