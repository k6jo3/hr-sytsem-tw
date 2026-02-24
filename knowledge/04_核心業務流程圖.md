# 核心業務流程圖 (Business Flowcharts & Swimlane)

本文件展示系統中跨模組、高複雜度的核心業務邏輯流程。透過泳道圖 (Swimlane Diagram) 清晰界定出不同角色 (Actors) 以及後端微服務集群 (Microservices) 於各個階段應負擔的責任與系統邊界條件。

---

## 一、 考勤結算至薪資發放流程 (Attendance to Payroll Flow)

此流程涵蓋了企業人資系統中最關鍵的「月結處理」。它不僅牽涉前端使用者的互動，更涉及【考勤模組 (ATT)】與【薪資模組 (PAY)】之間的跨服務資料同步與非同步事件運算。

```mermaid
%%{init: {'theme': 'base', 'themeVariables': { 'fontSize': '16px', 'fontFamily': 'sans-serif', 'textColor': '#111111', 'lineColor': '#888888'}}}%%
sequenceDiagram
    autonumber
    
    actor Emp as 員工 (ESS)
    actor Mgr as 部門主管
    actor HR as 人資管理員
    participant ATT as [ATT] 考勤模組
    participant PAY as [PAY] 薪資模組
    participant INS as [INS] 保險模組

    %% 階段一：日常打卡與異常處理
    rect rgb(238, 242, 255)
        Note over Emp, ATT: Phase 1: 日常打卡與月底修正
        Emp->>ATT: 每日上下班打卡 (Clock In/Out)
        ATT->>ATT: 比對班表排程，標記異常 (遲到/早退/缺卡)
        
        loop 每月底前
            Emp->>ATT: 針對異常發起補卡/請假單
            Mgr->>ATT: 審核補卡/請假單 (Approve/Reject)
        end
    end

    %% 階段二：考勤凍結與結算
    rect rgb(255, 243, 224)
        Note over Mgr, ATT: Phase 2: 考勤資料凍結 (Attendance Lock)
        HR->>ATT: 發起「當月考勤結算」指令
        ATT->>ATT: 檢查是否仍有未簽核表單 (Pending Requests)
        alt 尚有未簽核表單
            ATT-->>HR: 阻擋結算，提示未完成簽核清單
        else 全部結算完成
            ATT->>ATT: 關班計算：總工時、加班總時數、請假扣薪時數
            ATT->>ATT: 鎖定考勤週期 (Locked)
            ATT->>PAY: Publish: `AttendancePeriodClosedEvent`
        end
    end

    %% 階段三：薪資計算引擎
    rect rgb(240, 253, 244)
        Note over HR, INS: Phase 3: 薪資結算與 SAGA 處理
        HR->>PAY: 執行本月計薪作業 (Run Payroll)
        PAY->>ATT: API Query: 取得當月扣薪時數與加班費基礎
        PAY->>INS: API Query: 取得當月勞健保、勞退自付額級距
        
        Note right of PAY: [計薪引擎運算]<br/>1. 底薪計算<br/>2. 加上: 獎金、加班費<br/>3. 扣除: 請假扣款、請假半薪<br/>4. 扣除: 勞健保自付額
        
        PAY->>PAY: 產出個人薪資初步試算表
        PAY-->>HR: 呈現試算單預覽 (Draft Version)
    end

    %% 階段四：撥薪媒體產出與發放
    rect rgb(253, 237, 237)
        Note over HR, PAY: Phase 4: 鎖定與發放
        HR->>PAY: 確認無誤，發送算定薪資 (Lock & Publish)
        PAY->>PAY: 生成銀行媒體格式檔 (TXT)
        PAY->>PAY: 生成 PDF 加密電子薪資單
        
        PAY->>Emp (ESS UI): 解鎖薪資單檢視權限
        Emp->>PAY: 輸入驗證密碼，查看電子薪資單
    end
```

### 流程設計要點 (Architecture Design Points)

此設計展示了複雜的分散式結算邏輯如何被穩健地處理：

1. **防呆與依賴檢查 (Constraint & Validation)**：在進入薪資計算（Phase 3）前，必須確保考勤資料已經完全鎖定（Phase 2）。系統防呆設計會阻擋在仍有請假單卡在主管端的狀態下進行結算，確保資料一致性 (Data Integrity)。
2. **事件與 API Query 的混合應用 (Hybrid Communication)**：
   * 考勤結班後會發出 Event 告知 PAY 模組「這段期間的資料已鎖定」。
   * 但計薪時，基於資料即時性且為了避免把龐大運算參數塞入 Kafka 訊息中，PAY 模組是主動呼叫 ATT 與 INS 的 Query API 索取精確的時數與級距數字（API Composition 模式）。此做法降低了 Queue 的負載並提高了資料獲取的正確性。
