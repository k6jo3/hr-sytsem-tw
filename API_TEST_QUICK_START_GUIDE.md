# 🚀 API 整合測試快速實施指南

**文檔版本:** 1.0
**最後更新:** 2026-02-03
**對象:** 後端開發團隊

---

## 📋 **快速概覽**

### 工作規模
- **總缺失測試:** 74 個
- **預估時間:** 6-8 週
- **預估代碼:** 25,000+ 行
- **人員配置:** 2-3 名開發者

### 優先順序
```
Week 2-3: P0 服務 (11 個) ← 現在開始
Week 3-5: P1 服務 (14 個)
Week 5-8: P2 服務 (49 個)
```

---

## 🎯 **第一步：P0 優先級服務** (Week 2-3)

### 優先順序
1. **Payroll (04)** - 2 個缺失 ⭐ 最急迫
2. **Organization (02)** - 4 個缺失
3. **Attendance (03)** - 4 個缺失

### 立即行動清單

```
Payroll:
  ✓ BankTransferApiIntegrationTest.java
  ✓ SalaryStructureApiIntegrationTest.java
  ✓ payroll_test_data.sql (補充)
  ✓ cleanup.sql (補充)

Organization:
  ✓ ContractApiIntegrationTest.java
  ✓ EssApiIntegrationTest.java
  ✓ OrganizationApiIntegrationTest.java
  ✓ organization_test_data.sql

Attendance:
  ✓ LeaveTypeApiIntegrationTest.java
  ✓ MonthCloseApiIntegrationTest.java
  ✓ ShiftApiIntegrationTest.java
  ✓ ReportApiIntegrationTest.java (Report Query)
  ✓ attendance_test_data.sql
```

---

## 📝 **標準 API 整合測試模板**

### 檔案位置
```
{service}/src/test/java/com/company/hrms/{service}/api/controller/
└── {Feature}ApiIntegrationTest.java
```

### 標準模板

```java
package com.company.hrms.{serviceName}.api.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * {功能名稱} API 整合測試
 * 驗證 {業務流程說明}
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:test-data/{service}_test_data.sql",
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql",
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("{中文功能名稱} API 整合測試")
class {ServiceFeature}ApiIntegrationTest extends BaseApiContractTest {

    @Autowired private I{Feature}Repository {feature}Repository;
    @Autowired private ObjectMapper objectMapper;

    @BeforeEach
    void setupSecurity() {
        JWTModel mockUser = new JWTModel();
        mockUser.setUserId("test-user-001");
        mockUser.setUsername("test-user");
        mockUser.setRoles(Collections.singletonList("HR"));

        List<SimpleGrantedAuthority> authorities = mockUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(mockUser, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Nested
    @DisplayName("建立/新增操作")
    class CreationTests {
        @Test
        @DisplayName("{功能代碼}: {功能描述}")
        void {testMethodName}() throws Exception {
            // Given: 準備測試資料

            // When: 執行 API 呼叫

            // Then: 驗證結果
        }
    }

    @Nested
    @DisplayName("查詢操作")
    class QueryTests {
        @Test
        @DisplayName("{功能代碼}: {功能描述}")
        void {testMethodName}() throws Exception {
            // Given

            // When

            // Then
        }
    }

    @Nested
    @DisplayName("修改操作")
    class UpdateTests {
        @Test
        @DisplayName("{功能代碼}: {功能描述}")
        void {testMethodName}() throws Exception {
            // Given

            // When

            // Then
        }
    }

    @Nested
    @DisplayName("異常情況")
    class ExceptionHandlingTests {
        @Test
        @DisplayName("應拒絕無效的輸入")
        void shouldRejectInvalidInput() throws Exception {
            // Given

            // When & Then
        }
    }
}
```

---

## 📊 **測試資料 SQL 模板**

### 檔案位置
```
{service}/src/test/resources/test-data/
├── {service}_test_data.sql
└── cleanup.sql
```

### SQL 模板

```sql
-- {service}_test_data.sql

-- 清除舊資料
TRUNCATE TABLE {table1};
TRUNCATE TABLE {table2};

-- 插入測試資料
INSERT INTO {table1} (id, name, status, created_at) VALUES
('ID-001', '測試項目 1', 'ACTIVE', '2026-01-01'),
('ID-002', '測試項目 2', 'INACTIVE', '2026-01-02');

INSERT INTO {table2} (id, parent_id, description, status) VALUES
('SUB-001', 'ID-001', '子項目 1', 'DRAFT'),
('SUB-002', 'ID-001', '子項目 2', 'COMPLETED');

-- 驗證資料插入
SELECT * FROM {table1};
```

```sql
-- cleanup.sql

-- 清除測試資料（按外鍵關係順序）
DELETE FROM {child_table};
DELETE FROM {parent_table};
DELETE FROM {other_table};

-- 驗證清除
SELECT COUNT(*) FROM {table1};
SELECT COUNT(*) FROM {table2};
```

---

## ✅ **測試用例編寫要點**

### 1. Given-When-Then 結構

```java
@Test
@DisplayName("PAY_001: 建立薪資批次")
void PAY_001_createPayroll_ShouldReturnDraftStatus() throws Exception {
    // Given: 準備測試資料
    CreatePayrollRequest request = new CreatePayrollRequest();
    request.setOrganizationId("ORG-001");
    request.setPayrollSystem("MONTHLY");

    // When: 執行 API 呼叫
    var response = performPost("/api/v1/payrolls", request)
            .andExpect(status().isOk())
            .andReturn();

    // Then: 驗證結果
    String responseBody = response.getResponse().getContentAsString();
    CreatePayrollResponse result = objectMapper.readValue(responseBody, CreatePayrollResponse.class);

    assertThat(result.getId())
            .as("PAY_001: 應返回薪資批次 ID")
            .isNotBlank();
    assertThat(result.getStatus())
            .as("應以 DRAFT 狀態建立")
            .isEqualTo("DRAFT");
}
```

### 2. AssertJ 流暢斷言

```java
// ❌ 避免
assertEquals("DRAFT", payroll.getStatus());
assertNotNull(payroll.getId());

// ✅ 正確
assertThat(payroll.getStatus())
        .as("狀態應為 DRAFT")
        .isEqualTo("DRAFT");
assertThat(payroll.getId())
        .as("ID 不應為空")
        .isNotBlank();
```

### 3. 測試用例命名

```
格式: {功能代碼}_{方法名}_{場景}_{預期結果}

例如:
PAY_001_createPayroll_WithValidData_ShouldReturnDraftStatus
PAY_002_queryPayroll_WithoutPermission_ShouldReturn403
PAY_003_updatePayroll_FromDraft_ShouldTransitionToSubmitted
```

---

## 🏗️ **文件結構創建**

### 快速設定目錄結構

```bash
# 1. 為每個服務建立 test-data 目錄
mkdir -p backend/hrms-{payroll,organization,attendance,insurance,project,timesheet,performance,recruitment,training,workflow,notification,document,reporting}/src/test/resources/test-data

# 2. 建立預設的 cleanup.sql（可共用）
cat > backend/hrms-common/src/test/resources/test-data/cleanup.sql << 'EOF'
-- 共用清理腳本
-- 各服務自己的 cleanup 邏輯寫在各自的 SQL 檔案中
EOF

# 3. 驗證目錄結構
find backend/*/src/test/resources/test-data -type d
```

---

## 🚀 **執行測試**

### 單個服務的測試

```bash
# 執行 Payroll 的所有整合測試
cd backend/hrms-payroll
mvn test -Dtest=*ApiIntegrationTest

# 執行特定測試類
mvn test -Dtest=PayrollRunApiIntegrationTest

# 執行特定測試方法
mvn test -Dtest=PayrollRunApiIntegrationTest#shouldCreatePayroll
```

### 所有服務的整合測試

```bash
# 執行所有 API 整合測試
mvn test -Dtest=*ApiIntegrationTest -pl hrms-{payroll,organization,attendance}

# 生成覆蓋率報告
mvn jacoco:report
# 查看報告: target/site/jacoco/index.html
```

---

## 📈 **進度追蹤方法**

### 1. 使用 CSV 追蹤進度
```bash
# 編輯 API_TEST_PROGRESS_TRACKING.csv
# 更新每個測試的狀態
# 計算完成度百分比
```

### 2. Git 提交訊息規範

```bash
# 建立新的 API 整合測試
git commit -m "feat: 實現 Insurance 服務的 EnrollmentApiIntegrationTest

- 建立 EnrollmentApiIntegrationTest.java (350 行)
- 建立 insurance_test_data.sql (50 筆測試資料)
- 涵蓋加保、脫保、查詢流程
- 所有測試通過 (10/10) ✅

參考: COMPREHENSIVE_API_TEST_IMPLEMENTATION_PLAN.md"
```

### 3. 定期報告

```
週報格式:

本週完成:
- ✅ Payroll BankTransferApiIntegrationTest
- ✅ Organization ContractApiIntegrationTest

進度: 2/74 (2.7%)

下週計畫:
- Payroll SalaryStructureApiIntegrationTest
- Organization EssApiIntegrationTest

阻礙事項:
- (如有)
```

---

## 🔗 **相關檔案位置**

| 文件名 | 位置 | 用途 |
|:---|:---|:---|
| **實施計畫** | `COMPREHENSIVE_API_TEST_IMPLEMENTATION_PLAN.md` | 總體計畫和時程 |
| **進度追蹤** | `API_TEST_PROGRESS_TRACKING.csv` | 進度監控 |
| **詳細清單** | `MISSING_API_TESTS_BY_SERVICE.md` | 各服務需求 |
| **快速指南** | `API_TEST_QUICK_START_GUIDE.md` (本檔案) | 開發者指南 |

---

## ⚡ **常見問題與解決方案**

### Q1: 編譯錯誤 - 找不到 Repository

```
Error: Cannot resolve symbol 'I{Feature}Repository'
```

**解決方案:**
```java
// 檢查 repository 是否存在於 domain 層
// 路徑應為: domain/repository/I{Feature}Repository.java

// 如果不存在，檢查接口名稱是否正確
// 確認已 import
import com.company.hrms.{service}.domain.repository.I{Feature}Repository;
```

### Q2: SQL 語法錯誤

```
Error: SQL syntax error in {service}_test_data.sql
```

**解決方案:**
```bash
# 直接在資料庫測試 SQL
mysql> source test-data/{service}_test_data.sql;

# 檢查表名和欄位名是否正確
mysql> SHOW TABLES;
mysql> DESCRIBE {table_name};
```

### Q3: 測試資料未清理

```
Error: Data residue detected after test execution
```

**解決方案:**
```java
// 確認 cleanup.sql 正確執行
// 檢查刪除順序 (外鍵約束)
// 確認沒有遺漏的表

// 增加驗證
@Test
void verifyCleanupAfterTest() {
    long count = {feature}Repository.count();
    assertThat(count).isZero();
}
```

---

## 📞 **支援與資源**

### 相關文檔
- 📄 Spring Boot 整合測試: https://spring.io/guides/gs/testing-web/
- 📄 MockMvc 使用: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/web/servlet/MockMvc.html
- 📄 AssertJ: https://assertj.github.io/assertj-core-features-highlight.html

### 團隊支援
- 📧 有技術問題？檢查本文件或參考現有的測試實現
- 📧 測試資料問題？參考 Payroll 服務的 SQL 範例
- 📧 其他問題？提交 GitHub Issue

---

## ✨ **成功標準**

每個 API 整合測試應滿足:

- [ ] ✅ 遵循命名規範
- [ ] ✅ 代碼格式化完整
- [ ] ✅ 中文註釋清晰
- [ ] ✅ 所有測試通過 (綠燈)
- [ ] ✅ 測試資料正確清理 (無殘留)
- [ ] ✅ 覆蓋率 > 70%
- [ ] ✅ 執行時間 < 30 秒

---

**祝您實施順利！** 🎉

---

**最後更新:** 2026-02-03
**下一個檢查點:** Week 2 Day 3 (Payroll 完成後)

