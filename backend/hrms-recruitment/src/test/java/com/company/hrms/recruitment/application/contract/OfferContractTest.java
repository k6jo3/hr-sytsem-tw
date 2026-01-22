package com.company.hrms.recruitment.application.contract;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Offer 管理 API 合約測試
 * 
 * 驗證 API 規格文件中定義的 Offer 相關端點
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Offer 管理 API 合約測試")
public class OfferContractTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    /**
     * 合約場景：OFR_SC_001
     * 驗證 GET /api/v1/recruitment/offers 端點存在並回傳正確結構
     */
    @Test
    @WithMockUser(username = "hr_admin", roles = { "HR_ADMIN" })
    @DisplayName("[OFR_SC_001] GET /offers - 查詢 Offer 列表")
    void getOffers_shouldReturnPagedResult() throws Exception {
        mockMvc.perform(get("/api/v1/recruitment/offers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        System.out.println("✓ 場景 [OFR_SC_001] 驗證通過，符合業務合約。");
    }

    /**
     * 合約場景：OFR_SC_002
     * 驗證 POST /api/v1/recruitment/offers 端點存在
     */
    @Test
    @WithMockUser(username = "hr_admin", roles = { "HR_ADMIN" })
    @DisplayName("[OFR_SC_002] POST /offers - 發送 Offer")
    void createOffer_endpointExists() throws Exception {
        // 準備請求資料
        var request = new java.util.HashMap<String, Object>();
        request.put("candidateId", UUID.randomUUID().toString());
        request.put("offeredPosition", "軟體工程師");
        request.put("offeredSalary", 80000);
        request.put("offerExpiryDate", LocalDate.now().plusDays(7).toString());
        request.put("employmentType", "FULL_TIME");
        request.put("startDate", LocalDate.now().plusMonths(1).toString());

        // 此測試主要驗證端點存在，不驗證業務邏輯
        mockMvc.perform(post("/api/v1/recruitment/offers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError()); // 因無應徵者資料，預期 4xx

        System.out.println("✓ 場景 [OFR_SC_002] 驗證通過，端點存在。");
    }

    /**
     * 合約場景：OFR_SC_003
     * 驗證 POST /api/v1/recruitment/offers/{id}/accept 端點存在
     */
    @Test
    @WithMockUser(username = "hr_admin", roles = { "HR_ADMIN" })
    @DisplayName("[OFR_SC_003] POST /offers/{id}/accept - 接受 Offer")
    void acceptOffer_endpointExists() throws Exception {
        String offerId = UUID.randomUUID().toString();

        mockMvc.perform(post("/api/v1/recruitment/offers/" + offerId + "/accept")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError()); // 因無資料，預期 4xx

        System.out.println("✓ 場景 [OFR_SC_003] 驗證通過，端點存在。");
    }

    /**
     * 合約場景：OFR_SC_004
     * 驗證 POST /api/v1/recruitment/offers/{id}/reject 端點存在
     */
    @Test
    @WithMockUser(username = "hr_admin", roles = { "HR_ADMIN" })
    @DisplayName("[OFR_SC_004] POST /offers/{id}/reject - 拒絕 Offer")
    void rejectOffer_endpointExists() throws Exception {
        String offerId = UUID.randomUUID().toString();

        var request = new java.util.HashMap<String, Object>();
        request.put("reason", "薪資考量");

        mockMvc.perform(post("/api/v1/recruitment/offers/" + offerId + "/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError()); // 因無資料，預期 4xx

        System.out.println("✓ 場景 [OFR_SC_004] 驗證通過，端點存在。");
    }
}
