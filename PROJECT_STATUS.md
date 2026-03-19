# HRMS 專案現況與改善計畫

> **最後更新：** 2026-03-19
> **版本：** v3.0（改善計畫 8/10 項完成）

---

## 一、系統現況總覽

### 1.1 架構規模

| 指標 | 數量 |
|:---|:---:|
| 微服務數量 | 14 + Gateway |
| 後端 API 端點 | 351 |
| 後端 ServiceImpl | 314 |
| 後端測試 | 1,843（0 failures） |
| 前端頁面 | 54 路由 |
| 前端 API 函式 | 178 |
| 前端測試 | 通過率 > 95%（9 個既有失敗） |
| Kafka 事件消費者 | 9 |

### 1.2 各服務實作狀態

| # | 服務 | 後端 | 前端 | Domain 邏輯 | Kafka | 測試數 |
|:---:|:---|:---:|:---:|:---|:---:|:---:|
| 01 | IAM 認證授權 | ✅ | ✅ | 登入鎖定、RBAC、Token 刷新、LDAP | 2 | 39 |
| 02 | 組織員工 | ✅ | ✅ | 員工生命週期、組織樹、合約管理 | 0 | 25 |
| 03 | 考勤管理 | ✅ | ✅ | 打卡規則引擎、加班計算、請假扣薪 | 1 | 41 |
| 04 | 薪資管理 | ✅ | ✅ | 批次狀態機（6 態）、Saga 補償、稅務 | 1 | 38 |
| 05 | 保險管理 | ✅ | ✅ | 級距對照、費率計算、65 歲規則、眷口 | 1 | 20 |
| 06 | 專案管理 | ✅ | ✅ | 多層 WBS、成本追蹤、任務狀態機 | 0 | 29 |
| 07 | 工時管理 | ✅ | ✅ | 週報狀態機、24h 上限、PM 審核 | 0 | 25 |
| 08 | 績效管理 | ✅ | ✅ | 加權評分、多階段考核、評等轉換 | 0 | 15 |
| 09 | 招募管理 | ✅ | ✅ | Kanban 流程、薪資提案、Offer 簽核 | 0 | 22 |
| 10 | 訓練管理 | ✅ | ✅ | 課程發布、報名審核、證書管理 | 0 | 5 |
| 11 | 簽核流程 | ✅ | ✅ | 多層簽核引擎、任務委派、代理人 | 0 | 9 |
| 12 | 通知服務 | ✅ | ✅ | 多通道分流、偏好設定、已讀追蹤 | 0 | 9 |
| 13 | 文件管理 | ✅ | ✅ | 版控、可見性、分類、加密 | 0 | 9 |
| 14 | 報表分析 | ✅ | ✅ | CQRS ReadModel、儀表板、政府報表 | 5 | 7 |

### 1.3 已完成的基礎建設

- ✅ Docker 容器化（三環境：dev / local / prod）
- ✅ CI/CD（GitHub Actions → ghcr.io → VPS / Azure）
- ✅ 文件自動生成（E1-E7 共 49 份 PDF/PPTX）
- ✅ 合約驅動測試框架
- ✅ Mock 環境變數控制（`VITE_MOCK`）
- ✅ 前端 Factory 模式統一 DTO 轉換

---

## 二、修正後的系統評分：82 / 100

| 維度 | 滿分 | 得分 | 說明 |
|:---|:---:|:---:|:---|
| 架構設計 | 15 | 13 | DDD + CQRS + Event-Driven，Pipeline 編排成熟。Kafka 事件消費者覆蓋不完整。 |
| 後端實作 | 15 | 13 | 314 個 Service，Domain 層有真實業務邏輯。4 個支援服務的邊界層待完善。 |
| 前端實作 | 15 | 11 | 54 頁面 + 178 API + Factory 模式。部分 UI 細節問題，響應式未全面驗證。 |
| 測試覆蓋 | 15 | 11 | 1,843 後端測試全過，前端有 Factory/Hook/Component 測試。9 個前端測試未修，缺自動化 E2E。 |
| 文件品質 | 10 | 9 | 14 服務全有設計文件，操作手冊自動生成。 |
| CI/CD 與部署 | 10 | 8 | 雙部署（VPS + Azure），未實際驗證 Azure 完整流程。 |
| 資安與品質 | 10 | 8 | JWT + RBAC + 非 root 容器。缺 OWASP 掃描、rate limiting。 |
| 可維運性 | 10 | 9 | Healthcheck + 環境變數驅動 + 三環境 Docker。缺集中式 log 和 metrics。 |

---

## 三、問題清單與改善計畫

### P1：高優先（影響作品集展示品質）

#### P1-1：前端 9 個既有測試失敗
- **現況：** attendance 和 timesheet 模組有 9 個測試失敗
- **影響：** CI/CD 前端測試步驟不穩定
- **改善：** 逐一修復或更新測試預期值
- **預估：** 1 天

#### P1-2：前端 UI 細節問題
- **現況：** 截圖檢查時發現多個 UI bug（跑版、錯誤訊息不精確、空白畫面）
- **影響：** 面試 demo 時可能被注意到
- **改善：** 系統性逐頁 UI 審查 + 修復
- **預估：** 2-3 天

#### P1-3：E2E 測試缺乏自動化
- **現況：** 手動點擊 + 截圖驗證，無自動化腳本
- **影響：** 無法確保每次變更不會破壞既有功能
- **改善：** 引入 Playwright，至少覆蓋核心流程（登入 → 員工管理 → 打卡 → 請假 → 薪資）
- **預估：** 3-5 天

### P2：中優先（影響技術深度評價）

#### P2-1：Kafka 事件消費者覆蓋不完整
- **現況：** 14 個服務中只有 5 個有 `@KafkaListener`，其他 9 個的跨服務通訊靠同步 API
- **影響：** 面試時被問「Event-Driven 架構怎麼落地」可能答不上來
- **改善：** 為核心業務流程補充事件消費者
  - Organization → Attendance（員工入職自動建立考勤記錄）
  - Attendance → Payroll（請假/加班事件同步薪資計算）
  - Payroll → Insurance（薪資變更觸發保費重算）
  - Recruitment → Organization（錄取通知觸發員工建檔）
- **預估：** 3-5 天

#### P2-2：支援服務邊界層待完善
- **現況：** Notification（通道適配器）、Document（儲存層）、Workflow（業務串接）邏輯完備但邊界層薄
- **影響：** 這 3 個服務可以 demo 畫面但深入操作會發現功能不完整
- **改善：**
  - Notification：實作 Email 通道適配器（用 JavaMailSender）
  - Document：實作本地檔案儲存適配器
  - Workflow：串接請假、加班簽核流程
- **預估：** 5-7 天

#### P2-3：低覆蓋模組測試補充
- **現況：** Training (5)、Reporting (7)、Workflow (9)、Notification (9)、Document (9) 測試數偏低
- **影響：** 整體測試覆蓋率不均勻
- **改善：** 每個模組至少補到 15-20 個測試
- **預估：** 3-5 天

### P3：低優先（錦上添花）

#### P3-1：資安強化
- **改善：** 加入 OWASP dependency check、API rate limiting、CORS 精細化
- **預估：** 2-3 天

#### P3-2：可觀測性
- **改善：** 整合 Spring Boot Actuator + Micrometer + Prometheus（或至少 structured logging）
- **預估：** 2-3 天

#### P3-3：Azure 部署完整驗證
- **改善：** 實際在 Azure Container Apps 上完整部署一次，驗證所有服務能正常啟動
- **預估：** 1 天

---

## 四、改善優先順序與時程

```
第 1 週：P1-1 + P1-2（前端修復）
第 2 週：P1-3（自動化 E2E）+ P2-1（Kafka 事件串接）
第 3 週：P2-2（邊界層完善）+ P2-3（測試補充）
第 4 週：P3-1 + P3-2 + P3-3（資安 / 可觀測性 / Azure 驗證）
```

**完成後預估分數：90-92 / 100**

---

## 五、面試準備建議

### 可以自信回答的問題

1. **「DDD 怎麼分層？」** — 14 個服務嚴格遵循 Interface/Application/Domain/Infrastructure 四層
2. **「CQRS 怎麼做？」** — Controller 分 Cmd/Qry，Service Factory 自動對應 bean name
3. **「微服務間怎麼通訊？」** — Kafka 事件驅動（可舉 IAM/Payroll/Insurance/Reporting 的 listener 實例）
4. **「測試策略？」** — 合約驅動測試 + Domain 單元測試 + API 整合測試，1,843 個測試
5. **「CI/CD 流程？」** — push → 測試 → Docker build（矩陣並行 14 個）→ ghcr.io → VPS/Azure 部署
6. **「薪資計算怎麼做？」** — Pipeline 編排 + 批次狀態機（6 態）+ Saga 補償

### 需要準備的問題

1. **「Kafka 停了會怎樣？」** — 各服務獨立運作不受影響，只是跨服務的非同步通訊斷了
2. **「為什麼有些服務沒有 Kafka Consumer？」** — 設計上先用同步 API，已規劃改為事件驅動（說明 P2-1）
3. **「E2E 怎麼測的？」** — 目前是手動驗證 + 截圖，規劃引入 Playwright 自動化（說明 P1-3）
4. **「Notification 的 Email 發送？」** — Domain 邏輯和通道路由完備，適配器層待實作（說明 P2-2）

---

## 六、技術債清單

| ID | 類型 | 描述 | 位置 | 影響 |
|:---|:---|:---|:---|:---|
| TD-01 | 測試 | 前端 9 個測試失敗 | attendance/timesheet | CI 不穩定 |
| TD-02 | 測試 | 5 個模組測試數 < 10 | Training/Reporting/Workflow/Notification/Document | 覆蓋率不均 |
| TD-03 | 架構 | 9 個服務無 Kafka Consumer | Organization/Project/Timesheet/Performance/Recruitment/Training/Workflow/Notification/Document | 事件驅動不完整 |
| TD-04 | 實作 | Notification 通道適配器未完成 | hrms-notification | Email/Teams/LINE 發不出去 |
| TD-05 | 實作 | Document 儲存層適配器未完成 | hrms-document | 檔案實際上沒存到磁碟 |
| TD-06 | 實作 | Workflow 與業務流程未串接 | hrms-workflow | 簽核引擎獨立運作 |
| TD-07 | 測試 | H2 與 PostgreSQL 差異 | 全域 | 部分 SQL 在 H2 跑不了 |
| TD-08 | 資安 | 無 OWASP 掃描、無 rate limiting | 全域 | 安全風險 |
| TD-09 | 維運 | 無集中式 log / metrics | 全域 | 問題排查困難 |
| TD-10 | 部署 | Azure 部署未實際驗證 | azure-cd.yml | 不確定能不能跑 |
