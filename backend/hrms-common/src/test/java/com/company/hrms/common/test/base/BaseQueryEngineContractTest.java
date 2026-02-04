package com.company.hrms.common.test.base;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;

/**
 * QueryEngine 引擎契約測試基類
 *
 * <p>
 * 目的：一次性驗證 QueryEngine 的所有操作符（EQ, NE, LIKE, IN, GTE 等），
 * 確保 QueryGroup → SQL 翻譯機制正確運作。
 *
 * <p>
 * 測試通過後，後續業務測試不再需要跑 DB，只需驗證 QueryGroup 組裝邏輯。
 *
 * <p>
 * 使用方式：
 * 
 * <pre>
 * class EmployeeQueryEngineContractTest extends BaseQueryEngineContractTest&lt;Employee&gt; {
 *
 *     {@literal @}Autowired
 *     private IEmployeeRepository repository;
 *
 *     {@literal @}Override
 *     protected String getTestDataScript() {
 *         return "classpath:test-data/employee_engine_contract_data.sql";
 *     }
 *
 *     {@literal @}Override
 *     protected Page&lt;Employee&gt; executeQuery(QueryGroup query) {
 *         return repository.findAll(query, PageRequest.of(0, 100));
 *     }
 *
 *     static Stream&lt;Arguments&gt; operatorTestCases() {
 *         return Stream.of(
 *             Arguments.of("EQ", "status", "ACTIVE", 10),
 *             Arguments.of("NE", "status", "ACTIVE", 5),
 *             Arguments.of("LIKE", "name", "%王%", 3),
 *             Arguments.of("IN", "status", List.of("ACTIVE", "ON_LEAVE"), 12),
 *             Arguments.of("GTE", "salary", 50000, 8),
 *             Arguments.of("IS_NULL", "resignation_date", null, 15)
 *         );
 *     }
 * }
 * </pre>
 *
 * @param <T> 實體類型
 * @author SA Team
 * @since 2026-01-28
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public abstract class BaseQueryEngineContractTest<T> extends BaseTest {

    /**
     * 取得測試資料 SQL 腳本路徑
     * 
     * @return SQL 腳本路徑，例如 "classpath:test-data/employee_data.sql"
     */
    protected abstract String getTestDataScript();

    /**
     * 執行查詢
     * 
     * @param query QueryGroup 查詢條件
     * @return 查詢結果分頁
     */
    protected abstract Page<T> executeQuery(QueryGroup query);

    /**
     * 參數化測試：驗證各種操作符
     *
     * @param operatorName  操作符名稱 (EQ, NE, LIKE, IN, GTE, LTE, IS_NULL, IS_NOT_NULL)
     * @param field         欄位名稱
     * @param value         測試值
     * @param expectedCount 預期結果數量
     */
    @ParameterizedTest(name = "{0}: {1} = {2} → 預期 {3} 筆")
    @MethodSource("operatorTestCases")
    @DisplayName("QueryEngine 操作符契約測試")
    void operator_ShouldProduceCorrectResult(String operatorName, String field,
            Object value, int expectedCount) {
        // 1. Build QueryGroup
        QueryGroup query = buildQueryByOperator(operatorName, field, value);

        // 2. Execute
        Page<T> result = executeQuery(query);

        // 3. Assert
        assertThat(result.getContent())
                .as("操作符 [%s] 對欄位 [%s] 值 [%s] 應返回 %d 筆",
                        operatorName, field, value, expectedCount)
                .hasSize(expectedCount);
    }

    /**
     * 根據操作符類型建立 QueryGroup
     */
    protected QueryGroup buildQueryByOperator(String operatorName, String field, Object value) {
        QueryBuilder builder = QueryBuilder.where();

        switch (operatorName.toUpperCase()) {
            case "EQ":
                builder.eq(field, value);
                break;
            case "NE":
                builder.ne(field, value);
                break;
            case "LIKE":
                builder.like(field, (String) value);
                break;
            case "IN":
                @SuppressWarnings("unchecked")
                List<Object> inValues = (List<Object>) value;
                builder.in(field, inValues.toArray());
                break;
            case "NOT_IN":
                @SuppressWarnings("unchecked")
                List<Object> notInValues = (List<Object>) value;
                builder.notIn(field, notInValues.toArray());
                break;
            case "GT":
                builder.gt(field, (Comparable<?>) value);
                break;
            case "GTE":
                builder.gte(field, (Comparable<?>) value);
                break;
            case "LT":
                builder.lt(field, (Comparable<?>) value);
                break;
            case "LTE":
                builder.lte(field, (Comparable<?>) value);
                break;
            case "IS_NULL":
                builder.isNull(field);
                break;
            case "IS_NOT_NULL":
                builder.isNotNull(field);
                break;
            case "BETWEEN":
                @SuppressWarnings("unchecked")
                List<Comparable<?>> range = (List<Comparable<?>>) value;
                builder.between(field, range.get(0), range.get(1));
                break;
            default:
                throw new IllegalArgumentException("未知操作符: " + operatorName);
        }

        return builder.build();
    }

    /**
     * 測試複合條件：AND 組合
     */
    protected void assertAndConditions(QueryGroup query, int expectedCount) {
        Page<T> result = executeQuery(query);
        assertThat(result.getContent())
                .as("AND 組合條件應返回 %d 筆", expectedCount)
                .hasSize(expectedCount);
    }

    /**
     * 測試複合條件：OR 組合
     */
    protected void assertOrConditions(QueryGroup query, int expectedCount) {
        Page<T> result = executeQuery(query);
        assertThat(result.getContent())
                .as("OR 組合條件應返回 %d 筆", expectedCount)
                .hasSize(expectedCount);
    }

    /**
     * 測試巢狀條件
     */
    protected void assertNestedConditions(QueryGroup query, int expectedCount) {
        Page<T> result = executeQuery(query);
        assertThat(result.getContent())
                .as("巢狀條件應返回 %d 筆", expectedCount)
                .hasSize(expectedCount);
    }
}
