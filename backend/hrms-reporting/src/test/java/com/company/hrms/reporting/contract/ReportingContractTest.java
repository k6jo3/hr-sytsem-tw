package com.company.hrms.reporting.contract;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.company.hrms.common.query.FilterUnit;
import com.company.hrms.common.query.QueryGroup;

/**
 * 報表服務合約測試基類
 * 
 * <p>
 * 用途：驗證查詢條件是否符合 contracts/reporting_contracts.md 定義的合約規格
 * 
 * <p>
 * 使用範例：
 * 
 * <pre>
 * {@literal @}Test
 * void dashboardQuery_ShouldIncludeTenantFilter() {
 *     // Given
 *     GetDashboardListRequest req = new GetDashboardListRequest();
 *     req.setTenantId("tenant-001");
 *     
 *     // When
 *     QueryGroup query = buildQueryFromRequest(req);
 *     
 *     // Then
 *     verifyContract(query, "RPT-DASH-001");
 * }
 * </pre>
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class ReportingContractTest {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private static final String CONTRACT_FILE = "contracts/reporting_contracts.md";
    private static final Pattern SCENARIO_PATTERN = Pattern.compile(
            "\\|\\s*(RPT-[A-Z]+-\\d+)\\s*\\|\\s*([^|]+)\\|\\s*([^|]+)\\s*\\|");

    /**
     * 驗證查詢條件是否符合合約規格
     * 
     * @param actualQuery 實際產生的查詢條件
     * @param scenarioId  場景ID (例如: RPT-DASH-001)
     */
    protected void verifyContract(QueryGroup actualQuery, String scenarioId) {
        List<String> requiredFilters = loadRequiredFilters(scenarioId);

        assertThat(requiredFilters)
                .as("場景 [%s] 的合約規格應該存在", scenarioId)
                .isNotEmpty();

        for (String requiredFilter : requiredFilters) {
            assertFilterExists(actualQuery, requiredFilter, scenarioId);
        }

        log.info("✅ 場景 [{}] 合約驗證通過", scenarioId);
    }

    /**
     * 從合約文件載入必要篩選條件
     */
    private List<String> loadRequiredFilters(String scenarioId) {
        try {
            String contractPath = Paths.get(System.getProperty("user.dir"))
                    .getParent()
                    .getParent()
                    .resolve(CONTRACT_FILE)
                    .toString();

            String content = Files.readString(Paths.get(contractPath));

            return content.lines()
                    .filter(line -> line.contains(scenarioId))
                    .map(line -> {
                        Matcher matcher = SCENARIO_PATTERN.matcher(line);
                        if (matcher.find() && matcher.group(1).equals(scenarioId)) {
                            return matcher.group(3).trim();
                        }
                        return null;
                    })
                    .filter(filters -> filters != null && !filters.isEmpty())
                    .flatMap(filters -> parseFilters(filters).stream())
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException("無法載入合約文件: " + CONTRACT_FILE, e);
        }
    }

    /**
     * 解析篩選條件字串
     * 例如: "tenantId = :tenantId, status = ACTIVE" → ["tenantId", "status"]
     */
    private List<String> parseFilters(String filtersText) {
        Pattern filterPattern = Pattern.compile("(\\w+)\\s*[=<>]");
        Matcher matcher = filterPattern.matcher(filtersText);

        return matcher.results()
                .map(m -> m.group(1))
                .collect(Collectors.toList());
    }

    /**
     * 斷言查詢條件中包含必要的篩選器
     */
    private void assertFilterExists(QueryGroup query, String requiredField, String scenarioId) {
        boolean exists = containsFilter(query, requiredField);

        assertThat(exists)
                .as("場景 [%s] 必須包含篩選條件: %s", scenarioId, requiredField)
                .isTrue();
    }

    /**
     * 遞迴檢查 QueryGroup 是否包含指定欄位的篩選條件
     */
    private boolean containsFilter(QueryGroup group, String field) {
        // 檢查當前層級的條件
        boolean foundInConditions = group.getConditions().stream()
                .anyMatch(filter -> matchesField(filter, field));

        if (foundInConditions) {
            return true;
        }

        // 遞迴檢查子群組
        return group.getSubGroups().stream()
                .anyMatch(subGroup -> containsFilter(subGroup, field));
    }

    /**
     * 判斷 FilterUnit 是否匹配指定欄位
     */
    private boolean matchesField(FilterUnit filter, String field) {
        String filterField = filter.getField();

        // 支援巢狀欄位 (例如: department.id 匹配 departmentId)
        return filterField.equals(field) ||
                filterField.replace(".", "").equalsIgnoreCase(field) ||
                field.replace(".", "").equalsIgnoreCase(filterField);
    }

    /**
     * 驗證多租戶隔離
     * 所有查詢都應該包含 tenantId 篩選條件
     */
    @DisplayName("多租戶隔離驗證")
    protected void verifyTenantIsolation(QueryGroup query) {
        assertThat(containsFilter(query, "tenantId"))
                .as("查詢必須包含 tenantId 篩選條件以確保多租戶隔離")
                .isTrue();
    }

    /**
     * 驗證軟刪除過濾
     * 查詢應該排除已刪除的記錄 (若實體支援軟刪除)
     */
    @DisplayName("軟刪除過濾驗證")
    protected void verifySoftDeleteFilter(QueryGroup query) {
        boolean hasDeletedFilter = query.getConditions().stream()
                .anyMatch(filter -> filter.getField().equalsIgnoreCase("isDeleted") ||
                        filter.getField().equalsIgnoreCase("deleted"));

        if (hasDeletedFilter) {
            log.info("✅ 查詢包含軟刪除過濾條件");
        }
    }
}
