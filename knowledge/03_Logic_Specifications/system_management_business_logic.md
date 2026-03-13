# 系統管理模組 - 業務邏輯規格書

**版本:** 1.1
**日期:** 2026-03-13
**文件定位:** 補充 `01_IAM服務系統設計書` 第 13 章，深入描述系統管理模組的領域模型、業務規則、驗證邏輯與領域事件。
**所屬服務:** HR01 IAM（整合於 IAM 服務）

---

## 1. 文件概述

### 1.1 模組定位

系統管理模組整合於 IAM 服務（HR01），提供 IT 管理員（ADMIN 角色）日常維運所需工具。本模組涵蓋三大核心功能：

| 功能 | Aggregate Root | 職責 |
|:---|:---|:---|
| 功能開關管理 | `FeatureToggle` | 控制各模組業務功能的即時啟停，免重啟服務 |
| 系統參數管理 | `SystemParameter` | 管理全域及各模組業務參數，支援 audit trail |
| 排程管理 | `ScheduledJobConfig` | 管理排程任務的 Cron 表達式與啟停控制 |

### 1.2 權限要求

所有系統管理 API 均需 **ADMIN** 角色（即 `SYSTEM_ADMIN` 系統角色）方可存取。一般員工、HR 管理員、專案經理等角色不可操作本模組。

### 1.3 多租戶隔離

三個聚合根均包含 `tenantId` 欄位，查詢與更新操作 **必須** 加上 tenant 條件，確保租戶間資料隔離。唯一約束亦為 `(code, tenant_id)` 組合，允許不同租戶擁有相同代碼但不同設定。

---

## 2. 功能開關管理（Feature Toggle）

### 2.1 設計理念

功能開關採用 **靜態開關（Static Toggle）** 模式，由管理員手動控制，而非根據流量或 A/B 測試動態切換。開關狀態持久化至資料庫，各服務在執行業務邏輯前查詢開關狀態，決定是否執行對應功能。

### 2.2 開關類型

| 功能代碼 | 名稱 | 所屬模組 | 預設狀態 | 說明 |
|:---|:---|:---:|:---:|:---|
| `LATE_CHECK` | 遲到判定 | HR03 | ON | 啟用考勤遲到自動判定功能 |
| `LATE_SALARY_DEDUCTION` | 遲到扣薪 | HR03 | ON | 啟用遲到連動薪資扣款 |
| `SHIFT_SCHEDULING` | 輪班排程 | HR03 | ON | 啟用輪班排程管理功能 |
| `SALARY_ADVANCE` | 薪資預借 | HR04 | ON | 啟用薪資預借申請功能 |
| `LEGAL_DEDUCTION` | 法扣款 | HR04 | ON | 啟用法院扣押執行功能 |
| `LDAP_AUTH` | LDAP 認證 | HR01 | OFF | 啟用 LDAP/AD 企業登入整合 |
| `ABSENT_DETECTION` | 曠職自動判定 | HR03 | ON | 啟用每日下班後曠職自動判定排程 |
| `AUTO_INSURANCE_WITHDRAW` | 離職自動退保 | HR05 | ON | 啟用離職連動保險自動退保 |

### 2.3 啟用/停用規則

**狀態轉換：**

```
┌──────────────────────────────────────────┐
│            Feature Toggle 狀態            │
│                                          │
│   ┌──────────┐          ┌──────────┐    │
│   │ enabled  │ ──────→  │ disabled │    │
│   │ = true   │ disable()│ = false  │    │
│   │          │ ←──────  │          │    │
│   └──────────┘ enable() └──────────┘    │
│        ↑                     ↑           │
│        └─────── toggle() ────┘           │
│              （反轉狀態）                  │
└──────────────────────────────────────────┘
```

**業務規則：**

1. **enable(operator)** — 將 `enabled` 設為 `true`，記錄 `updatedAt` 與 `updatedBy`
2. **disable(operator)** — 將 `enabled` 設為 `false`，記錄 `updatedAt` 與 `updatedBy`
3. **toggle(operator)** — 反轉 `enabled` 值（`!enabled`），記錄 `updatedAt` 與 `updatedBy`
4. 操作者（operator）必須為具 ADMIN 角色的使用者
5. 開關變更即時生效，無需重啟任何服務

### 2.4 影響範圍

功能開關的影響範圍跨越多個微服務。各服務在執行受控功能前，應透過以下方式查詢開關狀態：

| 查詢方式 | 適用場景 | 說明 |
|:---|:---|:---|
| **API 即時查詢** | 低頻操作 | 直接呼叫 IAM 服務的功能開關查詢 API |
| **Redis 快取** | 高頻操作 | 開關狀態快取至 Redis，更新時透過事件清除快取 |
| **啟動載入** | 服務啟動時 | 服務啟動時批量載入所有相關開關至記憶體 |

**跨服務影響矩陣：**

| 功能開關 | 影響的服務 | 影響的業務流程 |
|:---|:---|:---|
| `LATE_CHECK` | HR03 考勤 | 打卡時不執行遲到判定 |
| `LATE_SALARY_DEDUCTION` | HR03 考勤 → HR04 薪資 | 遲到不連動扣薪計算 |
| `SHIFT_SCHEDULING` | HR03 考勤 | 停用輪班排程功能，僅支援固定班 |
| `SALARY_ADVANCE` | HR04 薪資 | 停用薪資預借申請入口 |
| `LEGAL_DEDUCTION` | HR04 薪資 | 停用法扣款計算項目 |
| `LDAP_AUTH` | HR01 IAM | 停用 LDAP/AD 登入，僅支援本地帳號登入 |
| `ABSENT_DETECTION` | HR03 考勤 | 停用每日曠職自動判定排程 |
| `AUTO_INSURANCE_WITHDRAW` | HR05 保險 | 離職時不自動觸發退保流程 |

### 2.5 資料表結構

```sql
CREATE TABLE IF NOT EXISTS feature_toggles (
    id              VARCHAR(36) PRIMARY KEY,
    feature_code    VARCHAR(100) NOT NULL,     -- 功能代碼（唯一識別）
    feature_name    VARCHAR(200) NOT NULL,     -- 功能名稱（顯示用）
    module          VARCHAR(20)  NOT NULL,     -- 所屬模組（HR01-HR14）
    enabled         BOOLEAN DEFAULT TRUE,      -- 是否啟用
    description     VARCHAR(500),              -- 功能說明
    tenant_id       VARCHAR(36),               -- 多租戶隔離
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by      VARCHAR(50),               -- 最後異動者
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (feature_code, tenant_id)
);

CREATE INDEX idx_feature_toggle_code   ON feature_toggles(feature_code);
CREATE INDEX idx_feature_toggle_module ON feature_toggles(module);
```

---

## 3. 系統參數管理

### 3.1 參數分類

系統參數依 `category` 區分為四大分類：

| 分類代碼 | 名稱 | 說明 | 修改頻率 |
|:---|:---|:---|:---:|
| `SECURITY` | 安全參數 | 登入失敗上限、鎖定時長、密碼規則 | 低 |
| `BUSINESS` | 業務參數 | 薪資預借上限、法扣規則、保險費率 | 中 |
| `UI` | 介面參數 | 分頁筆數、預設語系、主題色彩 | 中 |
| `SYSTEM` | 系統參數 | 排程 Cron、逾時秒數、快取 TTL | 低 |

### 3.2 參數型別

`paramType` 定義參數值的資料型別，業務層負責型別轉換：

| 型別代碼 | Java 轉換方法 | 說明 | 範例 |
|:---|:---|:---|:---|
| `STRING` | 直接取用 `paramValue` | 字串型參數 | `"0 0 19 * * ?"` |
| `INTEGER` | `Integer.parseInt(paramValue)` | 整數型參數 | `"5"` → `5` |
| `DECIMAL` | `new BigDecimal(paramValue)` | 小數型參數 | `"0.9"` → `0.9` |
| `BOOLEAN` | `Boolean.parseBoolean(paramValue)` | 布林型參數 | `"true"` → `true` |
| `JSON` | JSON 反序列化 | 複雜結構型參數 | `"{\"key\": \"value\"}"` |

### 3.3 預設參數清單

| 參數代碼 | 名稱 | 預設值 | 型別 | 模組 | 分類 | 說明 |
|:---|:---|:---:|:---:|:---:|:---:|:---|
| `MAX_FAILED_LOGIN_ATTEMPTS` | 登入失敗上限 | `5` | INTEGER | HR01 | SECURITY | 帳號鎖定前允許的最大登入失敗次數 |
| `ACCOUNT_LOCK_DURATION_MINUTES` | 帳號鎖定時長 | `30` | INTEGER | HR01 | SECURITY | 帳號鎖定時長（分鐘） |
| `PASSWORD_MIN_LENGTH` | 密碼最短長度 | `8` | INTEGER | HR01 | SECURITY | 密碼最低字元數 |
| `SALARY_ADVANCE_MAX_RATE` | 預借上限比率 | `0.9` | DECIMAL | HR04 | BUSINESS | 可預借金額佔（淨薪-法扣）的比率上限 |
| `GARNISHMENT_ONE_THIRD_RULE` | 法扣三分之一規則 | `true` | BOOLEAN | HR04 | BUSINESS | 是否啟用強制執行法 §115-1 三分之一扣薪上限 |
| `ABSENT_DETECTION_CRON` | 曠職判定排程 | `0 0 19 * * ?` | STRING | HR03 | SYSTEM | 曠職自動判定排程的 Cron 表達式 |

### 3.4 存取權限

| 操作 | 權限要求 | 說明 |
|:---|:---|:---|
| 查詢參數列表 | ADMIN | 可查看全部參數（含加密參數的遮罩值） |
| 查詢單一參數 | ADMIN | 加密參數回傳遮罩值（`****`） |
| 更新參數值 | ADMIN | 更新後自動記錄異動軌跡 |
| 重設為預設值 | ADMIN | 還原至 `defaultValue`，同樣記錄異動軌跡 |

### 3.5 參數生效機制

```
┌─────────────────────────────────────────────────────────────┐
│                    參數更新流程                               │
│                                                             │
│  管理員        SystemParameter       ParameterChangeLog     │
│    │                │                       │               │
│    │  updateValue() │                       │               │
│    │───────────────→│                       │               │
│    │                │ 1. 儲存 oldValue       │               │
│    │                │ 2. 更新 paramValue     │               │
│    │                │ 3. 更新 updatedAt      │               │
│    │                │ 4. 更新 updatedBy      │               │
│    │                │                       │               │
│    │                │  回傳 ParameterChange  │               │
│    │←───────────────│                       │               │
│    │                │                       │               │
│    │                │   INSERT 異動記錄      │               │
│    │                │──────────────────────→│               │
│    │                │  (paramCode,           │               │
│    │                │   oldValue,            │               │
│    │                │   newValue,            │               │
│    │                │   operator,            │               │
│    │                │   changedAt)           │               │
│    │                │                       │               │
│    │  [選配] 發布 SystemParameterUpdatedEvent │              │
│    │  → 各服務清除快取 / 重載參數            │               │
└─────────────────────────────────────────────────────────────┘
```

**生效時機：**

1. **即時生效：** 資料庫值更新後，下次查詢即取得新值
2. **快取刷新：** 若參數有 Redis 快取，需透過事件機制通知相關服務清除快取
3. **重啟生效：** 少數嵌入 Spring 配置的參數（如安全參數），可能需重啟服務生效

### 3.6 加密參數處理

當 `isEncrypted = true` 時：

1. **寫入：** 使用 AES-256 加密後儲存至 `paramValue`
2. **讀取：** 解密後回傳（僅限後端內部使用），API 回傳一律遮罩為 `****`
3. **適用場景：** 第三方服務密鑰、SMTP 密碼、LDAP 綁定密碼等
4. **異動記錄：** `parameter_change_logs` 中的 `old_value` / `new_value` 同樣儲存遮罩值

### 3.7 資料表結構

```sql
-- 系統參數主表
CREATE TABLE IF NOT EXISTS system_parameters (
    id              VARCHAR(36)   PRIMARY KEY,
    param_code      VARCHAR(100)  NOT NULL,       -- 參數代碼（唯一識別）
    param_name      VARCHAR(200)  NOT NULL,       -- 參數名稱（顯示用）
    param_value     VARCHAR(2000),                -- 參數值（字串格式）
    param_type      VARCHAR(20)   NOT NULL DEFAULT 'STRING',  -- 型別
    module          VARCHAR(20)   NOT NULL DEFAULT 'GLOBAL',  -- 所屬模組
    category        VARCHAR(20)   NOT NULL DEFAULT 'SYSTEM',  -- 分類
    description     VARCHAR(500),                 -- 說明
    default_value   VARCHAR(2000),                -- 預設值
    tenant_id       VARCHAR(36),                  -- 多租戶隔離
    is_encrypted    BOOLEAN DEFAULT FALSE,        -- 是否加密儲存
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by      VARCHAR(50),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (param_code, tenant_id)
);

-- 參數異動記錄表（僅 INSERT，不可 UPDATE/DELETE）
CREATE TABLE IF NOT EXISTS parameter_change_logs (
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    param_code  VARCHAR(100)  NOT NULL,       -- 參數代碼
    old_value   VARCHAR(2000),                -- 舊值
    new_value   VARCHAR(2000),                -- 新值
    operator    VARCHAR(50)   NOT NULL,       -- 操作者
    changed_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_sys_param_code   ON system_parameters(param_code);
CREATE INDEX idx_sys_param_module ON system_parameters(module);
CREATE INDEX idx_param_change_code ON parameter_change_logs(param_code);
```

---

## 4. 排程管理

### 4.1 排程任務類型

| 任務代碼 | 名稱 | 所屬模組 | Cron 表達式 | 預設狀態 | 說明 |
|:---|:---|:---:|:---|:---:|:---|
| `ABSENT_DETECTION` | 曠職自動判定 | HR03 | `0 0 19 * * ?` | ON | 每日 19:00 掃描無打卡且無請假的員工 |
| `ANNUAL_LEAVE_SETTLEMENT` | 特休年度結算 | HR03 | `0 0 1 1 1 ?` | ON | 每年 1/1 凌晨結算特休假（結轉/折薪/作廢） |
| `INSURANCE_DAILY_REPORT` | 保險每日異動報表 | HR05 | `0 30 8 * * ?` | ON | 每日 08:30 匯出加退保異動清單 |
| `PAYROLL_MONTHLY_CLOSE` | 薪資月結 | HR04 | `0 0 2 1 * ?` | ON | 每月 1 日凌晨 2:00 執行薪資月結 |

### 4.2 Cron 表達式格式

採用 Spring Scheduler 的 6 欄位 Cron 格式：

```
秒  分  時  日  月  週
 │   │   │   │   │   │
 │   │   │   │   │   └── 0-7 或 SUN-SAT（0/7 = 星期日）
 │   │   │   │   └────── 1-12 或 JAN-DEC
 │   │   │   └────────── 1-31
 │   │   └────────────── 0-23
 │   └────────────────── 0-59
 └────────────────────── 0-59
```

**特殊字元：**

| 字元 | 意義 | 範例 |
|:---:|:---|:---|
| `*` | 任意值 | `* * * * * ?` = 每秒 |
| `?` | 不指定（日與週互斥用） | `0 0 19 * * ?` = 每日 19:00 |
| `-` | 範圍 | `0 0 9-17 * * ?` = 9:00~17:00 每小時 |
| `,` | 列舉 | `0 0 8,12,18 * * ?` = 08:00, 12:00, 18:00 |
| `/` | 間隔 | `0 */15 * * * ?` = 每 15 分鐘 |

**驗證規則：**
- Cron 表達式不可為空
- 更新 Cron 表達式時應驗證格式合法性（可使用 `CronExpression.isValidExpression()` 驗證）
- 不允許秒級排程（`*/1 * * * * ?`）以防止資源過度消耗

### 4.3 啟停控制

**狀態轉換：**

```
┌──────────────────────────────────────────┐
│        ScheduledJobConfig 狀態            │
│                                          │
│   ┌──────────┐          ┌──────────┐    │
│   │ enabled  │ ──────→  │ disabled │    │
│   │ = true   │ disable()│ = false  │    │
│   │          │ ←──────  │          │    │
│   └──────────┘ enable() └──────────┘    │
└──────────────────────────────────────────┘
```

**啟停規則：**

1. **enable(operator)** — 啟用排程，設定 `enabled = true`，記錄操作者與時間
2. **disable(operator)** — 停用排程，設定 `enabled = false`，記錄操作者與時間
3. 停用排程不會中斷正在執行的任務，僅阻止下次排程觸發
4. 排程執行器（Scheduler）在觸發前應查詢 `enabled` 狀態，若為 `false` 則跳過

### 4.4 執行狀態追蹤

排程任務的執行狀態透過以下欄位追蹤：

| 欄位 | 型別 | 說明 |
|:---|:---|:---|
| `lastExecutedAt` | TIMESTAMP | 最近一次執行時間 |
| `lastExecutionStatus` | VARCHAR(20) | 最近執行狀態：`SUCCESS` / `FAILED` / `RUNNING` |
| `lastErrorMessage` | VARCHAR(2000) | 最近一次錯誤訊息（成功時清空） |
| `consecutiveFailures` | INTEGER | 連續失敗次數 |

**狀態轉換邏輯：**

```
                    recordStart()
                 ┌──────────────────┐
                 │                  │
                 ▼                  │
           ┌──────────┐            │
     ┌────→│ RUNNING  │────────────┘
     │     └──────────┘
     │       │       │
     │       │       │ recordFailure(msg)
     │       │       │
     │       │       ▼
     │       │  ┌──────────┐
     │       │  │  FAILED  │ consecutiveFailures++
     │       │  └──────────┘
     │       │
     │       │ recordSuccess()
     │       ▼
     │  ┌──────────┐
     └──│ SUCCESS  │ consecutiveFailures = 0
        └──────────┘
```

**方法說明：**

| 方法 | 行為 |
|:---|:---|
| `recordStart()` | 設定 `lastExecutedAt = now()`，`lastExecutionStatus = "RUNNING"` |
| `recordSuccess()` | 設定 `lastExecutedAt = now()`，`lastExecutionStatus = "SUCCESS"`，清除 `lastErrorMessage`，重置 `consecutiveFailures = 0` |
| `recordFailure(errorMessage)` | 設定 `lastExecutedAt = now()`，`lastExecutionStatus = "FAILED"`，記錄 `lastErrorMessage`，累加 `consecutiveFailures++` |
| `updateCron(newCron, operator)` | 更新 `cronExpression`，記錄 `updatedAt` / `updatedBy` |

### 4.5 連續失敗告警機制

```java
/**
 * 是否需要告警（連續失敗 >= 3 次）
 */
public boolean needsAlert() {
    return this.consecutiveFailures >= 3;
}
```

**告警規則：**

1. 每次 `recordFailure()` 後，檢查 `needsAlert()`
2. 若回傳 `true`，發布 `ScheduledJobAlertEvent` 至通知服務（HR12）
3. 告警內容應包含：任務代碼、任務名稱、連續失敗次數、最近錯誤訊息
4. 告警接收人為具 ADMIN 角色的使用者
5. `recordSuccess()` 會重置 `consecutiveFailures`，解除告警狀態

### 4.6 前端排程管理 Tab 顯示規則

排程管理 Tab（ScheduledJobTab）的前端顯示邏輯與後端 Domain Model 欄位對應如下：

**「需關注」標籤顯示規則：**

| 條件 | 顯示 | 說明 |
|:---|:---|:---|
| `consecutiveFailures > 0` | `Tag color="warning"` 顯示「需關注」 | 任何連續失敗皆標示警告 |
| `consecutiveFailures == 0` | 不顯示標籤 | 無連續失敗時隱藏 |

> **備註：** 此處前端的「需關注」標籤門檻（`> 0`）與後端 `needsAlert()` 的告警門檻（`>= 3`）不同。前端標籤用於視覺提醒管理員留意，後端告警用於觸發 `ScheduledJobAlertEvent` 通知。

**「查看錯誤」標籤顯示規則：**

| 條件 | 顯示 | 說明 |
|:---|:---|:---|
| `lastErrorMessage` 存在且非空 | `Tag` 顯示「查看錯誤」，可點擊 | 點擊後開啟 Modal 顯示錯誤詳情 |
| `lastErrorMessage` 為 null 或空 | 不顯示標籤 | 無錯誤時隱藏 |

**操作欄位（啟用/停用 Toggle）規則：**

| 操作 | 對應 API | 前端行為 |
|:---|:---|:---|
| 啟用排程 | `PUT /api/v1/admin/scheduled-jobs/{code}/enable` | Popconfirm 確認後執行，成功後重新載入列表 |
| 停用排程 | `PUT /api/v1/admin/scheduled-jobs/{code}/disable` | Popconfirm 確認後執行，成功後重新載入列表 |

### 4.7 資料表結構

```sql
CREATE TABLE IF NOT EXISTS scheduled_job_configs (
    id                      VARCHAR(36)   PRIMARY KEY,
    job_code                VARCHAR(100)  NOT NULL,       -- 任務代碼
    job_name                VARCHAR(200)  NOT NULL,       -- 任務名稱
    module                  VARCHAR(20)   NOT NULL,       -- 所屬模組
    cron_expression         VARCHAR(50)   NOT NULL,       -- Cron 表達式
    enabled                 BOOLEAN DEFAULT TRUE,         -- 是否啟用
    description             VARCHAR(500),                 -- 說明
    last_executed_at        TIMESTAMP,                    -- 最近執行時間
    last_execution_status   VARCHAR(20),                  -- 最近執行狀態
    last_error_message      VARCHAR(2000),                -- 最近錯誤訊息
    consecutive_failures    INTEGER DEFAULT 0,            -- 連續失敗次數
    tenant_id               VARCHAR(36),                  -- 多租戶隔離
    updated_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by              VARCHAR(50),
    created_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (job_code, tenant_id)
);

CREATE INDEX idx_job_config_code   ON scheduled_job_configs(job_code);
CREATE INDEX idx_job_config_module ON scheduled_job_configs(module);
```

---

## 5. 系統日誌與審計

### 5.1 審計範圍

系統管理模組的所有 **寫入操作** 皆需留下審計軌跡：

| 操作 | 審計方式 | 說明 |
|:---|:---|:---|
| 功能開關切換 | `FeatureToggleToggledEvent` | 記錄開關代碼、變更前後狀態、操作者 |
| 系統參數更新 | `parameter_change_logs` 表 | 記錄參數代碼、舊值、新值、操作者、時間 |
| 系統參數重設 | `parameter_change_logs` 表 | 同上，新值為 `defaultValue` |
| 排程啟停 | `ScheduledJobConfig.updatedBy/updatedAt` | 記錄操作者與時間 |
| 排程 Cron 更新 | `ScheduledJobConfig.updatedBy/updatedAt` | 記錄操作者與時間 |

### 5.2 參數異動記錄（parameter_change_logs）

這是系統參數的專用審計表，與通用的 `audit_logs` 表互補：

**設計原則：**
- **僅允許 INSERT**，不可 UPDATE 或 DELETE
- 建議使用 Database Trigger 保護，防止誤刪
- 加密參數的 `old_value` / `new_value` 儲存遮罩值 `****`
- 查詢時支援依 `param_code` 與時間範圍篩選

**記錄結構：**

```json
{
  "id": 1,
  "paramCode": "MAX_FAILED_LOGIN_ATTEMPTS",
  "oldValue": "5",
  "newValue": "3",
  "operator": "admin",
  "changedAt": "2026-03-05T10:30:00"
}
```

### 5.3 排程執行日誌

排程任務的執行記錄直接更新至 `scheduled_job_configs` 表（`lastExecutedAt`、`lastExecutionStatus`、`lastErrorMessage`）。若需要完整的歷史執行記錄，可擴充為獨立的 `scheduled_job_execution_logs` 表（目前尚未實作）。

---

## 6. Domain Model

### 6.1 Aggregate Root 總覽

```
com.company.hrms.iam.domain.model.aggregate
├── FeatureToggle          — 功能開關聚合根
├── SystemParameter        — 系統參數聚合根
│   └── ParameterChange    — 參數異動記錄（內嵌 Value Object）
└── ScheduledJobConfig     — 排程任務配置聚合根
```

### 6.2 FeatureToggle（功能開關）

**Package:** `com.company.hrms.iam.domain.model.aggregate`

| 欄位 | 型別 | 說明 |
|:---|:---|:---|
| `id` | String | 主鍵（UUID 格式） |
| `featureCode` | String | 功能代碼（如 `LATE_CHECK`），唯一識別 |
| `featureName` | String | 功能名稱（顯示用） |
| `module` | String | 所屬模組代碼（`HR01`~`HR14`） |
| `enabled` | boolean | 是否啟用 |
| `description` | String | 功能說明 |
| `tenantId` | String | 租戶 ID |
| `updatedAt` | LocalDateTime | 最後異動時間 |
| `updatedBy` | String | 最後異動者 |

**方法：**

| 方法 | 簽章 | 說明 |
|:---|:---|:---|
| `enable` | `void enable(String operator)` | 啟用功能，更新時間與操作者 |
| `disable` | `void disable(String operator)` | 停用功能，更新時間與操作者 |
| `toggle` | `void toggle(String operator)` | 反轉狀態，更新時間與操作者 |

### 6.3 SystemParameter（系統參數）

**Package:** `com.company.hrms.iam.domain.model.aggregate`

| 欄位 | 型別 | 說明 |
|:---|:---|:---|
| `id` | String | 主鍵（UUID 格式） |
| `paramCode` | String | 參數代碼（如 `MAX_FAILED_LOGIN_ATTEMPTS`），唯一識別 |
| `paramName` | String | 參數名稱（顯示用） |
| `paramValue` | String | 參數值（字串格式，由業務層轉型） |
| `paramType` | String | 參數型別（`STRING`/`INTEGER`/`DECIMAL`/`BOOLEAN`/`JSON`） |
| `module` | String | 所屬模組代碼（`GLOBAL` 或 `HR01`~`HR14`） |
| `category` | String | 參數分類（`SECURITY`/`BUSINESS`/`UI`/`SYSTEM`） |
| `description` | String | 參數說明 |
| `defaultValue` | String | 預設值 |
| `tenantId` | String | 租戶 ID |
| `isEncrypted` | boolean | 是否加密儲存 |
| `updatedAt` | LocalDateTime | 最後異動時間 |
| `updatedBy` | String | 最後異動者 |

**方法：**

| 方法 | 簽章 | 說明 |
|:---|:---|:---|
| `updateValue` | `ParameterChange updateValue(String newValue, String operator)` | 更新參數值，回傳異動記錄 |
| `resetToDefault` | `ParameterChange resetToDefault(String operator)` | 重設為預設值，回傳異動記錄 |
| `getIntValue` | `int getIntValue()` | 取得整數值（`Integer.parseInt`） |
| `getBoolValue` | `boolean getBoolValue()` | 取得布林值（`Boolean.parseBoolean`） |

**內嵌 Value Object — ParameterChange：**

| 欄位 | 型別 | 說明 |
|:---|:---|:---|
| `paramCode` | String | 參數代碼 |
| `oldValue` | String | 舊值 |
| `newValue` | String | 新值 |
| `operator` | String | 操作者 |
| `changedAt` | LocalDateTime | 異動時間 |

### 6.4 ScheduledJobConfig（排程任務配置）

**Package:** `com.company.hrms.iam.domain.model.aggregate`

| 欄位 | 型別 | 說明 |
|:---|:---|:---|
| `id` | String | 主鍵（UUID 格式） |
| `jobCode` | String | 任務代碼（如 `ABSENT_DETECTION`），唯一識別 |
| `jobName` | String | 任務名稱（顯示用） |
| `module` | String | 所屬模組代碼 |
| `cronExpression` | String | Cron 表達式 |
| `enabled` | boolean | 是否啟用 |
| `description` | String | 任務說明 |
| `lastExecutedAt` | LocalDateTime | 最近執行時間 |
| `lastExecutionStatus` | String | 最近執行狀態（`SUCCESS`/`FAILED`/`RUNNING`） |
| `lastErrorMessage` | String | 最近錯誤訊息 |
| `consecutiveFailures` | int | 連續失敗次數 |
| `tenantId` | String | 租戶 ID |
| `updatedAt` | LocalDateTime | 最後異動時間 |
| `updatedBy` | String | 最後異動者 |

**方法：**

| 方法 | 簽章 | 說明 |
|:---|:---|:---|
| `enable` | `void enable(String operator)` | 啟用排程 |
| `disable` | `void disable(String operator)` | 停用排程 |
| `recordStart` | `void recordStart()` | 記錄開始執行 |
| `recordSuccess` | `void recordSuccess()` | 記錄執行成功，重置失敗計數 |
| `recordFailure` | `void recordFailure(String errorMessage)` | 記錄執行失敗，累加失敗計數 |
| `updateCron` | `void updateCron(String newCron, String operator)` | 更新 Cron 表達式 |
| `needsAlert` | `boolean needsAlert()` | 判斷是否需要告警（`consecutiveFailures >= 3`） |

---

## 7. 業務規則與驗證邏輯

### 7.1 功能開關驗證規則

| 規則編號 | 規則描述 | 驗證時機 |
|:---|:---|:---|
| FT-V001 | `featureCode` 不可為空且不可重複（同一 tenant） | 建立時 |
| FT-V002 | `module` 必須為合法的模組代碼（`HR01`~`HR14`） | 建立/更新時 |
| FT-V003 | `operator` 不可為空 | 啟用/停用/切換時 |
| FT-V004 | 開關不存在時回傳 404 | 更新時 |

### 7.2 系統參數驗證規則

| 規則編號 | 規則描述 | 驗證時機 |
|:---|:---|:---|
| SP-V001 | `paramCode` 不可為空且不可重複（同一 tenant） | 建立時 |
| SP-V002 | `paramType` 必須為合法型別（`STRING`/`INTEGER`/`DECIMAL`/`BOOLEAN`/`JSON`） | 建立/更新時 |
| SP-V003 | `category` 必須為合法分類（`SECURITY`/`BUSINESS`/`UI`/`SYSTEM`） | 建立/更新時 |
| SP-V004 | `newValue` 需符合 `paramType` 的型別約束（如 INTEGER 型別的值必須可轉為整數） | 更新時 |
| SP-V005 | 加密參數（`isEncrypted = true`）的值在寫入前必須加密 | 更新時 |
| SP-V006 | 參數不存在時回傳 404 | 更新/重設時 |
| SP-V007 | `module` 必須為 `GLOBAL` 或合法模組代碼 | 建立時 |

### 7.3 排程任務驗證規則

| 規則編號 | 規則描述 | 驗證時機 |
|:---|:---|:---|
| SJ-V001 | `jobCode` 不可為空且不可重複（同一 tenant） | 建立時 |
| SJ-V002 | `cronExpression` 不可為空且必須為合法的 Cron 表達式 | 建立/更新 Cron 時 |
| SJ-V003 | 不允許秒級排程（防止資源過度消耗） | 更新 Cron 時 |
| SJ-V004 | `operator` 不可為空 | 啟用/停用/更新 Cron 時 |
| SJ-V005 | 排程不存在時回傳 404 | 更新時 |
| SJ-V006 | 連續失敗次數 `consecutiveFailures` 不可為負數 | 系統內部驗證 |

---

## 8. Domain Event 列表

### 8.1 功能開關事件

| 事件名稱 | Topic | 觸發時機 | Payload 關鍵欄位 | 訂閱者與用途 |
|:---|:---|:---|:---|:---|
| `FeatureToggleToggledEvent` | `hr.iam.feature.toggled` | 開關狀態變更 | `featureCode`, `module`, `enabled`, `operatedBy`, `occurredAt` | **各相關服務：** 清除功能開關快取 |

**Payload 範例：**

```json
{
  "eventId": "evt-ft-001",
  "eventType": "FeatureToggleToggled",
  "occurredAt": "2026-03-05T10:00:00Z",
  "data": {
    "featureCode": "LATE_CHECK",
    "featureName": "遲到判定",
    "module": "HR03",
    "enabled": false,
    "previousEnabled": true,
    "operatedBy": "admin",
    "tenantId": "tenant-001"
  }
}
```

### 8.2 系統參數事件

| 事件名稱 | Topic | 觸發時機 | Payload 關鍵欄位 | 訂閱者與用途 |
|:---|:---|:---|:---|:---|
| `SystemParameterUpdatedEvent` | `hr.iam.parameter.updated` | 參數值變更 | `paramCode`, `module`, `category`, `oldValue`, `newValue`, `operatedBy` | **各相關服務：** 重載參數 / 清除快取 |

**Payload 範例：**

```json
{
  "eventId": "evt-sp-001",
  "eventType": "SystemParameterUpdated",
  "occurredAt": "2026-03-05T10:30:00Z",
  "data": {
    "paramCode": "MAX_FAILED_LOGIN_ATTEMPTS",
    "paramName": "登入失敗上限",
    "module": "HR01",
    "category": "SECURITY",
    "oldValue": "5",
    "newValue": "3",
    "operatedBy": "admin",
    "tenantId": "tenant-001"
  }
}
```

### 8.3 排程任務事件

| 事件名稱 | Topic | 觸發時機 | Payload 關鍵欄位 | 訂閱者與用途 |
|:---|:---|:---|:---|:---|
| `ScheduledJobConfigUpdatedEvent` | `hr.iam.job.config-updated` | 排程啟停或 Cron 變更 | `jobCode`, `module`, `enabled`, `cronExpression`, `operatedBy` | **Scheduler：** 重載排程設定 |
| `ScheduledJobAlertEvent` | `hr.iam.job.alert` | 連續失敗 >= 3 次 | `jobCode`, `jobName`, `consecutiveFailures`, `lastErrorMessage` | **Notification（HR12）：** 發送告警通知給 ADMIN |

**ScheduledJobAlertEvent Payload 範例：**

```json
{
  "eventId": "evt-sj-001",
  "eventType": "ScheduledJobAlert",
  "occurredAt": "2026-03-05T19:00:30Z",
  "data": {
    "jobCode": "ABSENT_DETECTION",
    "jobName": "曠職自動判定",
    "module": "HR03",
    "consecutiveFailures": 3,
    "lastErrorMessage": "Connection refused: HR03 Attendance Service unavailable",
    "lastExecutedAt": "2026-03-05T19:00:00Z",
    "tenantId": "tenant-001"
  }
}
```

---

## 9. 相關 API 端點對照

> **注意：** 以下 API 端點為規劃設計，尚未完整實作。Controller 與 Service 命名遵循 Service Factory 模式。

### 9.1 功能開關 API

| 方法 | 端點 | Controller | Service Bean | 說明 |
|:---|:---|:---|:---|:---|
| GET | `/api/v1/admin/feature-toggles` | `HR01SystemQryController.getFeatureToggles()` | `getFeatureTogglesServiceImpl` | 查詢全部功能開關 |
| GET | `/api/v1/admin/feature-toggles/{code}` | `HR01SystemQryController.getFeatureToggle()` | `getFeatureToggleServiceImpl` | 查詢單一功能開關 |
| PUT | `/api/v1/admin/feature-toggles/{code}/toggle` | `HR01SystemCmdController.toggleFeature()` | `toggleFeatureServiceImpl` | 切換功能開關 |
| PUT | `/api/v1/admin/feature-toggles/{code}/enable` | `HR01SystemCmdController.enableFeature()` | `enableFeatureServiceImpl` | 啟用功能 |
| PUT | `/api/v1/admin/feature-toggles/{code}/disable` | `HR01SystemCmdController.disableFeature()` | `disableFeatureServiceImpl` | 停用功能 |

### 9.2 系統參數 API

| 方法 | 端點 | Controller | Service Bean | 說明 |
|:---|:---|:---|:---|:---|
| GET | `/api/v1/admin/system-parameters` | `HR01SystemQryController.getSystemParameters()` | `getSystemParametersServiceImpl` | 查詢全部參數（支援 module/category 篩選） |
| GET | `/api/v1/admin/system-parameters/{code}` | `HR01SystemQryController.getSystemParameter()` | `getSystemParameterServiceImpl` | 查詢單一參數 |
| PUT | `/api/v1/admin/system-parameters/{code}` | `HR01SystemCmdController.updateSystemParameter()` | `updateSystemParameterServiceImpl` | 更新參數值 |
| PUT | `/api/v1/admin/system-parameters/{code}/reset` | `HR01SystemCmdController.resetSystemParameter()` | `resetSystemParameterServiceImpl` | 重設為預設值 |
| GET | `/api/v1/admin/system-parameters/{code}/history` | `HR01SystemQryController.getParameterHistory()` | `getParameterHistoryServiceImpl` | 查詢參數異動記錄 |

### 9.3 排程管理 API

| 方法 | 端點 | Controller | Service Bean | 說明 |
|:---|:---|:---|:---|:---|
| GET | `/api/v1/admin/scheduled-jobs` | `HR01SystemQryController.getScheduledJobs()` | `getScheduledJobsServiceImpl` | 查詢全部排程任務 |
| GET | `/api/v1/admin/scheduled-jobs/{code}` | `HR01SystemQryController.getScheduledJob()` | `getScheduledJobServiceImpl` | 查詢單一排程任務 |
| PUT | `/api/v1/admin/scheduled-jobs/{code}/enable` | `HR01SystemCmdController.enableScheduledJob()` | `enableScheduledJobServiceImpl` | 啟用排程 |
| PUT | `/api/v1/admin/scheduled-jobs/{code}/disable` | `HR01SystemCmdController.disableScheduledJob()` | `disableScheduledJobServiceImpl` | 停用排程 |
| PUT | `/api/v1/admin/scheduled-jobs/{code}/cron` | `HR01SystemCmdController.updateScheduledJobCron()` | `updateScheduledJobCronServiceImpl` | 更新 Cron 表達式 |
| POST | `/api/v1/admin/scheduled-jobs/{code}/trigger` | `HR01SystemCmdController.triggerScheduledJob()` | `triggerScheduledJobServiceImpl` | 手動觸發排程（立即執行一次） |

---

## 10. 合約測試對照

本模組的合約測試定義於 `contracts/iam_contracts.md` 的「系統管理模組」區段：

| 合約 ID | 場景名稱 | 對應 Domain 方法 |
|:---|:---|:---|
| `IAM_SYS_001` | 啟用/停用功能開關 | `FeatureToggle.toggle()` / `enable()` / `disable()` |
| `IAM_SYS_002` | 更新系統參數 | `SystemParameter.updateValue()` / `resetToDefault()` |
| `IAM_SYS_003` | 更新排程任務 Cron / 啟停排程 | `ScheduledJobConfig.updateCron()` / `enable()` / `disable()` |

---

**文件結束**
