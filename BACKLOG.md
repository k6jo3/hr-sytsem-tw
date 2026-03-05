# HRMS 剩餘工項待辦

> 最後更新：2026-03-05

---

## 模組狀態總覽

| 代碼 | 模組 | Controllers | Port | 前端 | E2E | 測試 |
|:---:|:---|:---:|:---:|:---:|:---:|:---:|
| HR01 | IAM | 8 | 8081 | Complete | ✅ | ✅ |
| HR02 | Organization | 10 | 8082 | Complete | ✅ | ✅ |
| HR03 | Attendance | 12 | 8083 | Complete | ✅ | ✅ |
| HR04 | Payroll | 8 | 8084 | Complete | ✅ | ✅ |
| HR05 | Insurance | 6 | 8085 | Complete | ✅ | ✅ |
| HR06 | Project | 9 | 8086 | Complete | ✅ | ✅ |
| HR07 | Timesheet | 3 | 8087 | Complete | ✅ | ✅ |
| HR08 | Performance | 6 | 8088 | Complete | ✅ | ✅ |
| HR09 | Recruitment | 9 | 8089 | Complete | ✅ | ✅ |
| HR10 | Training | 10 | 8090 | Complete | ✅ | ✅ |
| HR11 | Workflow | 2 | 8091 | Skeleton | ✅ | ✅ |
| HR12 | Notification | 8 | 8092 | Skeleton | ✅ | ✅ |
| HR13 | Document | 6 | 8093 | Skeleton | ✅ | ✅ |
| HR14 | Reporting | 7 | 8094 | Skeleton | ✅ | ✅ |

**全模組測試：1,843 tests, 0 failures ✅**

---

## A. 前端完整化（4 模組）— 最高優先

HR11-HR14 目前僅有 Skeleton 前端，需完整實作。

| # | 模組 | Port | 目前狀態 | 需實作內容 |
|:---:|:---|:---:|:---|:---|
| A1 | HR11 Workflow | 8091 | Skeleton | 視覺化流程設計器（拖拉元件）、多層簽核 UI、流程實例追蹤、簽核歷程 |
| A2 | HR12 Notification | 8092 | Skeleton | 通知中心列表、已讀/未讀標記、偏好設定（Email/Push/Teams/LINE）、公告管理 |
| A3 | HR13 Document | 8093 | Skeleton | 文件上傳/下載、版本控制 UI、範本管理、加密文件處理、存取日誌 |
| A4 | HR14 Reporting | 8094 | Skeleton | 儀表板拖拉配置、報表查詢（HR/財務/專案）、匯出 Excel/PDF、政府報表 |

**開發流程（每個模組）：**
1. 讀設計書 `knowledge/02_System_Design/{NN}_*.md`
2. 建立 feature module：`api/` + `factory/` + `components/` + `hooks/` + `model/`
3. 實作 Factory（DTO → ViewModel 轉換）
4. 實作頁面與元件
5. 寫測試（Factory + Component + Hook）
6. E2E 驗證

---

## B. 待觀察問題（3 項）

| # | 服務 | 問題 | 可能原因 |
|:---:|:---|:---|:---|
| B1 | HR06 Project | `GET /projects/{id}/cost` 偶發 405 | 路由衝突或 Controller mapping 問題 |
| B2 | HR03 Attendance | 考勤月報 pipeline 跨服務依賴 | `FetchMonthlyReportEmployeesTask` 呼叫 Organization 服務 |
| B3 | HR08 Performance | 績效報表 totalEmployees/avgScore = 0 | 後端未回傳統計資料，需補 Service 邏輯 |

---

## C. 品質強化（依需要）

| # | 項目 | 說明 | 優先級 |
|:---:|:---|:---|:---:|
| C1 | 前端測試覆蓋 | Factory/Component/Hook 單元測試（CLAUDE.md 規定必要） | 高 |
| C2 | 跨服務整合測試 | Kafka Event 端到端驗證（目前用 InMemoryEventPublisher） | 中 |
| C3 | CI/CD Pipeline | GitHub Actions 自動化建置、測試、部署 | 中 |
| C4 | Docker 部署配置 | Docker Compose（開發）/ K8s（生產）配置 | 中 |
| C5 | 效能測試 | JMeter/Gatling 負載測試、壓力測試 | 低 |
| C6 | API 文件自動生成 | Swagger/OpenAPI 自動產生 + Swagger UI | 低 |

---

## D. 文件補充（已完成 ✅）

> 進度：9/9 ✅ 全部完成

| # | 文件 | 狀態 |
|:---:|:---|:---:|
| D1 | `ldap_authentication_flow.md` | ✅ |
| D2 | `salary_advance_and_legal_deduction.md` | ✅ |
| D3 | `shift_scheduling_and_rotation_rules.md` | ✅ |
| D4 | `flexible_time_and_lateness_detection.md` | ✅ |
| D5 | `system_management_business_logic.md` | ✅ |
| D6 | `01_核心業務循序圖.md/.puml` 更新 | ✅ |
| D7 | `03_系統使用案例圖與規格.md/.puml` 更新 | ✅ |
| D8 | `04_核心業務流程圖.md/.puml` 更新 | ✅ |
| D9 | `01_IAM_API_Spec.md` 更新 | ✅ |

---

## Seed Data UUID 對照表

| 類型 | UUID 尾碼 | 名稱 |
|:---|:---|:---|
| 員工 | `...-0000-000000000001` | 王大明 (A001) |
| 員工 | `...-0000-000000000002` | 李小美 (A002) |
| 員工 | `...-0000-000000000003` | 陳志強 (A003) |
| 員工 | `...-0000-000000000004` | 林雅婷 (A004) |
| 專案 | `...-2000-000000000001` | P-2026-001 ERP系統導入 |
| 專案 | `...-2000-000000000002` | P-2026-002 行動APP開發 |
| 專案 | `...-2000-000000000003` | P-2026-003 系統維護合約 |
| 客戶 | `...-1000-000000000001` | C-001 台灣科技 |
| 客戶 | `...-1000-000000000002` | C-002 創新數位 |
| 客戶 | `...-1000-000000000003` | C-003 全球商務顧問 |
| 週期 | `...-8000-000000000001` | 2025年度績效考核 (COMPLETED) |
| 週期 | `...-8000-000000000002` | 2026 Q1 季度考核 (IN_PROGRESS) |
| 考核 | `...-8002-00000000000x` | 6筆（各狀態覆蓋） |
| 職缺 | `...-9001-00000000000x` | 3筆 (OPEN×2, DRAFT×1) |
| 應徵者 | `...-9002-00000000000x` | 6筆（各狀態覆蓋） |
| 面試 | `...-9003-00000000000x` | 3筆 |
| Offer | `...-9004-00000000000x` | 2筆 |
| 課程 | `TRN-C00x` | 4筆 |
| 報名 | `TRN-E00x` | 5筆 |
| 證照 | `TRN-CT00x` | 3筆 |

---

## IAM 帳戶（密碼皆 Admin@123）

| 帳號 | 角色 | employee_id | 對應員工 |
|:---|:---|:---|:---|
| admin | ADMIN | 001 | 王大明 |
| hr_admin | HR | 002 | 李小美 |
| employee | EMPLOYEE | 003 | 陳志強 |
| manager | MANAGER | 003 | 陳志強 |
| pm | PM | 004 | 林雅婷 |
