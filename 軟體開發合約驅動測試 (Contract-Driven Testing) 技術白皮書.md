# 軟體開發合約驅動測試 (Contract-Driven Testing) 技術白皮書

## 一、 系統運作流程圖 (Process Flow)

本架構的核心在於將「需求」直接轉化為「測試斷言」，減少人為轉譯損失。

1. **規格定義 (Define):** SA 在 Markdown 定義業務場景與必須包含的過濾條件。
2. **實作 (Implement):** 工程師根據需求開發 Controller 與 Service。
3. **攔截 (Intercept):** 測試執行時，Mock 掉數據庫層，攔截 Service 產出的 `QueryGroup` 意圖物件。
4. **解析 (Parse):** `ContractEngine` 解析 Markdown 表格內容。
5. **斷言 (Assert):** 比對 `QueryGroup` 裡的過濾器是否完全滿足 Markdown 的合約要求。

---

## 二、 Markdown 規格書實例 (`specs/course_api.md`)

這是 SA 的工作台，用來鎖定業務邏輯與權限規則。

```markdown
### 課程查詢業務合約 (Course Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| SC_001 | 一般學生查詢 | STUDENT | `{"name":"Java"}` | `status = 'PUBLISHED'`, `is_deleted = 0`, `name LIKE 'Java'` |
| SC_002 | 管理員查看 | ADMIN | `{"dept":"IT"}` | `dept = 'IT'`, `is_deleted = 0` |
| SC_003 | 角色權限邊界 | GUEST | `{}` | `is_public = 1`, `price = 0` |

```

---

## 三、 Markdown 解析與斷言引擎實作

這是架構師提供的核心組件，用於自動化執行 SA 的判決。

```java
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

public class MarkdownContractEngine {

    // 支援語義解析：Key [運算子] Value (例如: status = 'ACTIVE')
    private static final Pattern CRITERIA_PATTERN = Pattern.compile("(\\w+)\\s*([=><!\\sLIKE]+)\\s*'?([^']+)'?");

    /**
     * 核心斷言：驗證 QueryGroup 是否符合 Markdown 定義
     * @param actualQuery 程式碼產出的查詢物件
     * @param markdownTable 來自 SA 的規格文本
     * @param scenarioId 指定驗證哪一個場景
     */
    public void assertContract(QueryGroup actualQuery, String markdownTable, String scenarioId) {
        List<String> requiredFilters = parseFiltersFromTable(markdownTable, scenarioId);
        
        for (String criteria : requiredFilters) {
            boolean isMatched = actualQuery.getFilters().stream()
                    .anyMatch(filter -> verifyFilterMatch(filter, criteria));
            
            if (!isMatched) {
                throw new AssertionError(String.format(
                    "❌ 合約違規 [%s]：預期應包含條件 [%s]，但實際產出的查詢條件中未找到。", 
                    scenarioId, criteria));
            }
        }
        System.out.println("✅ 場景 [" + scenarioId + "] 驗證通過，符合業務合約。");
    }

    private boolean verifyFilterMatch(Filter filter, String criteria) {
        Matcher m = CRITERIA_PATTERN.matcher(criteria);
        if (m.find()) {
            String key = m.group(1);
            String op = m.group(2).trim();
            String val = m.group(3);
            // 比對 Key 與 Value (Operator 可進一步映射 Enum)
            return filter.getKey().equalsIgnoreCase(key) && 
                   filter.getValue().toString().equalsIgnoreCase(val);
        }
        return false;
    }

    private List<String> parseFiltersFromTable(String table, String scenarioId) {
        return table.lines()
                .filter(line -> line.contains(scenarioId))
                .map(line -> line.split("\\|")[5].trim()) // 取第 5 欄：必須包含的條件
                .flatMap(s -> Arrays.stream(s.split(",")))
                .map(String::trim)
                .collect(Collectors.toList());
    }
}

```

---

## 四、 測試執行範例 (Flow Integration)

開發者只需調用基類方法，即可完成全鏈路驗證。

```java
@Test
void searchCourse_AsStudent_ShouldIncludeSecurityFilters() throws Exception {
    // 1. 取得 SA 定義的規格文本
    String spec = FileUtils.readString("specs/course_api.md");

    // 2. 模擬 API 呼叫流程 (底層會攔截 Repository.findPage 的參數)
    QueryGroup actualQuery = flowTester.execute(
        post("/api/courses/search")
        .content("{\"name\":\"Java\"}")
        .withRole("STUDENT")
    );

    // 3. 自動比對合約
    contractEngine.assertContract(actualQuery, spec, "SC_001");
}

```

---

## 五、 本架構之核心價值

1. **非對稱驗證 (Asymmetric Verification):** 工程師不再是自己測試自己，而是由 SA 定義的 Markdown 作為「外部法規」。
2. **業務可視化:** 所有的過濾邏輯（如刪除標記、發佈狀態、權限過濾）都顯性化地寫在 Markdown 中，而非隱藏在深層代碼。
3. **溝通革命:** PM/SA 看到測試失敗時，會知道是「合約未達成」，而不只是「工程師有 Bug」。
4. **零 Mock 負擔:** 透過 `QueryGroup` 攔截技術，測試無需啟動資料庫，執行速度極快。

---