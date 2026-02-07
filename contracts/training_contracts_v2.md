# 訓練發展服務業務合約 (Training Service Contract)

> **服務代碼:** HR10
> **版本:** 2.0
> **建立日期:** 2026-02-06

---

## 查詢操作合約

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| TRN_QRY_001 | 查詢可報名課程 | EMPLOYEE | `GET /api/v1/courses` | `{"status":"OPEN"}` | `status = 'OPEN'`, `enroll_end_date >= CURRENT_DATE` |
| TRN_QRY_002 | 查詢已結束課程 | HR | `GET /api/v1/courses` | `{"status":"COMPLETED"}` | `status = 'COMPLETED'` |
| TRN_QRY_003 | 員工查詢已報名課程 | EMPLOYEE | `GET /api/v1/enrollments/my` | `{}` | `employee_id = '{currentUserId}'` |
| TRN_QRY_004 | 查詢課程學員 | HR | `GET /api/v1/enrollments` | `{"courseId":"C001"}` | `course_id = 'C001'` |

---

## 命令操作合約

| 場景 ID | API 端點 | 必須檢查的業務規則 | 預期發布的事件 |
| :--- | :--- | :--- | :--- |
| TRN_CMD_001 | `POST /api/v1/courses` | 講師存在檢查 | `CourseCreated` |
| TRN_CMD_002 | `POST /api/v1/enrollments` | 名額檢查, 重複報名檢查 | `CourseEnrolled` |
| TRN_CMD_003 | `POST /api/v1/enrollments/{id}/complete` | 出席率檢查 | `CourseCompleted` |

---

## Domain Events

| 事件名稱 | 觸發時機 | 訂閱服務 |
|:---|:---|:---|
| `CourseCreated` | 建立課程 | Notification |
| `CourseEnrolled` | 員工報名 | Notification |
| `CourseCompleted` | 完成課程 | Organization (更新技能) |

---

**軟刪除:** 使用 `status` 欄位，不使用 `is_deleted`

**版本:** 2.0 | 2026-02-06
