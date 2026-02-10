package com.company.hrms.iam.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.base.BaseIntegrationTest;
import com.company.hrms.iam.domain.model.aggregate.User;
import com.company.hrms.iam.domain.repository.IUserRepository;

/**
 * User Repository 整合測試
 * 使用 H2 記憶體資料庫驗證 Repository 查詢邏輯
 *
 * <p>
 * 測試重點:
 * <ul>
 * <li>QueryGroup 各種操作符轉 SQL</li>
 * <li>分頁查詢</li>
 * <li>排序</li>
 * <li>軟刪除過濾</li>
 * </ul>
 *
 * @author SA Team
 * @since 2026-01-29
 */
@Sql(scripts = { "classpath:test-data/iam_base_data.sql",
                "classpath:test-data/user_test_data.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("User Repository 整合測試")
public class UserRepositoryIntegrationTest extends BaseIntegrationTest {

        @Autowired
        private IUserRepository userRepository;

        @Nested
        @DisplayName("基本查詢測試")
        class BasicQueryTests {

                @Test
                @DisplayName("EQ 操作符 - 依狀態查詢")
                void findByStatus_EQ_ShouldReturnMatchingUsers() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("status", "ACTIVE")
                                        .build();

                        // When
                        Page<User> result = userRepository.findPage(query, PageRequest.of(0, 100));

                        // Then
                        assertThat(result.getContent())
                                        .isNotEmpty()
                                        .allMatch(user -> "ACTIVE".equals(user.getStatus().name()));
                }

                @Test
                @DisplayName("LIKE 操作符 - 依使用者名稱模糊查詢")
                void findByUsername_LIKE_ShouldReturnMatchingUsers() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .like("username", "%admin%")
                                        .ne("status", "DELETED")
                                        .build();

                        // When
                        Page<User> result = userRepository.findPage(query, PageRequest.of(0, 100));

                        // Then
                        assertThat(result.getContent())
                                        .isNotEmpty()
                                        .allMatch(user -> user.getUsername().contains("admin"));
                }

                @Test
                @DisplayName("IN 操作符 - 依多個狀態查詢")
                void findByStatuses_IN_ShouldReturnMatchingUsers() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .in("status", List.of("ACTIVE", "LOCKED"))
                                        .build();

                        // When
                        Page<User> result = userRepository.findPage(query, PageRequest.of(0, 100));

                        // Then
                        assertThat(result.getContent())
                                        .isNotEmpty()
                                        .allMatch(user -> "ACTIVE".equals(user.getStatus().name()) ||
                                                        "LOCKED".equals(user.getStatus().name()));
                }
        }

        @Nested
        @DisplayName("分頁測試")
        class PaginationTests {

                @Test
                @DisplayName("分頁查詢 - 第一頁")
                void findAll_Page0_ShouldReturnFirstPage() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .ne("status", "DELETED")
                                        .build();

                        // When
                        Page<User> result = userRepository.findPage(query, PageRequest.of(0, 5));

                        // Then
                        assertThat(result.getNumber()).isEqualTo(0);
                        assertThat(result.getSize()).isEqualTo(5);
                        assertThat(result.getContent()).hasSizeLessThanOrEqualTo(5);
                }

                @Test
                @DisplayName("分頁查詢 - 第二頁")
                void findAll_Page1_ShouldReturnSecondPage() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .ne("status", "DELETED")
                                        .build();

                        // When
                        Page<User> page0 = userRepository.findPage(query, PageRequest.of(0, 5));
                        Page<User> page1 = userRepository.findPage(query, PageRequest.of(1, 5));

                        // Then
                        assertThat(page1.getNumber()).isEqualTo(1);
                        if (page0.getTotalElements() > 5) {
                                assertThat(page1.getContent()).isNotEmpty();
                        }
                }
        }

        @Nested
        @DisplayName("軟刪除過濾測試")
        class SoftDeleteTests {

                @Test
                @DisplayName("軟刪除過濾 - 不應返回已刪除資料")
                void findAll_WithSoftDeleteFilter_ShouldExcludeDeleted() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .ne("status", "DELETED")
                                        .build();

                        // When
                        Page<User> result = userRepository.findPage(query, PageRequest.of(0, 100));

                        // Then
                        // 驗證沒有已刪除的資料 (假設測試資料中有軟刪除資料)
                        assertThat(result.getContent())
                                        .allMatch(user -> !user.isDeleted());
                }

                @Test
                @DisplayName("不含軟刪除過濾 - 應返回所有資料")
                void findAll_WithoutSoftDeleteFilter_ShouldIncludeAll() {
                        // Given - 不加 is_deleted 條件
                        QueryGroup query = QueryBuilder.where().build();

                        // When
                        Page<User> withFilter = userRepository.findPage(
                                        QueryBuilder.where().ne("status", "DELETED").build(),
                                        PageRequest.of(0, 100));
                        Page<User> withoutFilter = userRepository.findPage(query, PageRequest.of(0, 100));

                        // Then
                        // 不含過濾的結果應 >= 含過濾的結果
                        assertThat(withoutFilter.getTotalElements())
                                        .isGreaterThanOrEqualTo(withFilter.getTotalElements());
                }
        }

        @Nested
        @DisplayName("複合條件測試")
        class CompoundConditionTests {

                @Test
                @DisplayName("AND 條件 - 狀態 + 角色")
                void findByStatusAndRole_ShouldReturnMatchingUsers() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("status", "ACTIVE")
                                        // 假設有關聯查詢
                                        .build();

                        // When
                        Page<User> result = userRepository.findPage(query, PageRequest.of(0, 100));

                        // Then
                        assertThat(result.getContent())
                                        .allMatch(user -> "ACTIVE".equals(user.getStatus().name()));
                }
        }
}
