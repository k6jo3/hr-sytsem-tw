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

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.performance.api.request.FinalizeReviewRequest;
import com.company.hrms.performance.api.request.SubmitReviewRequest;
import com.company.hrms.performance.api.response.SuccessResponse;
import com.company.hrms.performance.application.service.FinalizeReviewServiceImpl;
import com.company.hrms.performance.application.service.SubmitReviewServiceImpl;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("HR08 考核維護 Controller 測試")
class HR08ReviewCmdControllerTest extends BaseApiContractTest {

    @MockBean(name = "submitReviewServiceImpl")
    private SubmitReviewServiceImpl submitReviewService;

    @MockBean(name = "finalizeReviewServiceImpl")
    private FinalizeReviewServiceImpl finalizeReviewService;

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
    @DisplayName("提交考核 - 成功")
    void submitReview_ShouldReturnOk() throws Exception {
        // Arrange
        SubmitReviewRequest request = SubmitReviewRequest.builder()
                .reviewId("REVIEW-001")
                .selfComments("自我評價")
                .build();

        SuccessResponse response = new SuccessResponse("提交成功", true);

        when(submitReviewService.execCommand(any(SubmitReviewRequest.class), any(JWTModel.class)))
                .thenReturn(response);

        // Act & Assert
        performPost("/api/v1/performance/reviews/REVIEW-001/submit", request)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("確認最終評等 - 成功")
    void finalizeReview_ShouldReturnOk() throws Exception {
        // Arrange
        FinalizeReviewRequest request = FinalizeReviewRequest.builder()
                .reviewId("REVIEW-001")
                .finalScore(85.0)
                .finalGrade("A")
                .hrComments("HR意見")
                .build();

        SuccessResponse response = new SuccessResponse("確認成功", true);

        when(finalizeReviewService.execCommand(any(FinalizeReviewRequest.class), any(JWTModel.class)))
                .thenReturn(response);

        // Act & Assert
        performPut("/api/v1/performance/reviews/REVIEW-001/finalize", request)
                .andExpect(status().isOk());
    }
}
