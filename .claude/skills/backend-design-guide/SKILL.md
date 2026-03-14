# Backend Design Guide Skill

## 使用時機
開發後端 API、Service、Domain、Repository 時，用此 Skill 確認設計規範與慣例。
觸發關鍵字：`/backend-design-guide`、「後端設計」、「後端規範」

> **本文件會持續更新**，反映最新的後端設計決策與調整。
> 最後更新：2026-03-14

---

## 1. DDD 四層架構

```
┌─────────────────────────────────────────────────┐
│ Interface Layer（API 層）                        │
│   Controller (Cmd/Qry) + Request/Response DTO   │
├─────────────────────────────────────────────────┤
│ Application Layer（應用層）                      │
│   Service + Pipeline Context + Tasks            │
├─────────────────────────────────────────────────┤
│ Domain Layer（領域層 — 純 POJO）                 │
│   Aggregate + Entity + VO + DomainService       │
│   + Domain Event + Repository Interface         │
├─────────────────────────────────────────────────┤
│ Infrastructure Layer（基礎設施層）               │
│   Repository Impl + PO + DAO + Mapper           │
└─────────────────────────────────────────────────┘
```

### 關鍵規則
- Domain 層 **不依賴任何框架**（純 Java POJO）
- Repository Interface 定義在 Domain 層（依賴反轉 DIP）
- Service 只做 **編排（Orchestration）**，不做 **決策（Decision）**
- 驗證邏輯放在 Domain 層（Value Object 自帶驗證）

---

## 2. 檔案位置

```
src/main/java/com/company/hrms/{service}/
├── api/
│   ├── controller/{feature}/
│   │   ├── HR{DD}{Screen}CmdController.java    # POST/PUT/DELETE
│   │   └── HR{DD}{Screen}QryController.java    # GET
│   ├── request/{feature}/
│   │   └── {Verb}{Noun}Request.java
│   └── response/{feature}/
│       └── {Noun}{Type}Response.java
├── application/service/{feature}/
│   ├── {Verb}{Noun}ServiceImpl.java            # @Service("{method}ServiceImpl")
│   ├── context/{UseCase}Context.java           # extends PipelineContext
│   └── task/{Verb}{Business}Task.java          # implements PipelineTask<C>
├── domain/
│   ├── model/
│   │   ├── aggregate/{Entity}.java             # Aggregate Root
│   │   ├── entity/{Entity}.java
│   │   └── valueobject/{Type}.java             # 不可變、自帶驗證
│   ├── service/{Business}DomainService.java
│   ├── event/{Aggregate}{PastVerb}Event.java
│   └── repository/I{Noun}Repository.java       # Interface only
└── infrastructure/
    ├── persistence/
    │   ├── repository/{Noun}RepositoryImpl.java
    │   ├── po/{Noun}PO.java
    │   ├── dao/{Noun}DAO.java
    │   └── mapper/{Noun}Mapper.java            # PO ↔ Domain
    └── external/                               # 外部服務適配器
```

---

## 3. 命名規範

| 元素 | 格式 | 範例 |
|:---|:---|:---|
| Controller (Cmd) | `HR{DD}{Screen}CmdController` | `HR01AuthCmdController` |
| Controller (Qry) | `HR{DD}{Screen}QryController` | `HR01UserQryController` |
| Application Service | `{Verb}{Noun}ServiceImpl` | `CreateUserServiceImpl` |
| Domain Service | `{Business}DomainService` | `AccountLockingDomainService` |
| Task | `{Verb}{Business}Task` | `CheckUserStatusTask` |
| Context | `{UseCase}Context` | `AuthContext` |
| Request DTO | `{Verb}{Noun}Request` | `CreateUserRequest` |
| Response DTO | `{Noun}{Type}Response` | `UserDetailResponse` |
| Aggregate Root | `{Noun}` | `User` |
| Value Object | `{Type}` | `Email`, `UserId` |
| Domain Event | `{Aggregate}{PastVerb}Event` | `UserCreatedEvent` |
| Repository Interface | `I{Noun}Repository` | `IUserRepository` |
| PO | `{Noun}PO` | `UserPO` |

---

## 4. Service Factory 模式

Controller 方法名自動對應 Service Bean：

```
Controller.createUser() → AOP 攔截 → BeanNameConfig = "createUserServiceImpl"
                        → CommandApiServiceFactory.getService()
                        → 找到 @Service("createUserServiceImpl") 的 Bean
```

### Controller

```java
@RestController
@RequestMapping("/api/v1/users")
public class HR01UserCmdController extends CommandBaseController {

    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(
            @RequestBody @Valid CreateUserRequest request,
            @AuthenticationPrincipal JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }
}
```

### Service

```java
@Service("createUserServiceImpl")  // Bean 名稱 = Controller 方法名 + "ServiceImpl"
@RequiredArgsConstructor
@Transactional
public class CreateUserServiceImpl
        implements CommandApiService<CreateUserRequest, CreateUserResponse> {

    @Override
    public CreateUserResponse execCommand(
            CreateUserRequest request, JWTModel currentUser, String... args) throws Exception {
        // Pipeline 編排
    }
}
```

---

## 5. Business Pipeline 模式

步驟 ≥ 2 步以上就需建立 Pipeline + Task。

### Context（清晰分區：輸入 → 中間 → 輸出）

```java
public class AuthContext extends PipelineContext {
    // === 輸入 ===
    private LoginRequest loginRequest;

    // === 中間數據 ===
    private User user;

    // === 輸出 ===
    private String accessToken;
    private String refreshToken;
}
```

### Task（三種類型）

| 類型 | 命名 | 職責 |
|:---|:---|:---|
| Infrastructure | `Load{Entity}Task` | 資料存取 |
| Domain | `{Verb}{Business}Task` | 業務計算/驗證 |
| Integration | `{Verb}{Service}Task` | 外部服務呼叫 |

```java
@Component
@RequiredArgsConstructor
public class CheckUserStatusTask implements PipelineTask<AuthContext> {

    @Override
    public void execute(AuthContext context) throws Exception {
        User user = context.getUser();
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new DomainException("USER_INACTIVE", "帳號已停用");
        }
    }

    @Override
    public String getName() { return "檢查使用者狀態"; }
}
```

### Pipeline 組裝

```java
BusinessPipeline.start(ctx)
    .next(loadUserByUsernameTask)
    .next(checkUserStatusTask)
    .next(validatePasswordTask)
    .nextIf(ctx -> ctx.needsMFA(), mfaVerificationTask)  // 條件執行
    .next(generateTokenTask)
    .execute();
```

---

## 6. Query Engine（Fluent Query）

### 方式一：流暢 API

```java
QueryGroup query = QueryBuilder.where()
    .eq("status", "ACTIVE")
    .eq("isDeleted", 0)
    .like("name", keyword)
    .in("role", "ADMIN", "MANAGER")
    .build();

Page<User> result = userRepository.findPage(query, pageable);
```

### 方式二：DTO 註解

```java
public class EmployeeSearchRequest {
    @QueryFilter(operator = Operator.LIKE)
    private String name;

    @QueryFilter(property = "department.id")
    private String departmentId;

    @QueryFilter(operator = Operator.IN)
    private List<String> status;
}

// Service 中
QueryGroup query = QueryBuilder.fromCondition(searchRequest);
```

### 運算子

`EQ`, `NE`, `GT`, `LT`, `GTE`, `LTE`, `LIKE`, `IN`, `NOT_IN`, `BETWEEN`, `IS_NULL`, `IS_NOT_NULL`

---

## 7. Domain 設計

### Aggregate Root（工廠方法 + 業務邏輯）

```java
@Data @Builder
public class User {
    private UserId id;
    private String username;
    private Email email;  // Value Object

    // === 工廠方法 ===
    public static User create(String username, String email, String passwordHash) {
        return User.builder()
            .id(UserId.generate())
            .username(username)
            .email(new Email(email))
            .status(UserStatus.PENDING)
            .build();
    }

    // === 業務邏輯 ===
    public void lock(int minutes) {
        this.status = UserStatus.LOCKED;
        this.lockedUntil = LocalDateTime.now().plusMinutes(minutes);
    }
}
```

### Value Object（不可變 + 自帶驗證）

```java
public class Email {
    private final String value;

    public Email(String value) {
        if (value == null || !EMAIL_PATTERN.matcher(value).matches()) {
            throw new DomainException("EMAIL_INVALID", "Email 格式無效");
        }
        this.value = value.trim().toLowerCase();
    }
}
```

### Domain Event

```java
@Getter
public class UserCreatedEvent extends DomainEvent {
    private final String userId;
    private final String email;

    @Override
    public String getAggregateId() { return userId; }

    @Override
    public String getAggregateType() { return "User"; }
}

// 發布
eventPublisher.publish(new UserCreatedEvent(user.getId(), user.getEmail()));
```

---

## 8. 異常處理

### 異常類型 → HTTP 對應

| 異常 | HTTP | 使用情境 |
|:---|:---|:---|
| `DomainException` | 400 | 業務規則違反 |
| `DomainException("LOGIN_FAILED")` | 401 | 認證失敗 |
| `DomainException("*_EXISTS")` | 409 | 資源已存在 |
| `EntityNotFoundException` | 404 | 資源不存在 |
| `AccessDeniedException` | 403 | 授權失敗 |
| `PipelineExecutionException` | 依根因 | Pipeline 失敗 |

### DomainException

```java
throw new DomainException("USER_INACTIVE", "使用者帳號已停用");
throw new DomainException("USERNAME_EXISTS", "使用者名稱已存在: " + username);
```

### ErrorCode 規範

```
{DD}{HTTP_STATUS_SHORT}{SEQUENCE}
01  4  001  →  IAM 模組, 4xx 錯誤, 第 001 號
```

---

## 9. Request/Response DTO

### Request（含驗證）

```java
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateUserRequest {
    @NotBlank(message = "使用者名稱不可為空")
    private String username;

    @NotBlank @Size(min = 8, max = 128)
    private String password;

    @NotBlank @Email
    private String email;
}
```

### Response

```java
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private UserInfoResponse user;
}
```

### Swagger 註解

```java
@Operation(summary = "使用者登入", operationId = "login")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "登入成功"),
    @ApiResponse(responseCode = "401", description = "帳號或密碼錯誤"),
    @ApiResponse(responseCode = "423", description = "帳號已被鎖定")
})
```

---

## 10. 測試規範

### 測試結構

```
src/test/java/.../
├── api/
│   ├── contract/           # 合約測試（從 contracts/*.md 載入）
│   │   └── {Service}ApiContractTest.java
│   └── controller/         # 整合測試（MockMvc）
│       └── {Screen}ApiIntegrationTest.java
├── application/service/
│   └── {feature}/task/     # Task 單元測試（Mockito）
│       └── {Task}Test.java
└── domain/
    └── model/              # Domain 單元測試
        └── {Aggregate}Test.java
```

### Task 單元測試

```java
@ExtendWith(MockitoExtension.class)
class CheckUserStatusTaskTest {
    @Mock private IUserRepository userRepository;
    @InjectMocks private CheckUserStatusTask task;

    @Test
    void shouldThrowWhenUserInactive() {
        User user = createInactiveUser();
        context.setUser(user);

        DomainException ex = assertThrows(DomainException.class,
            () -> task.execute(context));
        assertEquals("USER_INACTIVE", ex.getErrorCode());
    }
}
```

### 合約測試

```java
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthApiContractTest extends BaseContractTest {

    @Test
    void login_AUTH_CMD_001() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(APPLICATION_JSON)
                .content(toJson(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists());
    }
}
```

### 覆蓋率要求

| 層級 | 要求 |
|:---|:---|
| Domain 邏輯 | 100% |
| Pipeline Task | 所有路徑 |
| API 端點 | 整合測試必要 |
| ErrorCode → HTTP | 每個映射都要測試 |

---

## 11. 架構師三原則

1. **能用「宣告」的，就不要用「程式碼」** — 如 `@QueryFilter` 註解取代手寫查詢
2. **能回傳「結構化物件」的，就不要回傳「基本型別」** — 方便快照測試
3. **Service 只做「編排」，不做「決策」** — 業務決策在 Domain / Task 中

---

## 12. 常用技術棧

- **框架**：Spring Boot 3.1 + Spring Cloud 2023
- **資料庫**：PostgreSQL 15+（測試用 H2）
- **ORM**：JPA + Querydsl + MyBatis
- **快取**：Redis
- **訊息**：Kafka
- **服務發現**：Eureka
- **建構**：Maven + Lombok
