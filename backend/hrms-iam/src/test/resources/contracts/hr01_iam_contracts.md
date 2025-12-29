# HR01 IAM 業務合約 (IAM Business Contracts)

## 1. 使用者管理 (User Management)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 預期結果 (Expected) |
| :--- | :--- | :--- | :--- | :--- |
| SC_USER_001 | 建立使用者 (正常) | ADMIN | `{"username":"newuser", "email":"new@example.com"}` | `Status=ACTIVE`, `Event=UserCreatedEvent` |
| SC_USER_002 | 更新使用者 (正常) | ADMIN | `{"displayName":"Updated Name"}` | `DisplayName=Updated Name`, `Event=UserUpdatedEvent` |
| SC_USER_003 | 停用使用者 (正常) | ADMIN | `N/A` | `Status=INACTIVE`, `Event=UserDeactivatedEvent` |
| SC_USER_004 | 刪除使用者 (正常) | ADMIN | `N/A` | `Deleted=True`, `Event=UserDeletedEvent` |
