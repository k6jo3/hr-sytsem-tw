package com.company.hrms.common.test.base;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.assertion.QueryGroupAssert;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Repository 測試基類
 * 使用 H2 記憶體資料庫進行 Repository 整合測試
 *
 * <p>測試重點:
 * <ul>
 *   <li>QueryGroup → SQL 轉換</li>
 *   <li>分頁與排序</li>
 *   <li>Auto-Join 機制</li>
 * </ul>
 *
 * <p>使用範例:
 * <pre>
 * class EmployeeRepositoryTest extends BaseRepositoryTest {
 *
 *     {@literal @}Autowired
 *     private EmployeeRepository repository;
 *
 *     {@literal @}BeforeEach
 *     void setupTestData() {
 *         // 載入測試資料
 *         loadTestData("employee_test_data.sql");
 *     }
 *
 *     {@literal @}Test
 *     void findByDepartment_ShouldReturnCorrectResults() {
 *         QueryGroup query = QueryGroup.and()
 *             .eq("department.name", "研發部")
 *             .eq("status", "ACTIVE");
 *
 *         Page&lt;Employee&gt; result = repository.findPage(query, PageRequest.of(0, 10));
 *
 *         assertThat(result.getContent()).hasSize(5);
 *     }
 * }
 * </pre>
 */
@DataJpaTest
@ActiveProfiles("test")
public abstract class BaseRepositoryTest extends BaseTest {

    /**
     * 載入測試資料
     */
    protected void loadTestData(String sqlFile) {
        // 實際專案中使用 @Sql 註解或 ResourceDatabasePopulator
        System.out.println("載入測試資料: " + sqlFile);
    }

    /**
     * 驗證 QueryGroup
     */
    protected QueryGroupAssert assertQuery(QueryGroup queryGroup) {
        return QueryGroupAssert.assertThat(queryGroup);
    }

    /**
     * 取得 Repository 測試快照目錄
     */
    protected String getSnapshotDirectory() {
        return "src/test/resources/snapshots/repository/" + getTestClassName();
    }
}
