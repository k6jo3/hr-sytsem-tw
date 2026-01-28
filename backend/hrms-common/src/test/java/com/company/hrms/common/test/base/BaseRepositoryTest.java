package com.company.hrms.common.test.base;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ActiveProfiles;

import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.assertion.QueryGroupAssert;

/**
 * Repository 整合測試基類
 * 使用 H2 記憶體資料庫進行 Repository 整合測試
 *
 * <p>測試重點:
 * <ul>
 *   <li>QueryGroup → SQL 轉換</li>
 *   <li>分頁與排序</li>
 *   <li>Auto-Join 機制</li>
 *   <li>軟刪除過濾</li>
 * </ul>
 *
 * <p>使用範例:
 * <pre>
 * {@literal @}Sql(scripts = "classpath:test-data/employee_test_data.sql")
 * class EmployeeRepositoryIntegrationTest extends BaseRepositoryTest {
 *
 *     {@literal @}Autowired
 *     private IEmployeeRepository repository;
 *
 *     {@literal @}Test
 *     {@literal @}DisplayName("依部門查詢員工 - 應返回正確結果")
 *     void findByDepartment_ShouldReturnCorrectResults() {
 *         // Given
 *         QueryGroup query = QueryBuilder.where()
 *             .eq("department.name", "研發部")
 *             .eq("status", "ACTIVE")
 *             .eq("is_deleted", 0)
 *             .build();
 *
 *         // When
 *         Page&lt;Employee&gt; result = repository.findAll(query, PageRequest.of(0, 10));
 *
 *         // Then
 *         assertThat(result.getContent()).hasSize(5);
 *         assertThat(result.getContent())
 *             .allMatch(e -&gt; e.getDepartment().getName().equals("研發部"));
 *     }
 *
 *     {@literal @}Test
 *     {@literal @}DisplayName("分頁查詢 - 應正確分頁")
 *     void findAll_WithPagination_ShouldReturnCorrectPage() {
 *         // Given
 *         QueryGroup query = QueryBuilder.where()
 *             .eq("is_deleted", 0)
 *             .build();
 *
 *         // When
 *         Page&lt;Employee&gt; page1 = repository.findAll(query, PageRequest.of(0, 5));
 *         Page&lt;Employee&gt; page2 = repository.findAll(query, PageRequest.of(1, 5));
 *
 *         // Then
 *         assertThat(page1.getNumber()).isEqualTo(0);
 *         assertThat(page1.getContent()).hasSize(5);
 *         assertThat(page2.getNumber()).isEqualTo(1);
 *     }
 *
 *     {@literal @}Test
 *     {@literal @}DisplayName("排序查詢 - 應按指定欄位排序")
 *     void findAll_WithSort_ShouldReturnSortedResults() {
 *         // Given
 *         QueryGroup query = QueryBuilder.where()
 *             .eq("is_deleted", 0)
 *             .build();
 *         Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
 *
 *         // When
 *         Page&lt;Employee&gt; result = repository.findAll(query, pageable);
 *
 *         // Then
 *         assertSortedAscending(result.getContent(), e -&gt; e.getName());
 *     }
 * }
 * </pre>
 *
 * @author SA Team
 * @since 2026-01-28
 */
@DataJpaTest
@ActiveProfiles("test")
public abstract class BaseRepositoryTest extends BaseTest {

    @Autowired
    protected TestEntityManager entityManager;

    @Autowired
    protected DataSource dataSource;

    /**
     * 載入測試資料 SQL 腳本
     *
     * @param sqlScriptPath 腳本路徑，例如 "test-data/employee_data.sql"
     */
    protected void loadTestData(String sqlScriptPath) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource(sqlScriptPath));
        populator.execute(dataSource);
    }

    /**
     * 刷新並清除 EntityManager 快取
     * 在驗證持久化資料前呼叫，確保從 DB 讀取最新資料
     */
    protected void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    /**
     * 持久化實體並刷新
     */
    protected <T> T persistAndFlush(T entity) {
        T persisted = entityManager.persist(entity);
        entityManager.flush();
        return persisted;
    }

    /**
     * 驗證 QueryGroup
     */
    protected QueryGroupAssert assertQuery(QueryGroup queryGroup) {
        return QueryGroupAssert.assertThat(queryGroup);
    }

    /**
     * 驗證分頁結果
     */
    protected <T> void assertPageResult(Page<T> page, int expectedPageNumber,
                                         int expectedPageSize, long expectedTotalElements) {
        assertThat(page.getNumber())
            .as("頁碼應為 %d", expectedPageNumber)
            .isEqualTo(expectedPageNumber);
        assertThat(page.getSize())
            .as("每頁筆數應為 %d", expectedPageSize)
            .isEqualTo(expectedPageSize);
        assertThat(page.getTotalElements())
            .as("總筆數應為 %d", expectedTotalElements)
            .isEqualTo(expectedTotalElements);
    }

    /**
     * 驗證升冪排序
     */
    protected <T, C extends Comparable<C>> void assertSortedAscending(
            List<T> list, java.util.function.Function<T, C> keyExtractor) {
        for (int i = 0; i < list.size() - 1; i++) {
            C current = keyExtractor.apply(list.get(i));
            C next = keyExtractor.apply(list.get(i + 1));
            assertThat(current.compareTo(next))
                .as("索引 %d 與 %d 的順序應為升冪", i, i + 1)
                .isLessThanOrEqualTo(0);
        }
    }

    /**
     * 驗證降冪排序
     */
    protected <T, C extends Comparable<C>> void assertSortedDescending(
            List<T> list, java.util.function.Function<T, C> keyExtractor) {
        for (int i = 0; i < list.size() - 1; i++) {
            C current = keyExtractor.apply(list.get(i));
            C next = keyExtractor.apply(list.get(i + 1));
            assertThat(current.compareTo(next))
                .as("索引 %d 與 %d 的順序應為降冪", i, i + 1)
                .isGreaterThanOrEqualTo(0);
        }
    }

    /**
     * 建立標準分頁請求
     */
    protected Pageable pageRequest(int page, int size) {
        return PageRequest.of(page, size);
    }

    /**
     * 建立帶排序的分頁請求
     */
    protected Pageable pageRequest(int page, int size, String sortField, boolean ascending) {
        Sort sort = ascending ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        return PageRequest.of(page, size, sort);
    }

    /**
     * 取得 Repository 測試快照目錄
     */
    protected String getSnapshotDirectory() {
        return "src/test/resources/snapshots/repository/" + getTestClassName();
    }
}
