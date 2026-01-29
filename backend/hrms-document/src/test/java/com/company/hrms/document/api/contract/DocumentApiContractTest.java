package com.company.hrms.document.api.contract;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import com.company.hrms.common.test.contract.BaseContractTest;
import com.company.hrms.document.api.request.GetDocumentAccessLogListRequest;
import com.company.hrms.document.api.request.GetDocumentListRequest;
import com.company.hrms.document.api.request.GetDocumentTemplateListRequest;
import com.company.hrms.document.api.request.GetDocumentVersionListRequest;
import com.company.hrms.document.application.assembler.DocumentAccessLogListQueryAssembler;
import com.company.hrms.document.application.assembler.DocumentListQueryAssembler;
import com.company.hrms.document.application.assembler.DocumentTemplateListQueryAssembler;
import com.company.hrms.document.application.assembler.DocumentVersionListQueryAssembler;

@ActiveProfiles("test")
@DisplayName("HR13 文件管理服務 API 合約測試")
public class DocumentApiContractTest extends BaseContractTest {

    private static final String CONTRACT = "document";
    private String contractSpec;

    @BeforeEach
    void setUp() throws Exception {
        contractSpec = loadContractSpec(CONTRACT);
    }

    @Nested
    @DisplayName("文件查詢合約 (Document Query Contract)")
    class DocumentQueryContractTests {

        private final DocumentListQueryAssembler assembler = new DocumentListQueryAssembler();

        /**
         * DOC_D001: 查詢資料夾內文件
         */
        @Test
        @DisplayName("DOC_D001: 查詢資料夾內文件")
        void searchDocumentsInFolder_DOC_D001() throws Exception {
            // Given
            var request = GetDocumentListRequest.builder()
                    .folderId("F001")
                    .accessibleVisibilities(java.util.List.of("PUBLIC", "SHARED", "DEPARTMENT")) // Match contract
                                                                                                 // placeholder
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            System.out.println("DEBUG QUERY DOC_D001: " + query);
            assertContract(query, contractSpec, "DOC_D001");
        }

        /**
         * DOC_D002: 依名稱模糊查詢
         */
        @Test
        @DisplayName("DOC_D002: 依名稱模糊查詢")
        void searchByName_DOC_D002() throws Exception {
            // Given
            var request = GetDocumentListRequest.builder()
                    .name("報告")
                    .accessibleVisibilities(java.util.List.of("PUBLIC", "SHARED", "DEPARTMENT"))
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            assertContract(query, contractSpec, "DOC_D002");
        }

        /**
         * DOC_D003: 依類型查詢
         */
        @Test
        @DisplayName("DOC_D003: 依類型查詢")
        void searchByType_DOC_D003() throws Exception {
            // Given
            var request = GetDocumentListRequest.builder()
                    .documentType("PDF")
                    .accessibleVisibilities(java.util.List.of("PUBLIC", "SHARED", "DEPARTMENT"))
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            assertContract(query, contractSpec, "DOC_D003");
        }

        /**
         * DOC_D004: 查詢個人文件
         */
        @Test
        @DisplayName("DOC_D004: 查詢個人文件")
        void searchMyDocuments_DOC_D004() throws Exception {
            // Given: currentUserId = "currentUserId" (Matches contract placeholder)
            String currentUserId = "currentUserId";
            var request = GetDocumentListRequest.builder()
                    .ownerId(currentUserId)
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            assertContract(query, contractSpec, "DOC_D004");
        }

        /**
         * DOC_D008: 查詢最近文件
         */
        @Test
        @DisplayName("DOC_D008: 查詢最近文件")
        void searchRecentDocuments_DOC_D008() throws Exception {
            // Given
            String currentUserId = "currentUserId";
            LocalDate daysAgo = LocalDate.of(2025, 1, 1);

            var request = GetDocumentListRequest.builder()
                    .ownerId(currentUserId)
                    .startDate(daysAgo)
                    .accessibleVisibilities(java.util.List.of("PUBLIC", "SHARED", "DEPARTMENT"))
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            assertContract(query, contractSpec, "DOC_D008");
        }

        /**
         * DOC_D005: 查詢共享文件
         */
        @Test
        @DisplayName("DOC_D005: 查詢共享文件")
        void searchSharedDocuments_DOC_D005() throws Exception {
            // Given
            var request = GetDocumentListRequest.builder()
                    .visibility("SHARED")
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            assertContract(query, contractSpec, "DOC_D005");
        }

        /**
         * DOC_D006: 查詢公開文件
         */
        @Test
        @DisplayName("DOC_D006: 查詢公開文件")
        void searchPublicDocuments_DOC_D006() throws Exception {
            // Given
            var request = GetDocumentListRequest.builder()
                    .visibility("PUBLIC")
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            assertContract(query, contractSpec, "DOC_D006");
        }

        /**
         * DOC_D007: 依標籤查詢
         */
        @Test
        @DisplayName("DOC_D007: 依標籤查詢")
        void searchByTag_DOC_D007() throws Exception {
            // Given
            var request = GetDocumentListRequest.builder()
                    .tag("合約")
                    .accessibleVisibilities(java.util.List.of("PUBLIC", "SHARED", "DEPARTMENT"))
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            assertContract(query, contractSpec, "DOC_D007");
        }

        /**
         * DOC_D009: HR 查詢全部文件
         */
        @Test
        @DisplayName("DOC_D009: HR 查詢全部文件")
        void searchAllDocumentsByHR_DOC_D009() throws Exception {
            // Given
            var request = GetDocumentListRequest.builder()
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            assertContract(query, contractSpec, "DOC_D009");
        }

        /**
         * DOC_D010: 查詢機密文件
         */
        @Test
        @DisplayName("DOC_D010: 查詢機密文件")
        void searchConfidentialDocuments_DOC_D010() throws Exception {
            // Given
            var request = GetDocumentListRequest.builder()
                    .classification("CONFIDENTIAL")
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            assertContract(query, contractSpec, "DOC_D010");
        }
    }

    @Nested
    @DisplayName("資料夾查詢合約 (Folder Query Contract)")
    class FolderQueryContractTests {

        private final DocumentListQueryAssembler assembler = new DocumentListQueryAssembler();

        /**
         * DOC_F001: 查詢根資料夾
         */
        @Test
        @DisplayName("DOC_F001: 查詢根資料夾")
        void searchRootFolders_DOC_F001() throws Exception {
            // Given
            var request = GetDocumentListRequest.builder()
                    .parentId(DocumentListQueryAssembler.NULL_MARKER)
                    .accessibleVisibilities(java.util.List.of("PUBLIC", "SHARED", "DEPARTMENT"))
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            assertContract(query, contractSpec, "DOC_F001");
        }

        /**
         * DOC_F002: 查詢子資料夾
         */
        @Test
        @DisplayName("DOC_F002: 查詢子資料夾")
        void searchSubFolders_DOC_F002() throws Exception {
            // Given
            var request = GetDocumentListRequest.builder()
                    .parentId("F001")
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            assertContract(query, contractSpec, "DOC_F002");
        }

        /**
         * DOC_F003: 查詢個人資料夾
         */
        @Test
        @DisplayName("DOC_F003: 查詢個人資料夾")
        void searchMyFolders_DOC_F003() throws Exception {
            // Given
            String currentUserId = "currentUserId";
            var request = GetDocumentListRequest.builder()
                    .ownerId(currentUserId)
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            assertContract(query, contractSpec, "DOC_F003");
        }

        /**
         * DOC_F004: 依名稱查詢
         */
        @Test
        @DisplayName("DOC_F004: 依名稱查詢")
        void searchFoldersByName_DOC_F004() throws Exception {
            // Given
            var request = GetDocumentListRequest.builder()
                    .name("專案")
                    .accessibleVisibilities(java.util.List.of("PUBLIC", "SHARED", "DEPARTMENT"))
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            assertContract(query, contractSpec, "DOC_F004");
        }
    }

    @Nested
    @DisplayName("文件版本查詢合約 (Document Version Query Contract)")
    class DocumentVersionQueryContractTests {

        private final DocumentVersionListQueryAssembler assembler = new DocumentVersionListQueryAssembler();

        /**
         * DOC_V001: 查詢文件版本
         */
        @Test
        @DisplayName("DOC_V001: 查詢文件版本")
        void searchDocumentVersions_DOC_V001() throws Exception {
            // Given
            var request = GetDocumentVersionListRequest.builder()
                    .documentId("D001")
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            assertContract(query, contractSpec, "DOC_V001");
        }

        /**
         * DOC_V002: 查詢最新版本
         */
        @Test
        @DisplayName("DOC_V002: 查詢最新版本")
        void searchLatestVersion_DOC_V002() throws Exception {
            // Given
            var request = GetDocumentVersionListRequest.builder()
                    .documentId("D001")
                    .isLatest(true)
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            assertContract(query, contractSpec, "DOC_V002");
        }

        /**
         * DOC_V003: 依版本號查詢
         */
        @Test
        @DisplayName("DOC_V003: 依版本號查詢")
        void searchByVersionNumber_DOC_V003() throws Exception {
            // Given
            var request = GetDocumentVersionListRequest.builder()
                    .documentId("D001")
                    .version("2.0")
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            assertContract(query, contractSpec, "DOC_V003");
        }

        /**
         * DOC_V004: 依上傳者查詢
         */
        @Test
        @DisplayName("DOC_V004: 依上傳者查詢")
        void searchByUploader_DOC_V004() throws Exception {
            // Given
            var request = GetDocumentVersionListRequest.builder()
                    .uploaderId("E001")
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            assertContract(query, contractSpec, "DOC_V004");
        }
    }

    @Nested
    @DisplayName("文件範本查詢合約 (Document Template Query Contract)")
    class DocumentTemplateQueryContractTests {

        private final DocumentTemplateListQueryAssembler assembler = new DocumentTemplateListQueryAssembler();

        /**
         * DOC_T001: 查詢啟用範本
         */
        @Test
        @DisplayName("DOC_T001: 查詢啟用範本")
        void searchActiveTemplates_DOC_T001() throws Exception {
            // Given
            var request = GetDocumentTemplateListRequest.builder()
                    .status("ACTIVE")
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            assertContract(query, contractSpec, "DOC_T001");
        }

        /**
         * DOC_T002: 依類型查詢範本
         */
        @Test
        @DisplayName("DOC_T002: 依類型查詢範本")
        void searchTemplatesByCategory_DOC_T002() throws Exception {
            // Given
            var request = GetDocumentTemplateListRequest.builder()
                    .category("CONTRACT")
                    .status("ACTIVE")
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            assertContract(query, contractSpec, "DOC_T002");
        }

        /**
         * DOC_T003: 依名稱模糊查詢
         */
        @Test
        @DisplayName("DOC_T003: 依名稱模糊查詢")
        void searchTemplatesByName_DOC_T003() throws Exception {
            // Given
            var request = GetDocumentTemplateListRequest.builder()
                    .name("勞動")
                    .status("ACTIVE")
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            assertContract(query, contractSpec, "DOC_T003");
        }

        /**
         * DOC_T004: 查詢部門範本
         */
        @Test
        @DisplayName("DOC_T004: 查詢部門範本")
        void searchDepartmentTemplates_DOC_T004() throws Exception {
            // Given
            var request = GetDocumentTemplateListRequest.builder()
                    .deptId("D001")
                    .status("ACTIVE")
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            assertContract(query, contractSpec, "DOC_T004");
        }

        /**
         * DOC_T005: HR 查詢全部範本
         */
        @Test
        @DisplayName("DOC_T005: HR 查詢全部範本")
        void searchAllTemplatesByHR_DOC_T005() throws Exception {
            // Given
            var request = GetDocumentTemplateListRequest.builder()
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            assertContract(query, contractSpec, "DOC_T005");
        }
    }

    @Nested
    @DisplayName("文件存取紀錄查詢合約 (Document Access Log Query Contract)")
    class DocumentAccessLogQueryContractTests {

        private final DocumentAccessLogListQueryAssembler assembler = new DocumentAccessLogListQueryAssembler();

        /**
         * DOC_L001: 查詢文件存取紀錄
         */
        @Test
        @DisplayName("DOC_L001: 查詢文件存取紀錄")
        void searchDocumentAccessLogs_DOC_L001() throws Exception {
            // Given
            var request = GetDocumentAccessLogListRequest.builder()
                    .documentId("D001")
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            assertContract(query, contractSpec, "DOC_L001");
        }

        /**
         * DOC_L002: 依使用者查詢
         */
        @Test
        @DisplayName("DOC_L002: 依使用者查詢")
        void searchLogsByUser_DOC_L002() throws Exception {
            // Given
            var request = GetDocumentAccessLogListRequest.builder()
                    .userId("E001")
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            assertContract(query, contractSpec, "DOC_L002");
        }

        /**
         * DOC_L003: 依操作類型查詢
         */
        @Test
        @DisplayName("DOC_L003: 依操作類型查詢")
        void searchLogsByAction_DOC_L003() throws Exception {
            // Given
            var request = GetDocumentAccessLogListRequest.builder()
                    .action("DOWNLOAD")
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            assertContract(query, contractSpec, "DOC_L003");
        }

        /**
         * DOC_L004: 依日期範圍查詢
         */
        @Test
        @DisplayName("DOC_L004: 依日期範圍查詢")
        void searchLogsByDateRange_DOC_L004() throws Exception {
            // Given
            var request = GetDocumentAccessLogListRequest.builder()
                    .startDate(java.time.LocalDate.of(2025, 1, 1))
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            assertContract(query, contractSpec, "DOC_L004");
        }

        /**
         * DOC_L005: 員工查詢自己紀錄
         */
        @Test
        @DisplayName("DOC_L005: 員工查詢自己紀錄")
        void searchMyLogs_DOC_L005() throws Exception {
            // Given
            String currentUserId = "currentUserId";
            var request = GetDocumentAccessLogListRequest.builder()
                    .userId(currentUserId)
                    .build();

            // When
            var query = assembler.toQueryGroup(request);

            // Then
            assertContract(query, contractSpec, "DOC_L005");
        }
    }
}
