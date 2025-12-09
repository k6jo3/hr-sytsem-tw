# HRMS IAM Service (HR01)

## 服務說明

Identity & Access Management (身份認證與授權) 服務

### 核心功能

- **身份認證** - JWT Token-based authentication
- **授權管理** - RBAC (Role-Based Access Control)
- **使用者管理** - User CRUD operations
- **角色管理** - Role & Permission management
- **SSO整合** - Google/Microsoft/SAML integration
- **多租戶** - Multi-tenancy support
- **會話管理** - Session & Token management

## 架構

採用 DDD (領域驅動設計) 四層架構：

```
com.company.hrms.iam/
├── api/                 # Interface Layer (介面層)
│   ├── controller/
│   │   ├── user/
│   │   │   ├── HR01UserCmdController.java
│   │   │   └── HR01UserQryController.java
│   │   └── role/
│   │       ├── HR01RoleCmdController.java
│   │       └── HR01RoleQryController.java
│   ├── request/
│   │   └── user/
│   │       ├── CreateUserRequest.java
│   │       └── UpdateUserRequest.java
│   └── response/
│       └── user/
│           ├── UserDetailResponse.java
│           └── UserListResponse.java
├── application/         # Application Layer (應用層)
│   └── service/
│       └── user/
│           ├── CreateUserServiceImpl.java
│           └── GetUserListServiceImpl.java
├── domain/              # Domain Layer (領域層)
│   ├── model/
│   │   ├── aggregate/
│   │   │   ├── User.java
│   │   │   └── Role.java
│   │   └── valueobject/
│   │       ├── Email.java
│   │       └── Password.java
│   ├── service/
│   │   └── PasswordEncryptionDomainService.java
│   ├── repository/
│   │   ├── IUserRepository.java
│   │   └── IRoleRepository.java
│   └── event/
│       ├── UserCreatedEvent.java
│       └── UserLoginEvent.java
└── infrastructure/      # Infrastructure Layer (基礎設施層)
    ├── repository/
    │   ├── UserRepositoryImpl.java
    │   └── RoleRepositoryImpl.java
    ├── dao/
    │   ├── UserDAO.java
    │   └── RoleDAO.java
    ├── po/
    │   ├── UserPO.java
    │   └── RolePO.java
    └── mapper/
        ├── UserMapper.java
        └── RoleMapper.java
```

## 端口

- 服務端口: 8081
- 資料庫: hrms_iam

## API 端點

### 使用者管理 (User Management)

| Method | Endpoint | Controller | Description |
|:---|:---|:---|:---|
| POST | `/api/v1/users` | HR01UserCmdController | 新增使用者 |
| PUT | `/api/v1/users/{id}` | HR01UserCmdController | 更新使用者 |
| DELETE | `/api/v1/users/{id}` | HR01UserCmdController | 刪除使用者 |
| GET | `/api/v1/users` | HR01UserQryController | 查詢使用者列表 |
| GET | `/api/v1/users/{id}` | HR01UserQryController | 查詢使用者詳情 |

### 角色管理 (Role Management)

| Method | Endpoint | Controller | Description |
|:---|:---|:---|:---|
| POST | `/api/v1/roles` | HR01RoleCmdController | 新增角色 |
| PUT | `/api/v1/roles/{id}` | HR01RoleCmdController | 更新角色 |
| DELETE | `/api/v1/roles/{id}` | HR01RoleCmdController | 刪除角色 |
| GET | `/api/v1/roles` | HR01RoleQryController | 查詢角色列表 |

### 認證 (Authentication)

| Method | Endpoint | Description |
|:---|:---|:---|
| POST | `/api/v1/auth/login` | 登入 |
| POST | `/api/v1/auth/logout` | 登出 |
| POST | `/api/v1/auth/refresh` | 刷新Token |
| POST | `/api/v1/auth/sso/google` | Google SSO |
| POST | `/api/v1/auth/sso/microsoft` | Microsoft SSO |

## 啟動

```bash
mvn spring-boot:run
```

## API 文檔

Swagger UI: http://localhost:8081/swagger-ui.html

## 數據庫

### 主要表

- `users` - 使用者表
- `roles` - 角色表
- `permissions` - 權限表
- `user_roles` - 使用者角色關聯表
- `role_permissions` - 角色權限關聯表
- `tenants` - 租戶表
- `user_sessions` - 會話表

## 環境變數

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/hrms_iam
SPRING_DATASOURCE_USERNAME=hrms_user
SPRING_DATASOURCE_PASSWORD=hrms_password

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000

# Redis
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379

# Kafka
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

## 設計文件

參考: `/spec/01_IAM服務系統設計書.md`
