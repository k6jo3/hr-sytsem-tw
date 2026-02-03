# 📋 API 整合測試完整實現計畫

**文檔版本:** 1.0
**建立日期:** 2026-02-03
**目標:** 實現全 14 個微服務的 API 整合測試覆蓋
**預期工作量:** 4-6 週

---

## 📊 **執行概要**

### 現況統計
- **總 Controller 數:** 92 個
- **已有 API 測試:** 18 個 (19.6%)
- **缺失整合測試:** 74 個 (80.4%)
- **需建立文件:** 74 個

### 預期成果
✅ 所有 14 個微服務的 API 整合測試框架完成
✅ 所有 Controller 都有對應的測試覆蓋
✅ 完整的測試生命週期（建立、執行、報告）

---

## 🎯 **分階段實施計畫**

### **第 1 階段：建立測試框架與基礎設施** (第 1-2 週)

#### 1.1 建立測試文件框架 (所有 74 個文件)
- 為每個缺失的 API 建立標準框架文件
- 包含 Given-When-Then 結構
- 安全上下文設定
- 測試資料 @Sql 配置

#### 1.2 建立測試資料 SQL 檔案
- 每個服務的測試資料 SQL
- cleanup.sql 清理腳本
- 涵蓋所有狀態轉換和邊界條件

#### 1.3 文檔與工具
- API 整合測試實施指南
- 測試檔案命名規範
- Maven 執行配置

---

### **第 2 階段：P0 優先級服務** (第 2-3 週)

#### 優先順序
1. **Payroll (04)** - 薪資系統 (2 個缺失)
2. **Organization (02)** - 員工管理 (4 個缺失)
3. **Attendance (03)** - 考勤系統 (4 個缺失)

#### 交付物
- ✅ 3 個服務的完整 API 整合測試實現
- ✅ 對應的測試資料 SQL
- ✅ 執行報告 (測試全部通過)

---

### **第 3 階段：P1 次要服務** (第 3-5 週)

#### 優先順序
1. **Insurance (05)** - 保險系統 (6 個)
2. **Project (06)** - 專案管理 (3 個)
3. **Timesheet (07)** - 工時管理 (2 個)
4. **IAM (01)** - 身份管理 (3 個)

#### 交付物
- ✅ 4 個服務的完整 API 整合測試
- ✅ 各服務測試資料
- ✅ 執行報告

---

### **第 4 階段：P2 附加服務** (第 5-6 週)

#### 優先順序
1. **Performance (08)** - 績效管理 (6 個)
2. **Recruitment (09)** - 招募管理 (9 個)
3. **Training (10)** - 訓練系統 (9 個)
4. **Workflow (11)** - 工作流程 (2 個)
5. **Notification (12)** - 通知系統 (8 個)
6. **Document (13)** - 文件管理 (5 個)
7. **Reporting (14)** - 報表系統 (4 個)

#### 交付物
- ✅ 7 個服務的完整 API 整合測試
- ✅ 各服務測試資料
- ✅ 執行報告

---

## 📁 **文件結構清單**

### 每個缺失的 API 整合測試文件包含

```java
package com.company.hrms.{serviceName}.api.controller;

/**
 * {API 功能名稱} API 整合測試
 * 驗證 {業務流程描述}
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:test-data/{service}_test_data.sql",
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql",
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("{中文描述}")
class {ApiIntegrationTestName} extends BaseApiContractTest {

    // 核心成員變數
    @Autowired private IXxxRepository xxxRepository;
    @Autowired private ObjectMapper objectMapper;

    // 安全上下文設定
    @BeforeEach void setupSecurity() { }

    // 測試用例 (Given-When-Then 結構)
    @Nested @DisplayName("...") class TestGroup { }

    // 異常情況測試
    @Nested @DisplayName("異常情況處理") class ExceptionHandlingTests { }
}
```

---

## 📊 **完整的文件清單**

### **01 IAM 服務 (3 個缺失)**

| 序號 | 文件名稱 | 路徑 | 狀態 |
|:---:|:---|:---|:---:|
| 1 | PermissionApiIntegrationTest.java | `hrms-iam/src/test/java/.../api/controller/` | ⏳ |
| 2 | ProfileApiIntegrationTest.java | `hrms-iam/src/test/java/.../api/controller/` | ⏳ |
| 3 | iam_test_data.sql | `hrms-iam/src/test/resources/test-data/` | ⏳ |

### **02 Organization 服務 (4 個缺失)**

| 序號 | 文件名稱 | 路徑 | 狀態 |
|:---:|:---|:---|:---:|
| 4 | ContractApiIntegrationTest.java | `hrms-organization/src/test/java/.../api/controller/` | ⏳ |
| 5 | EssApiIntegrationTest.java | `hrms-organization/src/test/java/.../api/controller/` | ⏳ |
| 6 | OrganizationApiIntegrationTest.java | `hrms-organization/src/test/java/.../api/controller/` | ⏳ |
| 7 | organization_test_data.sql | `hrms-organization/src/test/resources/test-data/` | ⏳ |

### **03 Attendance 服務 (4 個缺失)**

| 序號 | 文件名稱 | 路徑 | 狀態 |
|:---:|:---|:---|:---:|
| 8 | LeaveTypeApiIntegrationTest.java | `hrms-attendance/src/test/java/.../api/controller/` | ⏳ |
| 9 | MonthCloseApiIntegrationTest.java | `hrms-attendance/src/test/java/.../api/controller/` | ⏳ |
| 10 | ShiftApiIntegrationTest.java | `hrms-attendance/src/test/java/.../api/controller/` | ⏳ |
| 11 | attendance_test_data.sql | `hrms-attendance/src/test/resources/test-data/` | ⏳ |

### **04 Payroll 服務 (2 個缺失)**

| 序號 | 文件名稱 | 路徑 | 狀態 |
|:---:|:---|:---|:---:|
| 12 | BankTransferApiIntegrationTest.java | `hrms-payroll/src/test/java/.../api/controller/` | ⏳ |
| 13 | SalaryStructureApiIntegrationTest.java | `hrms-payroll/src/test/java/.../api/controller/` | ⏳ |

### **05 Insurance 服務 (6 個缺失)** 🔴

| 序號 | 文件名稱 | 路徑 | 狀態 |
|:---:|:---|:---|:---:|
| 14 | EnrollmentApiIntegrationTest.java | `hrms-insurance/src/test/java/.../api/controller/` | ⏳ |
| 15 | ExportApiIntegrationTest.java | `hrms-insurance/src/test/java/.../api/controller/` | ⏳ |
| 16 | FeeApiIntegrationTest.java | `hrms-insurance/src/test/java/.../api/controller/` | ⏳ |
| 17 | LevelApiIntegrationTest.java | `hrms-insurance/src/test/java/.../api/controller/` | ⏳ |
| 18 | MyInsuranceApiIntegrationTest.java | `hrms-insurance/src/test/java/.../api/controller/` | ⏳ |
| 19 | insurance_test_data.sql | `hrms-insurance/src/test/resources/test-data/` | ⏳ |

### **06 Project 服務 (3 個缺失)**

| 序號 | 文件名稱 | 路徑 | 狀態 |
|:---:|:---|:---|:---:|
| 20 | CustomerApiIntegrationTest.java | `hrms-project/src/test/java/.../api/controller/` | ⏳ |
| 21 | MemberApiIntegrationTest.java | `hrms-project/src/test/java/.../api/controller/` | ⏳ |
| 22 | TaskApiIntegrationTest.java | `hrms-project/src/test/java/.../api/controller/` | ⏳ |

### **07 Timesheet 服務 (2 個缺失)** 🔴

| 序號 | 文件名稱 | 路徑 | 狀態 |
|:---:|:---|:---|:---:|
| 23 | TimesheetApiIntegrationTest.java | `hrms-timesheet/src/test/java/.../api/controller/` | ⏳ |
| 24 | timesheet_test_data.sql | `hrms-timesheet/src/test/resources/test-data/` | ⏳ |

### **08 Performance 服務 (6 個缺失)** 🔴

| 序號 | 文件名稱 | 路徑 | 狀態 |
|:---:|:---|:---|:---:|
| 25 | CycleApiIntegrationTest.java | `hrms-performance/src/test/java/.../api/controller/` | ⏳ |
| 26 | ReviewApiIntegrationTest.java | `hrms-performance/src/test/java/.../api/controller/` | ⏳ |
| 27 | TemplateApiIntegrationTest.java | `hrms-performance/src/test/java/.../api/controller/` | ⏳ |
| 28 | ReportApiIntegrationTest.java | `hrms-performance/src/test/java/.../api/controller/` | ⏳ |
| 29 | performance_test_data.sql | `hrms-performance/src/test/resources/test-data/` | ⏳ |

### **09 Recruitment 服務 (9 個缺失)** 🔴

| 序號 | 文件名稱 | 路徑 | 狀態 |
|:---:|:---|:---|:---:|
| 30 | JobApiIntegrationTest.java | `hrms-recruitment/src/test/java/.../api/controller/` | ⏳ |
| 31 | CandidateApiIntegrationTest.java | `hrms-recruitment/src/test/java/.../api/controller/` | ⏳ |
| 32 | InterviewApiIntegrationTest.java | `hrms-recruitment/src/test/java/.../api/controller/` | ⏳ |
| 33 | OfferApiIntegrationTest.java | `hrms-recruitment/src/test/java/.../api/controller/` | ⏳ |
| 34 | ReportApiIntegrationTest.java | `hrms-recruitment/src/test/java/.../api/controller/` | ⏳ |
| 35 | recruitment_test_data.sql | `hrms-recruitment/src/test/resources/test-data/` | ⏳ |

### **10 Training 服務 (9 個缺失)**

| 序號 | 文件名稱 | 路徑 | 狀態 |
|:---:|:---|:---|:---:|
| 36 | CourseApiIntegrationTest.java | `hrms-training/src/test/java/.../api/controller/` | ⏳ |
| 37 | EnrollmentApiIntegrationTest.java | `hrms-training/src/test/java/.../api/controller/` | ⏳ |
| 38 | CertificateApiIntegrationTest.java | `hrms-training/src/test/java/.../api/controller/` | ⏳ |
| 39 | MyTrainingApiIntegrationTest.java | `hrms-training/src/test/java/.../api/controller/` | ⏳ |
| 40 | ReportApiIntegrationTest.java | `hrms-training/src/test/java/.../api/controller/` | ⏳ |
| 41 | StatisticsApiIntegrationTest.java | `hrms-training/src/test/java/.../api/controller/` | ⏳ |
| 42 | training_test_data.sql | `hrms-training/src/test/resources/test-data/` | ⏳ |

### **11 Workflow 服務 (2 個缺失)** 🔴

| 序號 | 文件名稱 | 路徑 | 狀態 |
|:---:|:---|:---|:---:|
| 43 | WorkflowApiIntegrationTest.java | `hrms-workflow/src/test/java/.../api/controller/` | ⏳ |
| 44 | workflow_test_data.sql | `hrms-workflow/src/test/resources/test-data/` | ⏳ |

### **12 Notification 服務 (8 個缺失)** 🔴

| 序號 | 文件名稱 | 路徑 | 狀態 |
|:---:|:---|:---|:---:|
| 45 | NotificationApiIntegrationTest.java | `hrms-notification/src/test/java/.../api/controller/` | ⏳ |
| 46 | AnnouncementApiIntegrationTest.java | `hrms-notification/src/test/java/.../api/controller/` | ⏳ |
| 47 | TemplateApiIntegrationTest.java | `hrms-notification/src/test/java/.../api/controller/` | ⏳ |
| 48 | PreferenceApiIntegrationTest.java | `hrms-notification/src/test/java/.../api/controller/` | ⏳ |
| 49 | notification_test_data.sql | `hrms-notification/src/test/resources/test-data/` | ⏳ |

### **13 Document 服務 (5 個缺失)**

| 序號 | 文件名稱 | 路徑 | 狀態 |
|:---:|:---|:---|:---:|
| 50 | DocumentApiIntegrationTest.java | `hrms-document/src/test/java/.../api/controller/` | ⏳ |
| 51 | RequestApiIntegrationTest.java | `hrms-document/src/test/java/.../api/controller/` | ⏳ |
| 52 | TemplateApiIntegrationTest.java | `hrms-document/src/test/java/.../api/controller/` | ⏳ |
| 53 | document_test_data.sql | `hrms-document/src/test/resources/test-data/` | ⏳ |

### **14 Reporting 服務 (4 個缺失)** 🔴

| 序號 | 文件名稱 | 路徑 | 狀態 |
|:---:|:---|:---|:---:|
| 54 | DashboardApiIntegrationTest.java | `hrms-reporting/src/test/java/.../api/controller/` | ⏳ |
| 55 | ReportApiIntegrationTest.java | `hrms-reporting/src/test/java/.../api/controller/` | ⏳ |
| 56 | ExportApiIntegrationTest.java | `hrms-reporting/src/test/java/.../api/controller/` | ⏳ |
| 57 | reporting_test_data.sql | `hrms-reporting/src/test/resources/test-data/` | ⏳ |

---

## 📈 **進度追蹤指標**

### 按階段進度
```
第 1 階段 (框架建立)           ░░░░░░░░░░░░░░░░░░░░  0% (待開始)
第 2 階段 (P0 服務)            ░░░░░░░░░░░░░░░░░░░░  0% (待開始)
第 3 階段 (P1 服務)            ░░░░░░░░░░░░░░░░░░░░  0% (待開始)
第 4 階段 (P2 服務)            ░░░░░░░░░░░░░░░░░░░░  0% (待開始)
```

### 按服務進度
```
Payroll (04)      ████████████████████ 50% (2/4 完成，2/4 缺失)
Attendance (03)   ████████░░░░░░░░░░░░ 25% (3/7 完成，4/7 缺失)
Organization (02) ████░░░░░░░░░░░░░░░░ 20% (2/6 完成，4/6 缺失)
IAM (01)          ███░░░░░░░░░░░░░░░░░ 37.5% (3/6 完成，3/6 缺失)
Project (06)      ██████░░░░░░░░░░░░░░ 37.5% (3/6 完成，3/6 缺失)
Insurance (05)    ░░░░░░░░░░░░░░░░░░░░  0% (0/6 缺失)
Training (10)     ░░░░░░░░░░░░░░░░░░░░ 10% (1/10 完成，9/10 缺失)
Document (13)     ░░░░░░░░░░░░░░░░░░░░ 16.7% (1/6 完成，5/6 缺失)
其他服務          ░░░░░░░░░░░░░░░░░░░░  0%
```

---

## 🔧 **技術標準與規範**

### 測試文件編寫規範

#### 1. 檔案命名
```
{Service}{Feature}ApiIntegrationTest.java
例如：PayrollRunApiIntegrationTest.java、EnrollmentApiIntegrationTest.java
```

#### 2. 類別結構
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:test-data/{service}_test_data.sql",
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql",
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("中文描述")
class {ServiceFeatureApiIntegrationTest} extends BaseApiContractTest {
    // 實現
}
```

#### 3. 測試用例數量
- **每個 API Controller:** 最少 3-5 個測試用例
- **完整生命週期:** 至少 1 個完整流程測試
- **異常處理:** 至少 2-3 個異常情況測試

#### 4. 測試資料規範
- 每個服務一個 SQL 檔案 (如 `payroll_test_data.sql`)
- 覆蓋所有狀態轉換
- 統一的清理腳本 (`cleanup.sql`)

#### 5. 斷言規範
- 使用 AssertJ 流暢斷言
- 必須使用 `.as()` 添加描述
- 驗證狀態碼、回應內容、資料庫狀態

---

## 📅 **時程規劃表**

### 第 1 週 (框架與基礎設施)

| 日期 | 任務 | 交付物 | 狀態 |
|:---:|:---|:---|:---:|
| Day 1-2 | 建立文件框架模板 | 標準 Java 框架檔案 | ⏳ |
| Day 3-4 | 建立 SQL 測試資料模板 | 測試資料設計指南 | ⏳ |
| Day 5 | 建立 Maven 執行配置 | 測試執行腳本 | ⏳ |

### 第 2-3 週 (P0 服務)

| 服務 | 計畫開始 | 計畫完成 | 交付物 | 進度 |
|:---|:---:|:---:|:---|:---:|
| Payroll | Week 2 Day 1 | Week 2 Day 2 | 2 個測試類 + SQL | ⏳ |
| Organization | Week 2 Day 3 | Week 2 Day 5 | 3 個測試類 + SQL | ⏳ |
| Attendance | Week 3 Day 1 | Week 3 Day 3 | 3 個測試類 + SQL | ⏳ |

### 第 3-5 週 (P1 服務)

| 服務 | 計畫開始 | 計畫完成 | 交付物 | 進度 |
|:---|:---:|:---:|:---|:---:|
| Insurance | Week 3 Day 4 | Week 4 Day 2 | 5 個測試類 + SQL | ⏳ |
| Project | Week 4 Day 3 | Week 4 Day 5 | 3 個測試類 + SQL | ⏳ |
| Timesheet | Week 5 Day 1 | Week 5 Day 2 | 2 個測試類 + SQL | ⏳ |
| IAM | Week 5 Day 3 | Week 5 Day 5 | 2 個測試類 + SQL | ⏳ |

### 第 5-6 週 (P2 服務)

| 服務 | 計畫開始 | 計畫完成 | 交付物 | 進度 |
|:---|:---:|:---:|:---|:---:|
| Performance | Week 5 Day 6 | Week 6 Day 1 | 4 個測試類 + SQL | ⏳ |
| Recruitment | Week 6 Day 1 | Week 6 Day 2 | 5 個測試類 + SQL | ⏳ |
| Training | Week 6 Day 3 | Week 6 Day 4 | 6 個測試類 + SQL | ⏳ |
| Workflow | Week 6 Day 5 | Week 6 Day 5 | 1 個測試類 + SQL | ⏳ |
| Notification | Week 7 Day 1 | Week 7 Day 1 | 4 個測試類 + SQL | ⏳ |
| Document | Week 7 Day 2 | Week 7 Day 2 | 3 個測試類 + SQL | ⏳ |
| Reporting | Week 7 Day 3 | Week 7 Day 3 | 3 個測試類 + SQL | ⏳ |

---

## ✅ **驗收標準**

### 每個 API 整合測試文件應滿足

- [ ] 正確的類別命名 (遵循 `{Service}{Feature}ApiIntegrationTest` 規範)
- [ ] 完整的 Javadoc 文檔 (中文描述、測試範圍)
- [ ] 正確的 @Sql 配置 (test-data 路徑、cleanup)
- [ ] @BeforeEach 安全上下文設定
- [ ] 至少 3 個 @Nested 測試組
- [ ] 每個測試用例遵循 Given-When-Then 結構
- [ ] 所有斷言使用 AssertJ
- [ ] 沒有 TODO 注釋 (已實現)
- [ ] 所有測試通過 (綠燈)
- [ ] 無測試資料殘留 (cleanup 正確執行)

### 每個 SQL 測試資料檔案應滿足

- [ ] 檔名規範 (`{service}_test_data.sql`)
- [ ] 覆蓋所有狀態轉換
- [ ] 至少 15-30 筆測試資料
- [ ] 包含邊界條件測試資料
- [ ] cleanup.sql 完全清理所有資料

---

## 🚀 **後續執行步驟**

### 立即執行 (第 1 週)
```bash
# 1. 建立所有缺失的 API 整合測試檔案框架
mvn -f scripts/generate-api-tests.pom generate

# 2. 建立所有服務的 test-data 目錄結構
mvn -f scripts/setup-test-data.pom setup

# 3. 執行框架驗證
mvn clean compile -pl hrms-{service}
```

### 漸進式實施 (第 2-7 週)
```bash
# 1. 實現第一個服務的完整測試
cd backend/hrms-payroll
mvn test -Dtest=*ApiIntegrationTest

# 2. 驗證覆蓋率
mvn jacoco:report

# 3. 完成後進行下一個服務
```

---

## 📞 **責任人分配**

| 階段 | 負責人 | 開始日期 | 預期完成 |
|:---|:---|:---:|:---:|
| 第 1 階段 (框架) | DevLead | Week 1 | Week 1 |
| 第 2 階段 (P0) | Team A | Week 2 | Week 3 |
| 第 3 階段 (P1) | Team B | Week 3 | Week 5 |
| 第 4 階段 (P2) | Team A+B | Week 5 | Week 6 |

---

## 📋 **相關文檔**

- 📄 API 整合測試缺失清單 (services_api_test_checklist.md)
- 📄 進度追蹤表 (api_test_progress_tracking.csv)
- 📄 API 整合測試實施指南 (api_integration_test_guide.md)
- 📄 各服務缺失項目詳表 (per_service_missing_apis.md)

---

**文檔更新日期:** 2026-02-03
**狀態:** ✅ 已完成計畫
**下一步:** 開始第 1 階段框架建立

