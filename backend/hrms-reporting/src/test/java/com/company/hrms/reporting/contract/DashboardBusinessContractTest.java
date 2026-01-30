package com.company.hrms.reporting.contract;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.contract.BaseContractTest;
import com.company.hrms.reporting.domain.model.dashboard.Dashboard;
import com.company.hrms.reporting.domain.repository.IDashboardRepository;

/**
 * Dashboard 業務合約測試
 *
 * <p>
 * 驗證 Dashboard 查詢是否符合 contracts/reporting_contracts.md 定義的合約規格
 *
 * <p>
 * 測試場景對照:
 * <ul>
 * <li>RPT-DASH-001: 查詢使用者儀表板列表 - 應包含 tenantId 和 ownerId 篩選</li>
 * <li>RPT-DASH-002: 查詢公開儀表板 - 應包含 isPublic 和 tenantId 篩選</li>
 * <li>RPT-DASH-003: 查詢預設儀表板 - 應包含 isDefault 和 tenantId 篩選</li>
 * </ul>
 *
 * @author SA Team
 * @since 2026-01-29
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:test-data/dashboard_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("Dashboard 業務合約測試")
class DashboardBusinessContractTest extends BaseContractTest {

    @Autowired
    private IDashboardRepository dashboardRepository;

    // ========================================================================
    // 1. 使用者儀表板查詢合約
    // ========================================================================
    @Nested
    @DisplayName("1. 使用者儀表板查詢合約")
    class UserDashboardQueryContractTests {

        @Test
        @DisplayName("RPT-DASH-001: 查詢使用者儀表板列表")
        void RPT_DASH_001_QueryUserDashboards() throws IOException {
            // Given - 合約規格：
            // 輸入: { "ownerId": "11111111-1111-1111-1111-111111111111", "tenantId": "T001" }
            // 必須包含的過濾條件: ownerId, tenantId

            QueryGroup query = QueryBuilder.where()
                    .eq("tenantId", "T001")
                    .eq("ownerId", "11111111-1111-1111-1111-111111111111")
                    .build();

            // When
            Page<Dashboard> result = dashboardRepository.findPage(query, PageRequest.of(0, 100));

            // Then - 驗證合約
            assertHasFilterForField(query, "tenantId");
            assertHasFilterForField(query, "ownerId");

            // 驗證結果符合預期
            assertThat(result.getContent())
                    .as("RPT-DASH-001: 應返回指定使用者在指定租戶的儀表板")
                    .hasSize(2)
                    .allMatch(d -> "T001".equals(d.getTenantId()) &&
                            d.getOwnerId().toString().equals("11111111-1111-1111-1111-111111111111"));
        }

        @Test
        @DisplayName("RPT-DASH-001a: 使用者查詢應包含租戶隔離")
        void RPT_DASH_001a_UserQueryShouldIncludeTenantIsolation() {
            // Given - 使用者查詢必須同時包含租戶條件
            QueryGroup queryWithTenant = QueryBuilder.where()
                    .eq("tenantId", "T001")
                    .eq("ownerId", "22222222-2222-2222-2222-222222222222")
                    .build();

            QueryGroup queryWithoutTenant = QueryBuilder.where()
                    .eq("ownerId", "22222222-2222-2222-2222-222222222222")
                    .build();

            // When
            Page<Dashboard> resultWithTenant = dashboardRepository.findPage(queryWithTenant, PageRequest.of(0, 100));
            Page<Dashboard> resultWithoutTenant = dashboardRepository.findPage(queryWithoutTenant, PageRequest.of(0, 100));

            // Then
            assertThat(resultWithTenant.getContent())
                    .as("帶租戶條件的查詢應返回正確結果")
                    .hasSize(2);

            assertThat(resultWithoutTenant.getContent())
                    .as("不帶租戶條件可能返回跨租戶資料 (需注意)")
                    .hasSizeGreaterThanOrEqualTo(2);

            // 合約驗證
            assertHasFilterForField(queryWithTenant, "tenantId");
        }
    }

    // ========================================================================
    // 2. 公開儀表板查詢合約
    // ========================================================================
    @Nested
    @DisplayName("2. 公開儀表板查詢合約")
    class PublicDashboardQueryContractTests {

        @Test
        @DisplayName("RPT-DASH-002: 查詢公開儀表板列表")
        void RPT_DASH_002_QueryPublicDashboards() {
            // Given - 合約規格：
            // 輸入: { "tenantId": "T001", "isPublic": true }
            // 必須包含的過濾條件: tenantId, isPublic

            QueryGroup query = QueryBuilder.where()
                    .eq("tenantId", "T001")
                    .eq("isPublic", true)
                    .build();

            // When
            Page<Dashboard> result = dashboardRepository.findPage(query, PageRequest.of(0, 100));

            // Then - 驗證合約
            assertHasFilterForField(query, "tenantId");
            assertHasFilterForField(query, "isPublic");

            // 驗證結果
            assertThat(result.getContent())
                    .as("RPT-DASH-002: 應返回 T001 租戶的公開儀表板")
                    .hasSize(3)
                    .allMatch(d -> "T001".equals(d.getTenantId()) && d.isPublic());
        }

        @Test
        @DisplayName("RPT-DASH-002a: 公開儀表板應可被同租戶使用者查看")
        void RPT_DASH_002a_PublicDashboardVisibleToTenantUsers() {
            // Given - T002 租戶的公開儀表板
            QueryGroup query = QueryBuilder.where()
                    .eq("tenantId", "T002")
                    .eq("isPublic", true)
                    .build();

            // When
            Page<Dashboard> result = dashboardRepository.findPage(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應返回 T002 租戶的公開儀表板")
                    .hasSize(1)
                    .allMatch(d -> d.isPublic());
        }
    }

    // ========================================================================
    // 3. 預設儀表板查詢合約
    // ========================================================================
    @Nested
    @DisplayName("3. 預設儀表板查詢合約")
    class DefaultDashboardQueryContractTests {

        @Test
        @DisplayName("RPT-DASH-003: 查詢預設儀表板")
        void RPT_DASH_003_QueryDefaultDashboard() {
            // Given - 合約規格：
            // 輸入: { "tenantId": "T001", "isDefault": true }
            // 必須包含的過濾條件: tenantId, isDefault

            QueryGroup query = QueryBuilder.where()
                    .eq("tenantId", "T001")
                    .eq("isDefault", true)
                    .build();

            // When
            Page<Dashboard> result = dashboardRepository.findPage(query, PageRequest.of(0, 100));

            // Then - 驗證合約
            assertHasFilterForField(query, "tenantId");
            assertHasFilterForField(query, "isDefault");

            // 驗證結果 - 每個租戶只應有一個預設儀表板
            assertThat(result.getContent())
                    .as("RPT-DASH-003: 每個租戶應只有一個預設儀表板")
                    .hasSize(1)
                    .allMatch(d -> d.isDefault());
        }

        @Test
        @DisplayName("RPT-DASH-003a: 各租戶預設儀表板獨立")
        void RPT_DASH_003a_EachTenantHasOwnDefault() {
            // Given - 查詢 T001 和 T002 的預設儀表板
            QueryGroup queryT001 = QueryBuilder.where()
                    .eq("tenantId", "T001")
                    .eq("isDefault", true)
                    .build();

            QueryGroup queryT002 = QueryBuilder.where()
                    .eq("tenantId", "T002")
                    .eq("isDefault", true)
                    .build();

            // When
            Page<Dashboard> resultT001 = dashboardRepository.findPage(queryT001, PageRequest.of(0, 100));
            Page<Dashboard> resultT002 = dashboardRepository.findPage(queryT002, PageRequest.of(0, 100));

            // Then
            assertThat(resultT001.getContent())
                    .as("T001 應有自己的預設儀表板")
                    .hasSize(1);
            assertThat(resultT002.getContent())
                    .as("T002 應有自己的預設儀表板")
                    .hasSize(1);

            // 驗證不是同一個
            assertThat(resultT001.getContent().get(0).getId())
                    .as("T001 和 T002 的預設儀表板應不同")
                    .isNotEqualTo(resultT002.getContent().get(0).getId());
        }
    }

    // ========================================================================
    // 4. 多租戶隔離合約
    // ========================================================================
    @Nested
    @DisplayName("4. 多租戶隔離合約")
    class TenantIsolationContractTests {

        @Test
        @DisplayName("RPT-ISO-001: 所有查詢必須包含 tenantId")
        void RPT_ISO_001_AllQueriesMustIncludeTenantId() {
            // Given - 不同類型的查詢都應包含 tenantId

            // 查詢 1: 使用者儀表板
            QueryGroup userQuery = QueryBuilder.where()
                    .eq("tenantId", "T001")
                    .eq("ownerId", "11111111-1111-1111-1111-111111111111")
                    .build();

            // 查詢 2: 公開儀表板
            QueryGroup publicQuery = QueryBuilder.where()
                    .eq("tenantId", "T001")
                    .eq("isPublic", true)
                    .build();

            // 查詢 3: 預設儀表板
            QueryGroup defaultQuery = QueryBuilder.where()
                    .eq("tenantId", "T001")
                    .eq("isDefault", true)
                    .build();

            // Then - 所有查詢都應包含 tenantId
            assertHasFilterForField(userQuery, "tenantId");
            assertHasFilterForField(publicQuery, "tenantId");
            assertHasFilterForField(defaultQuery, "tenantId");
        }

        @Test
        @DisplayName("RPT-ISO-002: 租戶間資料完全隔離")
        void RPT_ISO_002_DataIsolationBetweenTenants() {
            // Given
            QueryGroup queryT001 = QueryBuilder.where().eq("tenantId", "T001").build();
            QueryGroup queryT002 = QueryBuilder.where().eq("tenantId", "T002").build();
            QueryGroup queryT003 = QueryBuilder.where().eq("tenantId", "T003").build();

            // When
            Page<Dashboard> t001Dashboards = dashboardRepository.findPage(queryT001, PageRequest.of(0, 100));
            Page<Dashboard> t002Dashboards = dashboardRepository.findPage(queryT002, PageRequest.of(0, 100));
            Page<Dashboard> t003Dashboards = dashboardRepository.findPage(queryT003, PageRequest.of(0, 100));

            // Then
            // 驗證每個租戶只能看到自己的資料
            assertThat(t001Dashboards.getContent())
                    .as("T001 只能看到自己的儀表板")
                    .allMatch(d -> "T001".equals(d.getTenantId()));

            assertThat(t002Dashboards.getContent())
                    .as("T002 只能看到自己的儀表板")
                    .allMatch(d -> "T002".equals(d.getTenantId()));

            assertThat(t003Dashboards.getContent())
                    .as("T003 只能看到自己的儀表板")
                    .allMatch(d -> "T003".equals(d.getTenantId()));

            // 驗證資料不重疊
            long total = t001Dashboards.getTotalElements() +
                    t002Dashboards.getTotalElements() +
                    t003Dashboards.getTotalElements();

            assertThat(total)
                    .as("所有租戶資料總和應等於全部資料")
                    .isEqualTo(10);
        }
    }

    // ========================================================================
    // 5. 權限過濾合約
    // ========================================================================
    @Nested
    @DisplayName("5. 權限過濾合約")
    class PermissionFilterContractTests {

        @Test
        @DisplayName("RPT-PERM-001: 非擁有者只能查看公開儀表板")
        void RPT_PERM_001_NonOwnerCanOnlySeePublicDashboards() {
            // Given - 使用者查詢非自己的儀表板時，應只能看到公開的
            String currentUserId = "11111111-1111-1111-1111-111111111111";

            // 查詢非自己的公開儀表板
            QueryGroup query = QueryBuilder.where()
                    .eq("tenantId", "T001")
                    .eq("isPublic", true)
                    .ne("ownerId", currentUserId)
                    .build();

            // When
            Page<Dashboard> result = dashboardRepository.findPage(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("非擁有者應只能看到公開儀表板")
                    .allMatch(Dashboard::isPublic)
                    .noneMatch(d -> currentUserId.equals(d.getOwnerId().toString()));
        }

        @Test
        @DisplayName("RPT-PERM-002: 擁有者可以查看自己所有儀表板")
        void RPT_PERM_002_OwnerCanSeeAllOwnDashboards() {
            // Given
            String currentUserId = "22222222-2222-2222-2222-222222222222";

            QueryGroup query = QueryBuilder.where()
                    .eq("tenantId", "T001")
                    .eq("ownerId", currentUserId)
                    .build();

            // When
            Page<Dashboard> result = dashboardRepository.findPage(query, PageRequest.of(0, 100));

            // Then - 擁有者可看到公開和私人
            assertThat(result.getContent())
                    .as("擁有者應可看到自己所有儀表板")
                    .hasSize(2)
                    .allMatch(d -> currentUserId.equals(d.getOwnerId().toString()));

            // 驗證包含私人儀表板
            boolean hasPrivate = result.getContent().stream().anyMatch(d -> !d.isPublic());
            assertThat(hasPrivate)
                    .as("結果應包含私人儀表板")
                    .isTrue();
        }
    }
}
