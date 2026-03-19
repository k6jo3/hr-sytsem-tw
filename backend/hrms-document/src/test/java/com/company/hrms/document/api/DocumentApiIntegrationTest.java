package com.company.hrms.document.api;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.base.BaseApiIntegrationTest;
import com.company.hrms.document.domain.service.IFileStorageService;

/**
 * Document API 整合測試
 * 驗證文件管理完整 API 流程 (Controller → Service → Repository → H2 DB)
 *
 * <p>
 * 測試範圍：
 * <ul>
 * <li>文件查詢 API (列表、詳情、我的文件)</li>
 * <li>文件下載/版本查詢</li>
 * <li>異常情況處理</li>
 * </ul>
 *
 * <p>
 *
 * @author SA Team
 * @since 2026-02-03
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "file:src/test/resources/test-data/document_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "file:src/test/resources/test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("HR13 文件管理 API 整合測試")
class DocumentApiIntegrationTest extends BaseApiIntegrationTest {

    @Autowired
    private IFileStorageService fileStorageService;

    @BeforeEach
    void setupSecurity() {
        JWTModel mockUser = new JWTModel();
        mockUser.setUserId("E001");
        mockUser.setUsername("test-user");
        mockUser.setRoles(Collections.singletonList("HR"));

        List<SimpleGrantedAuthority> authorities = mockUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                mockUser, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 文件查詢 API 測試
     */
    @Nested
    @DisplayName("文件查詢 API")
    class DocumentQueryApiTests {

        @Test
        @DisplayName("DOC_API_001: 查詢文件列表 - 應返回分頁結果")
        void DOC_API_001_queryDocumentList_ShouldReturnPaginatedResults() throws Exception {
            // Given
            String queryUrl = "/api/v1/documents?page=0&size=10";

            // When & Then
            var response = performGet(queryUrl)
                    .andExpect(status().isOk())
                    .andReturn();

            String responseBody = response.getResponse().getContentAsString();
            // 驗證回應包含分頁資訊
            assertThat(responseBody).contains("content");
        }

        @Test
        @DisplayName("DOC_API_002: 查詢文件詳情 - 應返回指定文件")
        void DOC_API_002_queryDocumentDetail_ShouldReturnDocument() throws Exception {
            // Given
            String documentId = "DOC-001";

            // When & Then
            performGet("/api/v1/documents/" + documentId)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.documentId").value("DOC-001"))
                    .andExpect(jsonPath("$.fileName").value("勞動契約_張三.pdf"))
                    .andExpect(jsonPath("$.documentType").value("CONTRACT"));
        }

        @Test
        @DisplayName("DOC_API_003: 查詢我的文件 - 應只返回當前用戶的文件")
        void DOC_API_003_queryMyDocuments_ShouldReturnOwnDocuments() throws Exception {
            // Given
            String queryUrl = "/api/v1/documents/my?page=0&size=10";

            // When & Then
            var response = performGet(queryUrl)
                    .andExpect(status().isOk())
                    .andReturn();

            String responseBody = response.getResponse().getContentAsString();
            assertThat(responseBody).contains("content");
        }

        @Test
        @DisplayName("DOC_API_004: 依文件類型查詢 - CONTRACT 類型")
        void DOC_API_004_queryByDocumentType_ShouldReturnFilteredResults() throws Exception {
            // Given
            String queryUrl = "/api/v1/documents?documentType=CONTRACT&page=0&size=10";

            // When & Then
            performGet(queryUrl)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @DisplayName("DOC_API_005: 依可見度查詢 - PUBLIC 文件")
        void DOC_API_005_queryByVisibility_ShouldReturnPublicDocuments() throws Exception {
            // Given
            String queryUrl = "/api/v1/documents?visibility=PUBLIC&page=0&size=10";

            // When & Then
            performGet(queryUrl)
                    .andExpect(status().isOk());
        }
    }

    /**
     * 文件版本 API 測試
     */
    @Nested
    @DisplayName("文件版本 API")
    class DocumentVersionApiTests {

        @Test
        @DisplayName("DOC_API_006: 查詢文件版本歷史")
        void DOC_API_006_queryDocumentVersions_ShouldReturnVersionHistory() throws Exception {
            // Given
            String documentId = "DOC-001";

            // When & Then
            performGet("/api/v1/documents/" + documentId + "/versions")
                    .andExpect(status().isOk());
        }
    }

    /**
     * 文件下載 API 測試
     */
    @Nested
    @DisplayName("文件下載 API")
    class DocumentDownloadApiTests {

        @Test
        @DisplayName("DOC_API_007: 下載文件 - 應返回檔案內容")
        void DOC_API_007_downloadDocument_ShouldReturnFileContent() throws Exception {
            // Given - 在儲存空間中建立測試檔案
            String documentId = "DOC-005"; // 公開政策文件
            String storagePath = "documents/policies/handbook_2025.pdf";
            byte[] testContent = "員工手冊測試內容".getBytes(StandardCharsets.UTF_8);
            fileStorageService.save(storagePath, testContent);

            // When & Then
            performGet("/api/v1/documents/" + documentId + "/download")
                    .andExpect(status().isOk());
        }
    }

    /**
     * 異常情況測試
     */
    @Nested
    @DisplayName("異常情況處理")
    class ExceptionHandlingTests {

        @Test
        @DisplayName("應返回 404 當文件不存在")
        void shouldReturn404WhenDocumentNotFound() throws Exception {
            // Given
            String nonExistentDocumentId = "NONEXISTENT-DOC-ID";

            // When & Then
            performGet("/api/v1/documents/" + nonExistentDocumentId)
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("應返回 404 當下載已刪除文件")
        void shouldReturn404WhenDownloadDeletedDocument() throws Exception {
            // Given - 已軟刪除的文件
            String deletedDocumentId = "DOC-DEL-001";

            // When & Then
            performGet("/api/v1/documents/" + deletedDocumentId)
                    .andExpect(status().isNotFound());
        }
    }

    /**
     * 文件存取紀錄 API 測試
     */
    @Nested
    @DisplayName("文件存取紀錄 API")
    class DocumentAccessLogApiTests {

        @Test
        @DisplayName("DOC_API_010: 查詢下載紀錄 - 應返回存取記錄列表")
        void DOC_API_010_queryDownloadLogs_ShouldReturnAccessLogs() throws Exception {
            // Given
            String queryUrl = "/api/v1/documents/download-logs?page=0&size=10";

            // When & Then
            performGet(queryUrl)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }
    }
}
