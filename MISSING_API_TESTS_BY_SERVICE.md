# 📋 各服務缺失 API 整合測試詳細清單

**文檔日期:** 2026-02-03
**目的:** 按服務列出所有缺失的 API 整合測試

---

## **01 IAM 服務** 👤

**狀態:** 部分完成 (3/6, 50%)
**優先級:** P1 (次要)
**計畫週期:** Week 2-3

### 已有測試 ✅
- [x] AuthApiTest
- [x] RoleApiTest
- [x] UserApiTest

### 缺失的整合測試 ❌

#### 1. PermissionApiIntegrationTest.java
**路徑:** `hrms-iam/src/test/java/com/company/hrms/iam/api/controller/`

**涵蓋的 Controller:**
- HR01PermissionQryController

**測試場景:**
- 查詢角色權限清單
- 查詢特定角色的權限詳情
- 查詢使用者的所有權限
- 權限分頁查詢
- 異常處理 (角色不存在、無權限)

**預估代碼行數:** 250-300 行

---

#### 2. ProfileApiIntegrationTest.java
**路徑:** `hrms-iam/src/test/java/com/company/hrms/iam/api/controller/`

**涵蓋的 Controller:**
- HR01ProfileCmdController (建立、修改)
- HR01ProfileQryController (查詢)

**測試場景:**
- 個人檔案查詢
- 修改個人資訊 (名字、電話、地址)
- 更換頭像
- 變更密碼
- 密碼驗證規則
- 異常處理 (密碼不符、無權限修改他人)

**預估代碼行數:** 350-400 行

---

### 所需的測試資料 SQL

**檔案:** `iam_test_data.sql`

**測試資料需求:**
- 20+ 個使用者 (不同角色)
- 10+ 個角色配置
- 50+ 個權限項目
- 權限分配關係

---

## **02 Organization 服務** 🏢

**狀態:** 部分完成 (2/6, 33%)
**優先級:** P0 (優先)
**計畫週期:** Week 2-3

### 已有測試 ✅
- [x] DepartmentApiTest
- [x] EmployeeApiTest (需優化)

### 缺失的整合測試 ❌

#### 1. ContractApiIntegrationTest.java
**路徑:** `hrms-organization/src/test/java/com/company/hrms/organization/api/controller/`

**涵蓋的 Controller:**
- HR02ContractCmdController (建立、修改、終止)
- HR02ContractQryController (查詢)

**測試場景:**
- 建立勞動合約
- 修改合約條款
- 合約生效、終止流程
- 查詢員工的合約歷史
- 合約版本控制
- 異常處理 (無效日期、重複合約)

**預估代碼行數:** 400-450 行

---

#### 2. EssApiIntegrationTest.java
**路徑:** `hrms-organization/src/test/java/com/company/hrms/organization/api/controller/`

**涵蓋的 Controller:**
- HR02EssCmdController (員工自助更新)
- HR02EssQryController (查詢個人資訊)

**測試場景:**
- 員工查詢個人基本資訊
- 員工查詢薪資資訊
- 員工查詢假單資訊
- 員工查詢福利資訊
- 員工修改聯絡資訊
- 權限檢查 (只能查看自己的資訊)
- 異常處理 (越權查詢)

**預估代碼行數:** 350-400 行

---

#### 3. OrganizationApiIntegrationTest.java
**路徑:** `hrms-organization/src/test/java/com/company/hrms/organization/api/controller/`

**涵蓋的 Controller:**
- HR02OrganizationCmdController (建立、修改組織)
- HR02OrganizationQryController (查詢組織結構)

**測試場景:**
- 建立組織單位 (公司、事業部、部門)
- 修改組織結構
- 組織單位停用/啟用
- 查詢組織樹狀結構
- 查詢單位成員
- 異常處理 (無效上級單位、循環參照)

**預估代碼行數:** 400-450 行

---

### 所需的測試資料 SQL

**檔案:** `organization_test_data.sql`

**測試資料需求:**
- 5+ 個組織單位 (不同層級)
- 50+ 個員工記錄
- 20+ 個勞動合約
- 狀態轉換測試資料

---

## **03 Attendance 服務** 📊

**狀態:** 部分完成 (3/7, 43%)
**優先級:** P0 (優先)
**計畫週期:** Week 2-3

### 已有測試 ✅
- [x] CheckInApiTest
- [x] LeaveApiTest
- [x] OvertimeApiTest
- [x] AttendanceApiContractTest

### 缺失的整合測試 ❌

#### 1. LeaveTypeApiIntegrationTest.java
**路徑:** `hrms-attendance/src/test/java/com/company/hrms/attendance/api/controller/`

**涵蓋的 Controller:**
- HR03LeaveTypeCmdController (建立、修改)
- HR03LeaveTypeQryController (查詢)

**測試場景:**
- 建立假別 (病假、事假、特休)
- 修改假別配置
- 設定假別天數
- 假別停用/啟用
- 查詢假別清單
- 查詢假別計算規則
- 異常處理 (重複假別代碼)

**預估代碼行數:** 300-350 行

---

#### 2. MonthCloseApiIntegrationTest.java
**路徑:** `hrms-attendance/src/test/java/com/company/hrms/attendance/api/controller/`

**涵蓋的 Controller:**
- HR03MonthCloseCmdController (月結、重新開啟)

**測試場景:**
- 執行月結 (驗證當月資料完整)
- 異常資料檢查與警告
- 月結後無法新增考勤記錄
- 月結撤銷 (重新開啟月份)
- 月結歷史查詢
- 異常處理 (未完成審核無法月結)

**預估代碼行數:** 300-350 行

---

#### 3. ShiftApiIntegrationTest.java
**路徑:** `hrms-attendance/src/test/java/com/company/hrms/attendance/api/controller/`

**涵蓋的 Controller:**
- HR03ShiftCmdController (建立、修改班別)
- HR03ShiftQryController (查詢班別)

**測試場景:**
- 建立班別 (日班、夜班、輪班)
- 修改班別上下班時間
- 設定加班規則
- 班別停用/啟用
- 查詢班別清單
- 查詢班別員工
- 異常處理 (時間段衝突)

**預估代碼行數:** 350-400 行

---

#### 4. ReportApiIntegrationTest.java (Report Query)
**路徑:** `hrms-attendance/src/test/java/com/company/hrms/attendance/api/controller/`

**涵蓋的 Controller:**
- HR03ReportQryController (查詢考勤報表)

**測試場景:**
- 查詢月度考勤統計
- 查詢部門考勤匯總
- 查詢遲到/早退報表
- 查詢加班報表
- 查詢假勤報表
- 分頁查詢
- 異常處理 (無效日期範圍)

**預估代碼行數:** 300-350 行

---

### 所需的測試資料 SQL

**檔案:** `attendance_test_data.sql`

**測試資料需求:**
- 5+ 個假別設定
- 4+ 個班別配置
- 100+ 個打卡記錄
- 30+ 個請假記錄
- 20+ 個加班記錄
- 月結狀態資料

---

## **04 Payroll 服務** 💰

**狀態:** 部分完成 (2/4, 50%)
**優先級:** P0 (優先)
**計畫週期:** Week 2-3

### 已有測試 ✅
- [x] PayrollRunApiIntegrationTest (框架完成，2026-02-03)
- [x] PayslipApiIntegrationTest (框架完成，2026-02-03)
- [x] PayrollRunApiTest
- [x] SalaryStructureApiTest

### 缺失的整合測試 ❌

#### 1. BankTransferApiIntegrationTest.java
**路徑:** `hrms-payroll/src/test/java/com/company/hrms/payroll/api/controller/`

**涵蓋的 Controller:**
- HR04BankTransferCmdController (建立、修改轉帳)
- HR04BankTransferQryController (查詢轉帳)

**測試場景:**
- 建立銀行轉帳記錄
- 上傳薪轉批檔 (ACH 格式)
- 修改轉帳資訊
- 確認轉帳
- 查詢轉帳歷史
- 查詢員工銀行資訊
- 異常處理 (金額不符、帳號無效)

**預估代碼行數:** 350-400 行

---

#### 2. SalaryStructureApiIntegrationTest.java
**路徑:** `hrms-payroll/src/test/java/com/company/hrms/payroll/api/controller/`

**涵蓋的 Controller:**
- HR04SalaryStructureCmdController (建立、修改)
- HR04SalaryStructureQryController (查詢)

**測試場景:**
- 建立薪資結構 (月薪制、時薪制)
- 新增薪資項目 (收入、扣除)
- 修改薪資結構
- 薪資結構版本控制
- 查詢員工薪資結構
- 查詢薪資項目
- 異常處理 (重複項目、無效金額)

**預估代碼行數:** 400-450 行

---

### 所需的測試資料 SQL

**檔案:** 已納入 Payroll 統一的 `payroll_test_data.sql`

**測試資料需求:**
- 20+ 個薪資批次 (不同狀態)
- 30+ 個薪資單
- 15+ 個薪資結構
- 10+ 個銀行轉帳記錄

---

## **05 Insurance 服務** 🏥

**狀態:** 完全缺失 (0/6, 0%)
**優先級:** P1 (次要)
**計畫週期:** Week 3-4

### 缺失的整合測試 ❌

#### 1. EnrollmentApiIntegrationTest.java
**路徑:** `hrms-insurance/src/test/java/com/company/hrms/insurance/api/controller/`

**涵蓋的 Controller:**
- HR05EnrollmentCmdController (加保、脫保)
- HR05EnrollmentQryController (查詢)

**測試場景:**
- 員工入保流程
- 員工脫保流程
- 調整保險等級
- 查詢加保狀態
- 查詢保險生效日期
- 異常處理 (重複加保、無效脫保)

**預估代碼行數:** 350-400 行

---

#### 2. ExportApiIntegrationTest.java
**路徑:** `hrms-insurance/src/test/java/com/company/hrms/insurance/api/controller/`

**涵蓋的 Controller:**
- HR05ExportCmdController (產生單據)

**測試場景:**
- 產生月度保險申報單據
- 產生年度申報單據
- 產生員工保險證明
- 匯出 Excel 格式
- 簽核單據
- 異常處理 (無效月份、無資料)

**預估代碼行數:** 300-350 行

---

#### 3. FeeApiIntegrationTest.java
**路徑:** `hrms-insurance/src/test/java/com/company/hrms/insurance/api/controller/`

**涵蓋的 Controller:**
- HR05FeeCmdController (計算、調整)

**測試場景:**
- 計算月度保費
- 費率表更新
- 調整個人保費
- 補繳保費
- 查詢保費明細
- 異常處理 (無效費率)

**預估代碼行數:** 300-350 行

---

#### 4. LevelApiIntegrationTest.java
**路徑:** `hrms-insurance/src/test/java/com/company/hrms/insurance/api/controller/`

**涵蓋的 Controller:**
- HR05LevelQryController (查詢等級)

**測試場景:**
- 查詢保險等級清單
- 查詢投保金額級距
- 查詢費率對應表
- 查詢級距生效日期
- 異常處理 (無效級距)

**預估代碼行數:** 250-300 行

---

#### 5. MyInsuranceApiIntegrationTest.java
**路徑:** `hrms-insurance/src/test/java/com/company/hrms/insurance/api/controller/`

**涵蓋的 Controller:**
- HR05MyInsuranceQryController (個人查詢)

**測試場景:**
- 查詢個人保險資訊
- 查詢個人投保金額
- 查詢個人保費
- 查詢理賠查詢
- 權限檢查 (只能查看自己的資訊)

**預估代碼行數:** 250-300 行

---

### 所需的測試資料 SQL

**檔案:** `insurance_test_data.sql`

**測試資料需求:**
- 50+ 個員工保險記錄
- 12+ 個月份的保費資料
- 10+ 個保險等級
- 理賠記錄

---

## **06 Project 服務** 📈

**狀態:** 部分完成 (3/6, 50%)
**優先級:** P1 (次要)
**計畫週期:** Week 4-5

### 已有測試 ✅
- [x] ProjectCommandApiTest
- [x] ProjectCostApiTest
- [x] ProjectQueryApiTest

### 缺失的整合測試 ❌

#### 1. CustomerApiIntegrationTest.java
**路徑:** `hrms-project/src/test/java/com/company/hrms/project/api/controller/`

**涵蓋的 Controller:**
- HR06CustomerCmdController (建立、修改)
- HR06CustomerQryController (查詢)

**測試場景:**
- 建立客戶檔案
- 修改客戶資訊
- 新增聯絡人
- 設定信用額度
- 查詢客戶清單
- 查詢客戶專案
- 異常處理 (重複統編、無效聯絡人)

**預估代碼行數:** 350-400 行

---

#### 2. MemberApiIntegrationTest.java
**路徑:** `hrms-project/src/test/java/com/company/hrms/project/api/controller/`

**涵蓋的 Controller:**
- HR06MemberCmdController (新增、修改)

**測試場景:**
- 新增專案成員
- 分配成員角色 (PM、開發、QA)
- 設定成員成本
- 修改成員資訊
- 移除成員
- 查詢成員清單
- 異常處理 (重複分配、無效角色)

**預估代碼行數:** 350-400 行

---

#### 3. TaskApiIntegrationTest.java
**路徑:** `hrms-project/src/test/java/com/company/hrms/project/api/controller/`

**涵蓋的 Controller:**
- HR06TaskCmdController (建立、修改)
- HR06TaskQryController (查詢)

**測試場景:**
- 建立任務項目
- 修改任務資訊
- 指派任務
- 更新進度
- 查詢任務清單
- 查詢任務依賴關係
- 異常處理 (無效依賴、無權限修改)

**預估代碼行數:** 400-450 行

---

### 所需的測試資料 SQL

**檔案:** `project_test_data.sql` (如果沒有)

**測試資料需求:**
- 10+ 個客戶記錄
- 5+ 個專案
- 50+ 個成員分配
- 100+ 個任務項目

---

## **07 Timesheet 服務** ⏰

**狀態:** 完全缺失 (0/2, 0%)
**優先級:** P1 (次要)
**計畫週期:** Week 5

### 缺失的整合測試 ❌

#### 1. TimesheetApiIntegrationTest.java
**路徑:** `hrms-timesheet/src/test/java/com/company/hrms/timesheet/api/controller/`

**涵蓋的 Controller:**
- HR07TimesheetCmdController (建立、提交)
- HR07TimesheetQryController (查詢)

**測試場景:**
- 員工建立週工時
- 分配工時到專案/任務
- 提交工時審核
- 主管審核/駁回
- 查詢工時詳情
- 查詢工時歷史
- 異常處理 (工時不符、無效專案)

**預估代碼行數:** 400-450 行

---

#### 2. TimesheetReportApiIntegrationTest.java
**路徑:** `hrms-timesheet/src/test/java/com/company/hrms/timesheet/api/controller/`

**涵蓋的 Controller:**
- HR07TimesheetReportQryController (查詢報表)

**測試場景:**
- 查詢月度工時統計
- 查詢專案工時彙總
- 查詢成本追蹤 (與薪資整合)
- 查詢部門工時分析
- 匯出報表
- 異常處理 (無效日期)

**預估代碼行數:** 300-350 行

---

### 所需的測試資料 SQL

**檔案:** `timesheet_test_data.sql`

**測試資料需求:**
- 50+ 個工時記錄
- 10+ 週期的資料
- 不同狀態的工時 (草稿、已提交、已審核、已駁回)

---

## **08 Performance 服務** ⭐

**狀態:** 完全缺失 (0/6, 0%)
**優先級:** P2 (後續)
**計畫週期:** Week 6-7

### 缺失的整合測試 ❌

#### 1. CycleApiIntegrationTest.java
#### 2. ReviewApiIntegrationTest.java
#### 3. TemplateApiIntegrationTest.java
#### 4. ReportApiIntegrationTest.java

**共同特徵:**
- 涉及績效評核的完整生命週期
- 包含多層審核流程
- 與部門、員工資料整合

**預估行數:** 300-400 行/各

---

## **09 Recruitment 服務** 🎯

**狀態:** 完全缺失 (0/9, 0%)
**優先級:** P2 (後續)
**計畫週期:** Week 6-7

### 缺失的整合測試 ❌

#### 1. JobApiIntegrationTest.java
#### 2. CandidateApiIntegrationTest.java
#### 3. InterviewApiIntegrationTest.java
#### 4. OfferApiIntegrationTest.java
#### 5. ReportApiIntegrationTest.java

**共同特徵:**
- 涉及招募流程的各個環節
- 包含面試多輪機制
- 與組織、薪資資料整合

**預估行數:** 300-400 行/各

---

## **10 Training 服務** 📚

**狀態:** 大部分缺失 (1/10, 10%)
**優先級:** P2 (後續)
**計畫週期:** Week 7-8

### 已有測試 ✅
- [x] TrainingApiContractTest (契約測試)

### 缺失的整合測試 ❌

#### 1. CourseApiIntegrationTest.java
#### 2. EnrollmentApiIntegrationTest.java
#### 3. CertificateApiIntegrationTest.java
#### 4. MyTrainingApiIntegrationTest.java
#### 5. ReportApiIntegrationTest.java
#### 6. StatisticsApiIntegrationTest.java

**共同特徵:**
- 涉及課程、報名、認證的完整流程
- 包含員工個人訓練查詢
- 包含訓練統計分析

**預估行數:** 300-400 行/各

---

## **11 Workflow 服務** 🔄

**狀態:** 完全缺失 (0/2, 0%)
**優先級:** P2 (後續)
**計畫週期:** Week 8

### 缺失的整合測試 ❌

#### 1. WorkflowApiIntegrationTest.java
#### 2. WorkflowExecutionApiIntegrationTest.java

**預估行數:** 350-400 行/各

---

## **12 Notification 服務** 📧

**狀態:** 完全缺失 (0/8, 0%)
**優先級:** P2 (後續)
**計畫週期:** Week 8-9

### 缺失的整合測試 ❌

#### 1. NotificationApiIntegrationTest.java
#### 2. AnnouncementApiIntegrationTest.java
#### 3. TemplateApiIntegrationTest.java
#### 4. PreferenceApiIntegrationTest.java

**共同特徵:**
- 涉及多種通知管道 (Email、Push、Teams、LINE)
- 包含公告發佈
- 包含使用者偏好設定

**預估行數:** 300-350 行/各

---

## **13 Document 服務** 📄

**狀態:** 部分缺失 (1/6, 16.7%)
**優先級:** P2 (後續)
**計畫週期:** Week 9

### 已有測試 ✅
- [x] DocumentApiContractTest (契約測試)

### 缺失的整合測試 ❌

#### 1. DocumentApiIntegrationTest.java
#### 2. RequestApiIntegrationTest.java
#### 3. TemplateApiIntegrationTest.java

**預估行數:** 350-400 行/各

---

## **14 Reporting 服務** 📊

**狀態:** 完全缺失 (0/4, 0%)
**優先級:** P2 (後續)
**計畫週期:** Week 9-10

### 缺失的整合測試 ❌

#### 1. DashboardApiIntegrationTest.java
#### 2. ReportApiIntegrationTest.java
#### 3. ExportApiIntegrationTest.java
#### 4. AnalyticsApiIntegrationTest.java

**共同特徵:**
- 涉及報表查詢和資料分析
- 包含多種匯出格式
- 包含權限驗證

**預估行數:** 300-400 行/各

---

## 📊 **統計摘要**

```
總計需建立: 74 個測試檔案

按優先級:
├─ P0 (優先)      : 11 個 (Payroll 4 + Organization 4 + Attendance 3)
├─ P1 (次要)      : 14 個 (IAM 2 + Insurance 5 + Project 3 + Timesheet 2 + Training 1 + Document 1)
└─ P2 (後續)      : 49 個 (Performance 4 + Recruitment 5 + Training 5 + Workflow 2 + Notification 4 + Document 2 + Reporting 4 + 其他)

按狀態:
├─ 框架完成       : 2 個 (Payroll)
├─ 部分完成       : 0 個
└─ 待建立        : 72 個

總代碼行數預估: 25,000+ 行

預計週期: 6-8 週 (按優先級逐步實施)
```

---

**文檔更新:** 2026-02-03
**下一步:** 開始建立 P0 優先級的測試檔案

