# HR08 績效管理系統 (Performance Management) - 開發狀態報告
**日期**: 2026-01-02

## 1. 專案概況
- **服務名稱**: `hrms-performance`
- **當前階段**: ✅ Phase 2 (Infrastructure 層) 完成，準備進入 Phase 3 (Application 層)
- **總體進度**: 約 40% (Domain/Infra 基礎建設完成，業務邏輯與 API 待實作)

## 2. 已完成項目 (Completed)

### 2.1 Domain 層 (核心業務邏輯)
- **Aggregates**: `PerformanceCycle` (考核週期), `PerformanceReview` (考核記錄)
- **Value Objects**: 
  - `CycleId`, `ReviewId`
  - `CycleType`, `CycleStatus`, `ReviewType`, `ReviewStatus`, `ScoringSystem`
  - `EvaluationItem`, `EvaluationTemplate`
- **Domain Services**:
  - `RatingCalculator` (評等計算)
  - `DistributionValidator` (強制分配驗證)
- **Domain Events**:
  - `PerformanceCycleStartedEvent`
  - `PerformanceReviewSubmittedEvent`
  - `PerformanceReviewCompletedEvent`
- **Unit Tests**: Domain 層測試覆蓋率 100% (21/21 測試通過)

### 2.2 Infrastructure 層 (持久化)
- **Entities**: `PerformanceCycleEntity`, `PerformanceReviewEntity`
- **Repositories**: 
  - `PerformanceCycleRepositoryImpl` (已重構繼承 `CommandBaseRepository`)
  - `PerformanceReviewRepositoryImpl` (已重構繼承 `CommandBaseRepository`)
  - 實作了 Entity <-> Domain 的轉換邏輯 (`toDomain`, `toEntity`)
  - 實作了 `reconstitute` 方法支援 Repository 重建 Aggregate

## 3. 待執行項目 (Remaining Tasks)

### 3.1 Phase 3: Application 層 (應用服務)
**Command Services (寫入邏輯):**
- [ ] `CreateCycleServiceImpl`: 建立考核週期
- [ ] `UpdateCycleServiceImpl`: 更新週期資訊
- [ ] `DeleteCycleServiceImpl`: 刪除週期 (草稿狀態)
- [ ] `StartCycleServiceImpl`: 啟動週期 (發布事件)
- [ ] `CompleteCycleServiceImpl`: 結束週期
- [ ] `SaveTemplateServiceImpl`: 儲存考核表單範本
- [ ] `PublishTemplateServiceImpl`: 發布表單範本
- [ ] `SubmitReviewServiceImpl`: 提交考核 (發布事件)
- [ ] `FinalizeReviewServiceImpl`: 確認考核結果 (發布事件)

**Query Services (讀取邏輯):**
- [ ] `GetCyclesServiceImpl`: 查詢週期列表
- [ ] `GetCycleDetailServiceImpl`: 查詢週期詳情
- [ ] `GetMyReviewsServiceImpl`: 查詢我的考核
- [ ] `GetTeamReviewsServiceImpl`: 查詢團隊考核
- [ ] `GetReviewDetailServiceImpl`: 查詢考核詳情
- [ ] `GetDistributionServiceImpl`: 查詢績效分布狀況
- [ ] `ExportReportServiceImpl`: 匯出報表

### 3.2 Phase 4: Interface 層 (API 介面)
**Controllers (REST API):**
- [ ] `HR08CycleCmdController`: 週期管理 (建立/更新/刪除/啟動/結束)
- [ ] `HR08CycleQryController`: 週期查詢
- [ ] `HR08TemplateCmdController`: 範本管理
- [ ] `HR08ReviewCmdController`: 考核執行 (提交/確認)
- [ ] `HR08ReviewQryController`: 考核查詢
- [ ] `HR08ReportQryController`: 報表查詢

**DTOs (Data Transfer Objects):**
- [ ] Request DTOs (e.g., `CreateCycleRequest`, `SubmitReviewRequest`)
- [ ] Response DTOs (e.g., `CycleDetailResponse`, `ReviewDetailResponse`)

### 3.3 Phase 5: 整合測試 & 驗證
- [ ] API 整合測試 (Controller -> Service -> Repository)
- [ ] Domain Event 發布與消費測試
- [ ] 端到端流程驗證 (建立週期 -> 啟動 -> 考核 -> 評分 -> 結束)

## 4. 下一步計畫 (Next Steps)
優先開始 **Application Layer** 的 **Command Services** 開發，依照 Use Case 順序：
1. `CreateCycleServiceImpl`
2. `SaveTemplateServiceImpl`
3. `PublishTemplateServiceImpl`
4. `StartCycleServiceImpl`
