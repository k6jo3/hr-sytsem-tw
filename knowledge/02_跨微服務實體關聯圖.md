# 核心實體關聯圖 (Global Entity Relationship Diagram)

本文件展示 HRMS 系統的跨服務邏輯實體關聯圖 (Logical ERD)。在微服務架構 (Microservices) 及「每服務一資料庫 (Database-per-service)」的設計下，不同模組之間**並不存在實體的外鍵約束 (No Physical Foreign Keys)**。跨模組的資料實體皆以 UUID 作為邏輯引用 (Logical Reference)，並透過事件驅動或 API Composition 進行資料聚合。

```mermaid
%%{init: {'theme': 'base', 'themeVariables': { 'fontSize': '16px', 'fontFamily': 'sans-serif', 'textColor': '#111111', 'lineColor': '#888888'}}}%%
erDiagram
    %% ==========================================
    %% ORG 模組: Organization (主檔)
    %% ==========================================
    EMPLOYEE {
        uuid employee_id PK "員工 ID (系統唯一)"
        string employee_no "員編"
        string full_name "全名"
        uuid department_id FK "所屬部門"
        string employment_status "在職狀態"
        date hire_date "到職日"
    }

    DEPARTMENT {
        uuid department_id PK "部門 ID"
        string department_code "部門代碼"
        string department_name "名稱"
        uuid parent_id FK "上層部門 (Self-Reference)"
        uuid manager_id FK "主管 ID (Logical: Employee)"
    }

    %% ==========================================
    %% IAM 模組: Identity & Access Management
    %% ==========================================
    USER_ACCOUNT {
        uuid user_id PK "帳號 ID"
        uuid employee_id FK "綁定之員工 ID (Logical: Employee)"
        string username "登入帳號"
        string password_hash "Hash 密碼"
        string account_status "帳號狀態"
    }

    ROLE {
        uuid role_id PK "角色 ID"
        string role_code "權限角色代碼"
    }

    %% ==========================================
    %% ATT 模組: Attendance
    %% ==========================================
    ATTENDANCE_RECORD {
        uuid record_id PK "打卡紀錄 ID"
        uuid employee_id FK "關聯員工 (Logical: Employee)"
        date work_date "考勤日期"
        datetime clock_in_time "上班打卡時間"
        datetime clock_out_time "下班打卡時間"
        string status "出勤狀態(正常/遲到/異常)"
    }
    
    LEAVE_REQUEST {
        uuid request_id PK "請假單 ID"
        uuid employee_id FK "關聯員工 (Logical: Employee)"
        string leave_type "假別"
        date start_date "開始日"
        date end_date "結束日"
        string approval_status "簽核狀態"
    }

    %% ==========================================
    %% PAY 模組: Payroll
    %% ==========================================
    PAYROLL_RUN {
        uuid run_id PK "結算批次 ID"
        string period_year_month "發薪年月 (e.g. 2026-02)"
        string status "結算狀態"
    }

    PAYSLIP {
        uuid payslip_id PK "薪資單 ID"
        uuid run_id FK "結算批次"
        uuid employee_id FK "關聯員工 (Logical: Employee)"
        decimal base_salary "本薪"
        decimal overtime_pay "加班費加總"
        decimal deductions "勞健保扣款"
        decimal net_pay "實發金額"
    }

    %% ==========================================
    %% 關聯線定義 (Relationships)
    %% 第一部分：同模組內部的強關聯 (由實體外鍵維護)
    %% ==========================================
    DEPARTMENT ||--o{ EMPLOYEE : "僱用 (has)"
    DEPARTMENT ||--o{ DEPARTMENT : "階層 (parent-child)"
    PAYROLL_RUN ||--o{ PAYSLIP : "包含 (contains)"
    USER_ACCOUNT }o--o{ ROLE : "分配 (many-to-many)"
    
    %% ==========================================
    %% 第二部分：跨模組邏輯關聯 (虛線表示無實體 FK)
    %% ==========================================
    EMPLOYEE ||..o| USER_ACCOUNT : "登入綁定 (Logical)"
    EMPLOYEE ||..o{ ATTENDANCE_RECORD : "打卡產生 (Logical)"
    EMPLOYEE ||..o{ LEAVE_REQUEST : "發起請假 (Logical)"
    EMPLOYEE ||..o{ PAYSLIP : "領取薪資 (Logical)"
    
    %% 說明註記
    %% "||--o{" : 一對多 (One-to-Many)
    %% "||--o|" : 一對零或一 (One-to-Zero-or-One)
    %% "||..o{" : (虛線)跨微服務一對多
```

### 關聯設計要點 (Schema Design Principles)

本系統的資料架構具備以下三個高可用性與資安防護的設計特徵：

1. **跨微服務無實體外鍵（No Physical Foreign Keys）**
   在傳統單體 (Monolith) 系統中，跨業務模組常倚賴實體關聯 (如：`Payroll.employee_id` 指向 `Employee` 表)。在本系統微服務架構中，薪資模組 (Payroll) 與人事模組 (Organization) 具備獨立的 PostgreSQL Schema，改以邏輯外鍵 (Logical Reference) 進行弱關聯 (如圖上虛線所示)。此舉能徹底隔離單一模組資料庫異常所引發的死鎖 (Deadlock) 與系統級存取失效。

2. **跨模組資料聚合策略 (Data Aggregation & API Composition)**
   由於跨庫無法執行 SQL JOIN 語句，當前端要求複合性檢視表 (如：包含員工姓名與部門名稱的打卡列表) 時，系統採取 **CQRS 加上 API Composition** 的架構設計。在主領域模型撈取分頁資料後，利用 Application Service 發出批次查詢 (Batch Query) 獲取來自關聯模組的 Snapshot；針對高頻讀取的情境，則利用 Kafka 訂閱關聯實體的異動事件，於本地端維護讀取專用的快取模型 (Read-Model)。

3. **採用 UUID 構築無狀態主鍵設計**
   所有微服務之領域實體皆強制採用 `UUID V4` 取代傳統的 Auto Increment ID。此策略除避免在跨微服務水平擴展時發生 ID 碰撞外，更天然地阻絕了 **ID 猜測攻擊 (BOLA: Broken Object Level Authorization)**，使得資料探勘與外洩風險降至最低。
