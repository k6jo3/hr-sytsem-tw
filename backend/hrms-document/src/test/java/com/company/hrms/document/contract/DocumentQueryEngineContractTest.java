package com.company.hrms.document.contract;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.base.BaseQueryEngineContractTest;
import com.company.hrms.document.domain.model.Document;
import com.company.hrms.document.domain.model.IDocumentRepository;

/**
 * Document QueryEngine 契約測試
 *
 * <p>
 * 驗證 QueryEngine 各種操作符在 Document 實體上的正確運作
 * 使用 H2 資料庫實際執行 SQL 查詢
 *
 * <p>
 * 驗證 QueryEngine 各種操作符在 Document 實體上的正確運作，並支援自動日期與 Enum 轉換。
 * </p>
 *
 * @author SA Team
 * @since 2026-01-29
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:test-data/document_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("Document QueryEngine 契約測試")
class DocumentQueryEngineContractTest extends BaseQueryEngineContractTest<Document> {

        @Autowired
        private IDocumentRepository documentRepository;

        @Override
        protected String getTestDataScript() {
                return "classpath:test-data/document_test_data.sql";
        }

        @Override
        protected Page<Document> executeQuery(QueryGroup query) {
                return documentRepository.findDocuments(query, PageRequest.of(0, 100));
        }

        /**
         * 提供參數化測試案例
         * 格式: (操作符名稱, 欄位名稱, 測試值, 預期結果數量)
         *
         * 測試資料分布 (document_test_data.sql):
         * - 文件類型: CONTRACT=4, POLICY=3, CERTIFICATE=3, PERSONAL=3, REPORT=2
         * - 可見度: PUBLIC=5, PRIVATE=6, DEPARTMENT=4
         * - 分類: CONFIDENTIAL=4, INTERNAL=6, PUBLIC=5
         * - 已加密: true=5, false=10
         * - 已刪除: true=1, false=15
         */
        static Stream<Arguments> operatorTestCases() {
                return Stream.of(
                                // EQ 操作符測試 - 文件類型
                                Arguments.of("EQ", "documentType", "CONTRACT", 5),
                                Arguments.of("EQ", "documentType", "POLICY", 3),
                                Arguments.of("EQ", "documentType", "CERTIFICATE", 3),
                                Arguments.of("EQ", "documentType", "PERSONAL", 3),
                                Arguments.of("EQ", "documentType", "REPORT", 2),

                                // EQ 操作符測試 - 可見度
                                Arguments.of("EQ", "visibility", "PUBLIC", 3),
                                Arguments.of("EQ", "visibility", "PRIVATE", 9),
                                Arguments.of("EQ", "visibility", "DEPARTMENT", 4),

                                // EQ 操作符測試 - 分類
                                Arguments.of("EQ", "classification", "CONFIDENTIAL", 5),
                                Arguments.of("EQ", "classification", "INTERNAL", 7),
                                Arguments.of("EQ", "classification", "PUBLIC", 4),

                                // EQ 操作符測試 - 加密狀態
                                Arguments.of("EQ", "isEncrypted", true, 7),
                                Arguments.of("EQ", "isEncrypted", false, 9),

                                // NE 操作符測試
                                Arguments.of("NE", "documentType", "CONTRACT", 11),
                                Arguments.of("NE", "visibility", "PRIVATE", 7),

                                // IN 操作符測試
                                Arguments.of("IN", "documentType", List.of("CONTRACT", "POLICY"), 8),
                                Arguments.of("IN", "visibility", List.of("PUBLIC", "DEPARTMENT"), 7),
                                Arguments.of("IN", "classification", List.of("CONFIDENTIAL", "INTERNAL"), 12),

                                // NOT_IN 操作符測試
                                Arguments.of("NOT_IN", "documentType", List.of("CONTRACT"), 11),
                                Arguments.of("NOT_IN", "visibility", List.of("PRIVATE"), 7));
        }

        @Nested
        @DisplayName("軟刪除過濾測試")
        class SoftDeleteFilterTests {

                @Test
                @DisplayName("DOC_T005: 查詢未刪除文件 - 應排除已刪除")
                void DOC_T005_QueryActiveDocuments() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("isDeleted", false)
                                        .build();

                        // When
                        Page<Document> result = executeQuery(query);

                        // Then
                        assertThat(result.getContent())
                                        .as("DOC_T005: 應返回所有未刪除的文件")
                                        .hasSize(15)
                                        .allMatch(doc -> !doc.isDeleted());
                }

                @Test
                @DisplayName("查詢已刪除文件")
                void queryDeletedDocuments() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("isDeleted", true)
                                        .build();

                        // When
                        Page<Document> result = executeQuery(query);

                        // Then
                        assertThat(result.getContent())
                                        .as("應返回已刪除的文件")
                                        .hasSize(1)
                                        .allMatch(Document::isDeleted);
                }
        }

        @Nested
        @DisplayName("LIKE 操作符測試")
        class LikeOperatorTests {

                @Test
                @DisplayName("LIKE - 依檔案名稱模糊查詢 (張三)")
                void like_FileName_ZhangSan() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .like("fileName", "%張三%")
                                        .build();

                        // When
                        Page<Document> result = executeQuery(query);

                        // Then
                        assertThat(result.getContent())
                                        .as("應找到檔名包含 '張三' 的文件")
                                        .hasSize(3);
                }

                @Test
                @DisplayName("LIKE - 依檔案名稱模糊查詢 (合約)")
                void like_FileName_Contract() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .like("fileName", "%合約%")
                                        .build();

                        // When
                        Page<Document> result = executeQuery(query);

                        // Then
                        assertThat(result.getContent())
                                        .as("應找到檔名包含 '合約' 的文件")
                                        .hasSize(2);
                }

                @Test
                @DisplayName("LIKE - 依標籤模糊查詢")
                void like_Tags() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .like("tags", "%政策%")
                                        .build();

                        // When
                        Page<Document> result = executeQuery(query);

                        // Then
                        assertThat(result.getContent())
                                        .as("應找到標籤包含 '政策' 的文件")
                                        .hasSize(3);
                }
        }

        @Nested
        @DisplayName("檔案大小查詢測試")
        class FileSizeQueryTests {

                @Test
                @DisplayName("GTE - 查詢大於等於 1MB 的文件")
                void gte_FileSize_1MB() {
                        // Given - 1MB = 1048576 bytes
                        QueryGroup query = QueryBuilder.where()
                                        .gte("fileSize", 1048576L)
                                        .build();

                        // When
                        Page<Document> result = executeQuery(query);

                        // Then
                        assertThat(result.getContent())
                                        .as("應找到檔案大小 >= 1MB 的文件")
                                        .hasSize(5)
                                        .allMatch(doc -> doc.getFileSize() >= 1048576L);
                }

                @Test
                @DisplayName("LT - 查詢小於 512KB 的文件")
                void lt_FileSize_512KB() {
                        // Given - 512KB = 524288 bytes
                        QueryGroup query = QueryBuilder.where()
                                        .lt("fileSize", 524288L)
                                        .build();

                        // When
                        Page<Document> result = executeQuery(query);

                        // Then
                        assertThat(result.getContent())
                                        .as("應找到檔案大小 < 512KB 的文件")
                                        .allMatch(doc -> doc.getFileSize() < 524288L);
                }

                @Test
                @DisplayName("BETWEEN - 查詢特定大小範圍的文件")
                void between_FileSize() {
                        // Given - 200KB ~ 600KB
                        QueryGroup query = QueryBuilder.where()
                                        .between("fileSize", 200000L, 600000L)
                                        .build();

                        // When
                        Page<Document> result = executeQuery(query);

                        // Then
                        assertThat(result.getContent())
                                        .as("應找到檔案大小在 200KB ~ 600KB 之間的文件")
                                        .allMatch(doc -> doc.getFileSize() >= 200000L && doc.getFileSize() <= 600000L);
                }
        }

        @Nested
        @DisplayName("業務關聯查詢測試")
        class BusinessQueryTests {

                @Test
                @DisplayName("依業務類型查詢 - EMPLOYEE")
                void queryByBusinessType_Employee() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("businessType", "EMPLOYEE")
                                        .build();

                        // When
                        Page<Document> result = executeQuery(query);

                        // Then
                        assertThat(result.getContent())
                                        .as("應返回所有員工相關的文件")
                                        .hasSize(9)
                                        .allMatch(doc -> "EMPLOYEE".equals(doc.getBusinessType()));
                }

                @Test
                @DisplayName("依擁有者查詢")
                void queryByOwnerId() {
                        // Given - E001 擁有 3 筆文件
                        QueryGroup query = QueryBuilder.where()
                                        .eq("ownerId", "E001")
                                        .build();

                        // When
                        Page<Document> result = executeQuery(query);

                        // Then
                        assertThat(result.getContent())
                                        .as("應返回 E001 擁有的文件")
                                        .hasSize(3)
                                        .allMatch(doc -> "E001".equals(doc.getOwnerId()));
                }

                @Test
                @DisplayName("依資料夾查詢")
                void queryByFolderId() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("folderId", "F-CONTRACT")
                                        .build();

                        // When
                        Page<Document> result = executeQuery(query);

                        // Then
                        assertThat(result.getContent())
                                        .as("應返回 F-CONTRACT 資料夾的文件")
                                        .hasSize(4)
                                        .allMatch(doc -> "F-CONTRACT".equals(doc.getFolderId()));
                }
        }

        @Nested
        @DisplayName("複合條件測試")
        class CompoundConditionTests {

                @Test
                @DisplayName("文件類型 + 未刪除複合查詢")
                void queryByTypeAndNotDeleted() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("documentType", "CONTRACT")
                                        .eq("isDeleted", false)
                                        .build();

                        // When
                        Page<Document> result = executeQuery(query);

                        // Then
                        assertThat(result.getContent())
                                        .as("應返回未刪除的合約文件")
                                        .hasSize(4);
                }

                @Test
                @DisplayName("可見度 + 分類複合查詢")
                void queryByVisibilityAndClassification() {
                        // Given - PRIVATE + CONFIDENTIAL
                        QueryGroup query = QueryBuilder.where()
                                        .eq("visibility", "PRIVATE")
                                        .eq("classification", "CONFIDENTIAL")
                                        .build();

                        // When
                        Page<Document> result = executeQuery(query);

                        // Then
                        assertThat(result.getContent())
                                        .as("應返回私人且機密的文件")
                                        .hasSize(3);
                }

                @Test
                @DisplayName("三重條件複合查詢")
                void queryByThreeConditions() {
                        // Given - EMPLOYEE + PRIVATE + 未刪除
                        QueryGroup query = QueryBuilder.where()
                                        .eq("businessType", "EMPLOYEE")
                                        .eq("visibility", "PRIVATE")
                                        .eq("isDeleted", false)
                                        .build();

                        // When
                        Page<Document> result = executeQuery(query);

                        // Then
                        assertThat(result.getContent())
                                        .as("應返回員工的私人未刪除文件")
                                        .isNotEmpty()
                                        .allMatch(doc -> "EMPLOYEE".equals(String.valueOf(doc.getBusinessType())) &&
                                                        "PRIVATE".equals(String.valueOf(doc.getVisibility())) &&
                                                        !doc.isDeleted());
                }

                @Test
                @DisplayName("加密 + 擁有者複合查詢")
                void queryByEncryptedAndOwner() {
                        // Given - E001 的加密文件
                        QueryGroup query = QueryBuilder.where()
                                        .eq("ownerId", "E001")
                                        .eq("isEncrypted", true)
                                        .build();

                        // When
                        Page<Document> result = executeQuery(query);

                        // Then
                        assertThat(result.getContent())
                                        .as("應返回 E001 的加密文件")
                                        .hasSize(2)
                                        .allMatch(doc -> doc.isEncrypted() && "E001".equals(doc.getOwnerId()));
                }
        }

        @Nested
        @DisplayName("分頁測試")
        class PaginationTests {

                @Test
                @DisplayName("分頁查詢 - 第一頁")
                void pagination_FirstPage() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("isDeleted", false)
                                        .build();

                        // When
                        Page<Document> result = documentRepository.findDocuments(query, PageRequest.of(0, 5));

                        // Then
                        assertThat(result.getNumber()).isEqualTo(0);
                        assertThat(result.getSize()).isEqualTo(5);
                        assertThat(result.getContent()).hasSize(5);
                        assertThat(result.getTotalElements()).isEqualTo(15);
                }

                @Test
                @DisplayName("分頁查詢 - 最後一頁")
                void pagination_LastPage() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("isDeleted", false)
                                        .build();

                        // When
                        Page<Document> result = documentRepository.findDocuments(query, PageRequest.of(2, 5));

                        // Then
                        assertThat(result.getNumber()).isEqualTo(2);
                        assertThat(result.getContent()).hasSize(5); // 15 筆，第 3 頁還有 5 筆
                }

                @Test
                @DisplayName("帶條件的分頁查詢")
                void pagination_WithCondition() {
                        // Given - 合約類型文件
                        QueryGroup query = QueryBuilder.where()
                                        .eq("documentType", "CONTRACT")
                                        .eq("isDeleted", false)
                                        .build();

                        // When
                        Page<Document> result = documentRepository.findDocuments(query, PageRequest.of(0, 2));

                        // Then
                        assertThat(result.getTotalElements()).isEqualTo(4);
                        assertThat(result.getContent()).hasSize(2);
                        assertThat(result.getTotalPages()).isEqualTo(2);
                }
        }

        @Nested
        @DisplayName("日期範圍測試")
        class DateRangeTests {

                @Test
                @DisplayName("GTE - 依上傳時間查詢")
                void gte_UploadedAt() {
                        // Given - 2025-01-10 之後上傳的文件
                        QueryGroup query = QueryBuilder.where()
                                        .gte("uploadedAt", "2025-01-10")
                                        .build();

                        // When
                        Page<Document> result = executeQuery(query);

                        // Then
                        assertThat(result.getContent())
                                        .as("應找到 2025-01-10 之後上傳的文件")
                                        .hasSize(6);
                }

                @Test
                @DisplayName("LTE - 依上傳時間查詢")
                void lte_UploadedAt() {
                        // Given - 2025-01-05 之前上傳的文件
                        QueryGroup query = QueryBuilder.where()
                                        .lte("uploadedAt", "2025-01-05")
                                        .build();

                        // When
                        Page<Document> result = executeQuery(query);

                        // Then
                        assertThat(result.getContent())
                                        .as("應找到 2025-01-05 之前上傳的文件")
                                        .hasSize(5);
                }
        }
}
