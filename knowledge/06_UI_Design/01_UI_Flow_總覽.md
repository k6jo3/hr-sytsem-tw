# UI Flow 總覽

**文件編號：** UI-01
**版本：** 1.0
**更新日期：** 2026-03-02
**適用範圍：** HRMS 全系統 14 個服務模組的用戶操作路徑

---

## 目錄

1. [系統入口與導航總覽](#1-系統入口與導航總覽)
2. [角色-功能可見性矩陣](#2-角色-功能可見性矩陣)
3. [各模組 UI Flow](#3-各模組-ui-flow)
4. [跨模組流程](#4-跨模組流程)

---

## 1. 系統入口與導航總覽

### 1.1 系統入口流程

```plantuml
@startuml
!theme plain
skinparam ActivityBackgroundColor #f8f9fa
skinparam ActivityBorderColor #667eea

start
:使用者存取系統;

if (已登入？) then (是)
  :進入主頁面 (PageLayout);
else (否)
  :重導至登入頁 (/login);

  fork
    :帳密登入;
  fork again
    :Google SSO;
  fork again
    :Azure SSO;
  end fork

  if (驗證成功？) then (是)
    :儲存 Token 至 LocalStorage;
    :更新 Redux auth 狀態;
    :導向 /admin/reports (報表中心);
  else (否)
    if (連續失敗 5 次？) then (是)
      :帳號鎖定 30 分鐘;
    else (否)
      :顯示錯誤訊息;
    endif
    stop
  endif
endif

:依角色顯示側邊導航選單;

fork
  :管理功能 (/admin/*);
fork again
  :個人功能 (/profile/*);
fork again
  :考勤功能 (/attendance/*);
end fork

stop
@enduml
```

### 1.2 角色導向首頁

| 角色 | 預設首頁 | 可見選單 |
|:---|:---|:---|
| ADMIN | `/admin/reports` | 全部功能 |
| HR | `/admin/reports` | 除系統設定外全部 |
| MANAGER | `/admin/reports` | 團隊管理 + 審核功能 |
| PM | `/admin/projects` | 專案管理 + 工時管理 |
| FINANCE | `/admin/payroll/runs` | 薪資 + 保險 + 報表 |
| EMPLOYEE | `/attendance/check-in` | 個人功能 (ESS) |

---

## 2. 角色-功能可見性矩陣

| 頁面代碼 | 頁面名稱 | ADMIN | HR | MANAGER | PM | FINANCE | EMPLOYEE |
|:---|:---|:---:|:---:|:---:|:---:|:---:|:---:|
| **HR01 — IAM** |
| HR01-P01 | 登入頁 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| HR01-P02 | 使用者管理 | ✅ | — | — | — | — | — |
| HR01-P03 | 角色權限分配 | ✅ | — | — | — | — | — |
| HR01-P04 | 修改密碼 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **HR02 — 組織員工** |
| HR02-P01 | 部門與編制 | ✅ | ✅ | — | — | — | — |
| HR02-P02 | 員工列表 | ✅ | ✅ | — | — | — | — |
| HR02-P03 | 員工詳情 | ✅ | ✅ | — | — | — | — |
| **HR03 — 考勤管理** |
| HR03-P01 | 每日打卡 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| HR03-P02 | 請假加班申請 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| HR03-P03 | 我的考勤日誌 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| HR03-P04 | 考勤例外審核 | ✅ | ✅ | ✅ | — | — | — |
| HR03-P07 | 班制管理 | ✅ | ✅ | — | — | — | — |
| HR03-P08 | 假期類型設定 | ✅ | ✅ | — | — | — | — |
| HR03-P09 | 考勤報告 | ✅ | ✅ | ✅ | — | — | — |
| HR03-P10 | 月結審核 | ✅ | ✅ | — | — | — | — |
| **HR04 — 薪資核算** |
| HR04-P01 | 計薪作業中心 | ✅ | ✅ | — | — | — | — |
| HR04-P03 | 我的電子薪資單 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| HR04-P06 | 計薪審核 | ✅ | — | — | — | ✅ | — |
| **HR05 — 保險管理** |
| HR05-P01 | 勞健保加退保 | ✅ | ✅ | — | — | — | — |
| HR05-P02 | 保費試算工具 | ✅ | ✅ | — | — | — | — |
| HR05-P03 | 我的保險資訊 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **HR06 — 專案管理** |
| HR06-P02 | 專案列表 | ✅ | — | — | ✅ | — | — |
| HR06-P03 | 專案詳情 | ✅ | — | — | ✅ | — | — |
| HR06-P04 | 專案編輯 | ✅ | — | — | ✅ | — | — |
| HR06-P05 | 專案任務 | ✅ | — | — | ✅ | — | — |
| **HR07 — 工時申報** |
| HR07-P01 | 每週工時報表 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| HR07-P02 | 工時審核看板 | ✅ | — | — | ✅ | — | — |
| HR07-P03 | 工時報告 | ✅ | ✅ | — | ✅ | — | — |
| **HR08 — 績效考核** |
| HR08-P01 | 考核週期管理 | ✅ | ✅ | — | — | — | — |
| HR08-P03 | 我的評核表 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| HR08-P04 | 團隊績效 | ✅ | — | ✅ | — | — | — |
| **HR09~14 — 支援模組** |
| HR09-P01 | 招募管理 | ✅ | ✅ | — | — | — | — |
| HR10-P01 | 教育訓練 | ✅ | ✅ | — | — | — | — |
| HR11-P01 | 簽核流程 | ✅ | ✅ | — | — | — | — |
| HR12-P01 | 訊息通知 | ✅ | ✅ | — | — | — | — |
| HR13-P01 | 文件管理 | ✅ | ✅ | — | — | — | — |
| HR14-P01 | 報表中心 | ✅ | ✅ | ✅ | ✅ | ✅ | — |

---

## 3. 各模組 UI Flow

### 3.1 HR01 IAM — 身分認證流程

```plantuml
@startuml
!theme plain
title HR01 IAM - 身分認證與權限管理

start
:存取系統;

partition "登入流程" {
  :顯示登入頁 (HR01-P01);
  :輸入帳號密碼;
  :POST /api/v1/auth/login;

  if (驗證成功？) then (是)
    :Token 存入 LocalStorage;
  else (否)
    :顯示錯誤;
    stop
  endif
}

partition "使用者管理" {
  :進入使用者管理 (HR01-P02);
  :搜尋/篩選使用者;

  fork
    :新增使用者 (HR01-M01);
    :POST /api/v1/users;
  fork again
    :編輯使用者 (HR01-M01);
    :PUT /api/v1/users/{id};
  fork again
    :停用/啟用使用者;
  end fork
}

partition "角色權限管理" {
  :進入角色管理 (HR01-P03);
  :選擇角色;
  :配置權限樹;
  :PUT /api/v1/roles/{id}/permissions;
}

stop
@enduml
```

### 3.2 HR02 組織員工 — 員工生命週期

```plantuml
@startuml
!theme plain
title HR02 組織員工 - 員工生命週期

start

partition "到職流程" {
  :進入員工列表 (HR02-P02);
  :點擊新增員工;
  :步驟式表單;
  note right: 基本資料 → 職務 → 銀行
  :POST /api/v1/employees;
  :發布 EmployeeCreatedEvent;

  fork
    :IAM 自動建立帳號;
  fork again
    :Insurance 產生加保提醒;
  end fork
}

partition "在職管理" {
  :查看員工詳情 (HR02-P03);

  fork
    :基本資訊 Tab;
  fork again
    :職務資訊 Tab;
  fork again
    :學經歷 Tab;
  fork again
    :合約資料 Tab;
  fork again
    :人事歷程 Tab;
  end fork
}

partition "離職流程" {
  :操作「辦理離職」;
  :Modal.confirm 確認;
  :POST /api/v1/employees/{id}/terminate;
  :發布 EmployeeTerminatedEvent;

  fork
    :IAM 停用帳號;
  fork again
    :Attendance 計算未休假;
  fork again
    :Payroll 結算薪資;
  fork again
    :Insurance 辦理退保;
  end fork
}

stop
@enduml
```

### 3.3 HR03 考勤管理 — 請假申請流程

```plantuml
@startuml
!theme plain
title HR03 考勤管理 - 請假申請流程

|員工|
start
:每日打卡 (HR03-P01);
:查看考勤日誌 (HR03-P03);

:進入請假申請 (HR03-P02);
:選擇假別;
:查詢假期餘額;
note right: GET /api/v1/leave/balances/{type}

if (餘額充足？) then (是)
  :填寫請假日期與原因;
  :POST /api/v1/leave/applications;
else (否)
  :顯示餘額不足提示;
  stop
endif

|主管|
:收到審核通知;
:進入審核頁 (HR03-P04);
:查看申請詳情;

if (核准？) then (是)
  :PUT .../approve;
  :發布 LeaveApprovedEvent;
else (否)
  :輸入駁回原因;
  :PUT .../reject;
endif

|員工|
:收到審核結果通知;

if (核准) then (是)
  :假期餘額自動扣除;
else (駁回)
  :可複製重新申請;
endif

stop
@enduml
```

### 3.4 HR04 薪資管理 — 計薪流程

```plantuml
@startuml
!theme plain
title HR04 薪資管理 - 計薪流程 (Saga Pattern)

|HR|
start
:進入計薪作業中心 (HR04-P01);
:建立薪資批次;
note right: POST /api/v1/payroll-runs\n狀態: DRAFT

:執行薪資計算;
note right: POST .../execute\n回應: 202 Accepted

|系統 (Saga)|
:Step 1: 查詢員工清單;
note right: Organization Service
:Step 2: 取得差勤資料;
note right: Attendance Service
:Step 3: 取得保費資料;
note right: Insurance Service
:Step 4: 計算薪資;
note right
  底薪 + 加班費 + 津貼
  - 勞保 - 健保 - 勞退
  - 所得稅 = 實發金額
end note
:Step 5: 儲存薪資單;
:狀態: COMPLETED;

|HR|
:檢視批次詳情 (HR04-P02);
:確認明細無誤;
:送審;
note right: 狀態: SUBMITTED

|Finance|
:進入計薪審核 (HR04-P06);
if (核准？) then (是)
  :PUT .../approve;
  :狀態: APPROVED;
  :產生銀行轉帳檔 (HR04-P07);
  :發布 PayrollRunCompletedEvent;

  fork
    :Timesheet 鎖定已核准工時;
  fork again
    :Notification 通知員工查閱;
  fork again
    :Report 更新月度統計;
  end fork
else (否)
  :輸入駁回原因;
  :狀態: 退回 COMPLETED;
endif

|員工|
:查看薪資單 (HR04-P03);
:下載 PDF;
note right: 密碼: 身分證末4碼

stop
@enduml
```

### 3.5 HR06/07 專案與工時 — 成本追蹤流程

```plantuml
@startuml
!theme plain
title HR06/07 專案與工時 - 成本追蹤閉迴路

|PM|
start
:建立專案 (HR06-P04);
:指派成員與工項 (HR06-P05);
:設定預算;

|員工|
:填報每週工時 (HR07-P01);
:選擇專案/任務;
:填入每日工時;
:送審;

|PM|
:工時審核看板 (HR07-P02);
if (核准？) then (是)
  :PUT .../approve;
  :發布 TimesheetApprovedEvent;
else (否)
  :駁回（含原因）;
  stop
endif

|系統|
:接收 TimesheetApprovedEvent;
:查詢員工時薪;
:計算成本 = hours × hourlyRate;
:更新 Project.actualCost;

if (實際成本 > 預算 80%？) then (是)
  :發布 ProjectBudgetAlertEvent;
  :通知 PM 預算預警;
endif

|PM|
:查看專案成本分析 (HR06-P03);
:查看工時報告 (HR07-P03);
:查看專案報表 (HR14-P03);

stop
@enduml
```

### 3.6 HR08 績效考核流程

```plantuml
@startuml
!theme plain
title HR08 績效考核流程

|HR|
start
:建立考核週期 (HR08-P01);
:設計考核表單 (HR08-P02);
note right: 設定項目、權重(=100%)、評分規則
:啟動考核週期;
:發布 CycleActivatedEvent;

|員工|
:收到自評通知;
:進入我的評核表 (HR08-P03);
:填寫自評分數與說明;
:送出自評;
note right: PUT .../submit-self

|主管|
:收到評核通知;
:進入團隊績效 (HR08-P04);
:評核下屬績效;
:送出主管評核;
note right: PUT .../submit-manager

|HR|
:確認所有評核完成;
:發布績效評等;
:查看績效報告 (HR08-P05);

|系統|
:發布 PerformanceCompletedEvent;
:Payroll 訂閱進行薪資調整;

stop
@enduml
```

### 3.7 HR09 招募管理流程

```plantuml
@startuml
!theme plain
title HR09 招募管理 - Kanban 流程

|HR|
start
:建立職缺;
:收到應徵者投遞;

partition "看板流程 (HR09-P01)" {
  :履歷篩選 (SCREENING);
  note right: 拖曳卡片至下一欄

  if (通過篩選？) then (是)
    :面試中 (INTERVIEWING);
    :安排面試;
    note right: 發布 InterviewScheduledEvent

    if (面試通過？) then (是)
      :Offer (OFFER);
      :發送 Offer;

      if (接受 Offer？) then (是)
        :已錄取 (HIRED);
        :發布 CandidateHiredEvent;
        :Organization 自動建立員工;
      else (否)
        :流程結束;
        stop
      endif
    else (否)
      :流程結束;
      stop
    endif
  else (否)
    :流程結束;
    stop
  endif
}

stop
@enduml
```

### 3.8 HR05 保險管理流程

```plantuml
@startuml
!theme plain
title HR05 保險管理 - 自動加保流程

start

partition "事件驅動加保" {
  :接收 EmployeeCreatedEvent;
  :查詢投保級距表;
  :自動建立三筆記錄;
  note right
    1. 勞工保險
    2. 全民健保
    3. 勞工退休金
  end note
  :發布 InsuranceEnrollmentCompleted;
}

partition "HR 手動操作" {
  :進入勞健保管理 (HR05-P01);

  fork
    :手動加保 (HR05-M01);
  fork again
    :級距調整 (HR05-M02);
    :發布 InsuranceLevelAdjusted;
  fork again
    :保費試算 (HR05-P02);
  fork again
    :產生申報檔;
  end fork
}

partition "員工自助" {
  :我的保險資訊 (HR05-P03);
  :查看投保狀態與歷程;
}

stop
@enduml
```

### 3.9 HR10~HR14 支援模組 UI Flow

```plantuml
@startuml
!theme plain
title HR10~HR14 支援模組 UI Flow

partition "HR10 教育訓練" {
  :課程管理 (HR10-P01);
  :員工報名 → 主管審核 → 出席 → 標記完成;
  :證照管理 → 到期提醒 (每週);
}

partition "HR11 簽核流程" {
  :流程定義管理 (HR11-P01);
  :可視化流程設計器 (HR11-P02);
  :我的待辦 → 核准/駁回;
  :我的申請 → 追蹤進度;
}

partition "HR12 通知服務" {
  :範本管理 (HR12-P01);
  :訂閱領域事件 → 發送通知;
  :渠道: IN_APP | EMAIL | PUSH | TEAMS | LINE;
  :WebSocket 即時推送;
}

partition "HR13 文件管理" {
  :文件上傳/下載 (HR13-P01);
  :版本管理;
  :範本 + 變數替換 → 自動產生 PDF;
  :薪資單 AES-256 加密;
}

partition "HR14 報表分析" {
  :KPI 儀表板 (HR14-P01);
  :CQRS ReadModel 事件驅動更新;
  :ECharts 圖表視覺化;
  :Excel/PDF 匯出;
}

@enduml
```

---

## 4. 跨模組流程

### 4.1 員工入職全流程

```plantuml
@startuml
!theme plain
title 跨模組流程 - 員工入職全流程

|HR02 組織員工|
start
:HR 新增員工 (HR02-P04);
:發布 EmployeeCreatedEvent;

|HR01 IAM|
:自動建立使用者帳號;
:指派預設角色 (EMPLOYEE);
:發送帳號啟用通知;

|HR05 保險管理|
:自動查詢投保級距;
:建立勞保/健保/勞退記錄;
:發布 InsuranceEnrollmentCompleted;

|HR04 薪資管理|
:建立薪資結構;
:關聯薪資項目;

|HR10 教育訓練|
:自動加入新人訓練課程;

|HR12 通知服務|
:發送歡迎通知;
:發送新人入職提醒給主管;

stop
@enduml
```

### 4.2 月底結算全流程

```plantuml
@startuml
!theme plain
title 跨模組流程 - 月底結算作業

|HR03 考勤管理|
start
:HR 執行月結 (HR03-P10);
:檢查異常考勤記錄;
:鎖定月份考勤資料;
:發布 MonthlyAttendanceClosedEvent;

|HR04 薪資管理|
:建立薪資批次 (HR04-P01);
:執行 Saga 計薪流程;
note right
  1. 取得員工清單
  2. 取得差勤資料
  3. 取得保費資料
  4. 計算薪資
  5. 儲存薪資單
end note
:Finance 審核 (HR04-P06);
:產生銀行轉帳檔 (HR04-P07);
:發布 PayrollRunCompletedEvent;

|HR07 工時管理|
:鎖定已核准工時 (status=LOCKED);

|HR14 報表分析|
:更新 monthly_hr_stats;
:刷新儀表板 KPI;

|HR12 通知服務|
:發送薪資單通知給全部員工;

stop
@enduml
```

### 4.3 專案成本追蹤全流程

```plantuml
@startuml
!theme plain
title 跨模組流程 - 專案成本追蹤

|HR06 專案管理|
start
:PM 建立專案;
:設定預算與成員;

|HR07 工時管理|
:員工填報工時;
:PM 審核工時;
:發布 TimesheetApprovedEvent;

|HR06 專案管理|
:計算成本 = hours × hourlyRate;
:更新 Project.actualCost;
:檢查預算使用率;

if (超過 80% 預算？) then (是)
  :發布 ProjectBudgetAlertEvent;

  |HR12 通知服務|
  :通知 PM 預算預警;
endif

|HR04 薪資管理|
:月薪計算包含專案工時成本;

|HR14 報表分析|
:更新 project_cost_snapshots;
:稼動率報表;
:專案獲利分析;

stop
@enduml
```

### 4.4 員工離職全流程

```plantuml
@startuml
!theme plain
title 跨模組流程 - 員工離職

|HR02 組織員工|
start
:HR 辦理離職 (HR02-P03);
:Modal.confirm 確認;
:POST .../terminate;
:發布 EmployeeTerminatedEvent;

|HR01 IAM|
:停用使用者帳號;

|HR03 考勤管理|
:計算未休特休天數;
:結算代金;

|HR05 保險管理|
:辦理退保 (勞保/健保/勞退);
:發布 InsuranceWithdrawCompleted;

|HR04 薪資管理|
:結算最後薪資;
:含未休假代金;
:產生最終薪資單;

|HR06 專案管理|
:移除專案成員;
:重新分配任務;

|HR14 報表分析|
:更新離職率統計;

stop
@enduml
```
