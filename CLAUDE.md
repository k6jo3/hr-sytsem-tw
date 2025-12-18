# CLAUDE.md - AI Assistant Guide

**Version:** 3.1
**Last Updated:** 2025-12-09
**Purpose:** This document provides AI assistants with comprehensive guidance for understanding and working with this HR & Project Management System codebase.

---
## 回答語言
請用繁體中文回答
## 📋 Table of Contents

1. [Project Overview](#-project-overview)
2. [Codebase Structure](#-codebase-structure)
3. [Architecture & Design Patterns](#-architecture--design-patterns)
4. [Development Workflows](#-development-workflows)
5. [Naming Conventions](#-naming-conventions)
6. [Technology Stack](#-technology-stack)
7. [Key Concepts for AI Assistants](#-key-concepts-for-ai-assistants)
8. [Common Development Tasks](#-common-development-tasks)
9. [Testing Guidelines](#-testing-guidelines)
10. [Important Files Reference](#-important-files-reference)

---

## 🎯 Project Overview

### What is This System?

This is an **Enterprise-grade HR & Project Management System** built with microservices architecture, designed for comprehensive human resources management and project cost tracking.

**Key Characteristics:**
- **14 Microservices** - Full business domain separation
- **DDD (Domain-Driven Design)** - Clean architecture with strict layering
- **Event-Driven** - Kafka-based asynchronous communication
- **CQRS** - Command/Query separation for scalability
- **Taiwan Labor Law Compliant** - Full regulatory compliance
- **Project Cost Analytics** - Integration of timesheet and payroll for precise cost calculation

### Project Status

| Phase | Status | Notes |
|:---|:---|:---|
| Requirements Analysis | ✅ Complete | 14 service requirement documents |
| PM Review | ✅ Complete | All review items addressed |
| System Design | ✅ Complete | All 14 microservices designed |
| Logic Specifications | ✅ Complete | 5 detailed spec documents |
| Development Standards | ✅ Complete | Frontend & backend guidelines aligned |
| Compliance Reports | ✅ Complete | Implementation & design compliance checks |
| **Implementation** | ⏳ In Progress | 9 frontend features with TDD, backend scaffolding complete |
| Testing & QA | ⏳ In Progress | 17 test files, TDD approach, see TEST_SUMMARY.md |

**Implementation Progress:**
- **Frontend:** 9/14 features implemented (64%)
  - ✅ HR01-Login, HR02-Employee List, HR03-Attendance, HR04-Payslips
  - ✅ HR05-Insurance, HR06-Projects, HR07-Timesheet, HR08-Performance, HR09-Recruitment
- **Backend:** Microservices scaffolding complete, IAM service ~15% implemented
- **Testing:** TDD approach with Factory pattern tests, component tests, hook tests

---

## 📁 Codebase Structure

### Repository Layout

```
hr-system-2/
├── README.md                           # Project overview (Chinese)
├── CLAUDE.md                           # This file - AI assistant guide
├── LICENSE
│
├── SA/                                 # System Analysis documents
│   ├── 人力資源暨專案管理系統_正式需求規格書.md
│   ├── PM需求審查報告.md
│   ├── 系統開發工作計畫書.md
│   ├── 01_IAM服務需求分析書.md
│   └── ... (14 service requirement docs)
│
├── spec/                               # System Design documents
│   ├── 系統架構設計文件.md              # Overall architecture & DDD layers
│   ├── 系統架構設計文件_命名規範.md      # Naming conventions
│   ├── 系統實作合規性檢查報告.md        # Implementation compliance check report
│   ├── 系統設計書與需求分析書合規性檢查報告.md  # Design vs requirements compliance
│   ├── 系統設計書命名規範合規性檢查.md   # Naming convention compliance check
│   ├── 01_IAM服務系統設計書*.md         # IAM service design (3 parts)
│   ├── 02_組織員工服務系統設計書*.md    # Organization service (4 parts)
│   ├── ... (All 14 microservices)
│   │
│   └── logic_spec/                     # Business Logic Specifications
│       ├── variable_hours_rules.md              # Variable working hours
│       ├── occupational_injury_compensation.md  # Work injury compensation
│       ├── tax_insurance_tables_2025.md         # 2025 tax/insurance tables
│       ├── sso_account_linking.md               # SSO account linking
│       └── regulatory_parameters_and_audit.md   # Regulatory param management
│
├── backend/                            # Backend microservices
│   ├── 架構說明與開發規範.md
│   ├── pom.xml                         # Parent POM
│   ├── hrms-common/                    # Shared libraries
│   ├── hrms-iam/                       # IAM Service (01)
│   ├── hrms-organization/              # Organization Service (02)
│   └── ... (14 microservices total)
│
└── frontend/                           # React frontend
    ├── 架構說明與開發規範.md
    ├── TEST_SUMMARY.md                 # TDD test summary and progress
    ├── package.json
    ├── vite.config.ts
    ├── tsconfig.json
    └── src/
        ├── features/                   # Feature-based modules
        │   ├── auth/                   # IAM (HR01)
        │   │   ├── api/                # API calls
        │   │   ├── factory/            # DTO transformations (with tests)
        │   │   ├── components/         # UI components (with tests)
        │   │   ├── hooks/              # React hooks (with tests)
        │   │   └── model/              # Frontend domain models
        │   ├── organization/           # ORG (HR02)
        │   ├── attendance/             # ATT (HR03)
        │   ├── payroll/                # PAY (HR04)
        │   └── ... (14 features)
        ├── pages/                      # Page components (21 pages)
        ├── shared/                     # Shared utilities
        ├── store/                      # Redux state
        └── App.tsx
```

---

## 🏗️ Architecture & Design Patterns

### Microservices Architecture

**14 Microservices** organized by business domain:

| Code | Service | Description | Design | Backend | Frontend |
|:---:|:---|:---|:---:|:---:|:---:|
| **01** | IAM | Auth, RBAC, SSO, Multi-tenancy | ✅ | 🟡 15% | ✅ Complete |
| **02** | Organization | Employee lifecycle, org structure, ESS | ✅ | 🔴 0% | ✅ Complete |
| **03** | Attendance | Clock in/out, leave, overtime, variable hours | ✅ | 🔴 0% | ✅ Complete |
| **04** | Payroll | Salary calculation (Saga), tax, overtime pay | ✅ | 🔴 0% | ✅ Complete |
| **05** | Insurance | Labor/health insurance, pension | ✅ | 🔴 0% | ✅ Complete |
| **06** | Project | Customer, multi-level WBS, cost tracking | ✅ | 🔴 0% | ✅ Complete |
| **07** | Timesheet | Weekly timesheet, PM approval | ✅ | 🔴 0% | ✅ Complete |
| **08** | Performance | Review cycles, flexible forms | ✅ | 🔴 0% | ✅ Complete |
| **09** | Recruitment | Job posting, Kanban, interview | ✅ | 🔴 0% | ✅ Complete |
| **10** | Training | Course management, certifications | ✅ | 🔴 0% | 🟡 Skeleton |
| **11** | Workflow | Visual workflow designer, multi-level approval | ✅ | 🔴 0% | 🟡 Skeleton |
| **12** | Notification | Email/Push/Teams/LINE, event-driven | ✅ | 🔴 0% | 🟡 Skeleton |
| **13** | Document | Storage, versioning, templates, encryption | ✅ | 🔴 0% | 🟡 Skeleton |
| **14** | Reporting | CQRS read models, dashboards | ✅ | 🔴 0% | 🟡 Skeleton |

**Legend:** ✅ Complete | 🟡 Partial | 🔴 Not Started

### DDD (Domain-Driven Design) Layering

**CRITICAL:** Every microservice MUST follow this 4-layer structure:

```
┌─────────────────────────────────────────────────────────┐
│ Interface Layer (介面層)                                │
│ - REST Controllers                                      │
│ - Naming: HR{DD}{Screen}{Cmd/Qry}Controller            │
│ - Request/Response DTOs                                 │
├─────────────────────────────────────────────────────────┤
│ Application Layer (應用層)                              │
│ - Use Case orchestration                               │
│ - Saga workflow coordination                           │
│ - NO core business rules here                          │
├─────────────────────────────────────────────────────────┤
│ Domain Layer (領域層 - CORE)                           │
│ - Pure Java POJOs (no framework dependencies)          │
│ - Aggregate Roots, Entities, Value Objects             │
│ - Domain Services                                      │
│ - Business logic lives here                            │
├─────────────────────────────────────────────────────────┤
│ Infrastructure Layer (基礎設施層)                       │
│ - Repository implementations                           │
│ - DAO, Mappers (MyBatis)                               │
│ - External service adapters                            │
└─────────────────────────────────────────────────────────┘
```

### Key Design Patterns

#### 1. CQRS (Command Query Responsibility Segregation)

**Controllers are split into Command and Query:**

```java
// Command Controller - Writes (POST, PUT, DELETE)
@RestController
@RequestMapping("/api/v1/users")
public class HR01UserCmdController extends CommandBaseController {
    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(...) { }
}

// Query Controller - Reads (GET only)
@RestController
@RequestMapping("/api/v1/users")
public class HR01UserQryController extends QueryBaseController {
    @GetMapping
    public ResponseEntity<UserListResponse> getUserList(...) { }
}
```

#### 2. Service Factory Pattern

**Controllers don't directly inject services.** Instead, they use dynamic service resolution:

```java
// Controller method name "createUser" automatically resolves to Service bean "createUserServiceImpl"
@PostMapping
public ResponseEntity<CreateUserResponse> createUser(...) throws Exception {
    return ResponseEntity.ok(execCommand(request, currentUser));
}
```

**How it works:**
1. `ApiServiceAspect` (AOP) intercepts controller method
2. Sets target service bean name in `BeanNameConfig` (request-scoped)
3. `CommandApiServiceFactory` retrieves the service
4. `BaseController.execCommand()` invokes the service

**Service Implementation:**

```java
@Service("createUserServiceImpl")  // Bean name MUST match pattern
@Transactional
public class CreateUserServiceImpl
        implements CommandApiService<CreateUserRequest, CreateUserResponse> {

    @Override
    public CreateUserResponse execCommand(
            CreateUserRequest req,
            JWTModel currentUser,
            String... args) throws Exception {
        // Business logic here
    }
}
```

#### 3. Factory Pattern (Frontend)

**MANDATORY in React components:** Never use raw API data directly.

```typescript
// ❌ WRONG - Direct API data usage
const UserList = () => {
  const { data } = useQuery(...);
  return <div>{data.first_name} {data.last_name}</div>;
}

// ✅ CORRECT - Factory transformation
const UserList = () => {
  const { data } = useQuery(...);
  const viewModel = UserViewModelFactory.createFromDTO(data);
  return <div>{viewModel.fullName}</div>;
}
```

#### 4. Event-Driven Architecture

**Services communicate asynchronously via Kafka events:**

```java
// Publishing events
@Service
public class CreateEmployeeServiceImpl implements CommandApiService<...> {
    @Autowired
    private EventPublisher eventPublisher;

    @Override
    public CreateEmployeeResponse execCommand(...) {
        Employee emp = Employee.create(...);
        employeeRepository.save(emp);

        // Publish domain event
        eventPublisher.publish(new EmployeeCreatedEvent(emp.getId(), emp.getName()));
        return ...;
    }
}
```

**Event flow example - Employee onboarding:**
```
Organization → EmployeeCreatedEvent
   ├→ IAM: Create user account
   ├→ Insurance: Auto enrollment
   └→ Payroll: Create salary structure
```

---

## 🔄 Development Workflows

### For Backend Development

**When adding a new API endpoint:**

1. **Read the Design Document** first:
   - `spec/{NN}_{ServiceName}服務系統設計書.md`
   - Check API design section for endpoint specification
   - Review UX flow diagrams for business logic

2. **Define Request/Response DTOs** in `api/request/` and `api/response/`

3. **Create Controller Method** following naming convention:
   ```java
   // Method name determines service bean name
   public ResponseEntity<CreateUserResponse> createUser(...) { }
   // → Will resolve to bean: "createUserServiceImpl"
   ```

4. **Implement Service** in `application/service/`:
   ```java
   @Service("createUserServiceImpl")  // MUST match method name + ServiceImpl
   public class CreateUserServiceImpl implements CommandApiService<...> { }
   ```

5. **Implement Domain Logic** in `domain/model/`:
   ```java
   // Aggregate Root
   public class User extends AggregateRoot<UserId> {
       public static User create(String name, Email email) { ... }
   }
   ```

6. **Add Swagger Annotations** to controller:
   ```java
   @Operation(summary = "新增使用者", operationId = "createUser")
   @ApiResponses(value = {
       @ApiResponse(responseCode = "200", description = "成功"),
       @ApiResponse(responseCode = "400", description = "請求格式錯誤")
   })
   ```

7. **Write Tests First (TDD)**:
   - Unit tests for domain logic (100% coverage required)
   - Integration tests for API endpoints

### For Frontend Development

**When adding a new page:**

1. **Check Design Document**:
   - `spec/{NN}_{ServiceName}服務系統設計書.md`
   - Review UI wireframes and page codes (HR{DD}-P{XX})

2. **Create Page Component** in `src/pages/`:
   ```typescript
   // HR01-P01 → HR01LoginPage.tsx
   export const HR01LoginPage: React.FC = () => { ... }
   ```

3. **Create Feature Module** in `src/features/{domain}/`:
   ```
   src/features/auth/
   ├── api/
   │   ├── AuthApi.ts          // API calls
   │   └── AuthTypes.ts        // DTOs
   ├── factory/
   │   └── UserViewModelFactory.ts  // DTO transformation
   ├── components/
   │   └── LoginForm.tsx
   ├── hooks/
   │   └── useLogin.ts
   └── model/
       └── UserProfile.ts      // Frontend domain model
   ```

4. **Implement Factory** (MANDATORY):
   ```typescript
   export class UserViewModelFactory {
     static createFromDTO(dto: UserDto): UserViewModel {
       return {
         id: dto.id,
         fullName: `${dto.first_name} ${dto.last_name}`,
         isAdmin: dto.role_list.includes('ADMIN')
       };
     }
   }
   ```

5. **Write Tests** (TDD):
   ```typescript
   // Factory.test.ts
   describe('UserViewModelFactory', () => {
     it('should transform DTO correctly', () => { ... });
   });
   ```

### Git Workflow

**IMPORTANT:** This project uses feature branches with strict naming:

```bash
# Branch naming pattern: claude/claude-md-{random-id}-{session-id}
# Example current branch: claude/claude-md-miyizlkbnm098nsi-013HYCF3b8wKt6qfwCRYu8qa

# Check current branch
git branch --show-current

# Make changes, then commit
git add .
git commit -m "feat: Implement user management API"

# Push to remote (MUST use current branch)
git push -u origin $(git branch --show-current)
```

**Critical Git Requirements:**
- ✅ Branch MUST start with `claude/`
- ✅ Branch MUST match the session ID provided in task context
- ❌ Push will fail with 403 if branch name doesn't match pattern
- ⚠️ Always check the task instructions for the correct branch name
- 🔄 If push fails due to network errors, retry up to 4 times with exponential backoff (2s, 4s, 8s, 16s)

---

## 📐 Naming Conventions

### Domain Code Reference

| Code | Domain | Backend Package | Frontend Feature |
|:---:|:---|:---|:---|
| `01` | IAM | `com.company.hrms.iam` | `features/auth` |
| `02` | ORG | `com.company.hrms.organization` | `features/organization` |
| `03` | ATT | `com.company.hrms.attendance` | `features/attendance` |
| `04` | PAY | `com.company.hrms.payroll` | `features/payroll` |
| `05` | INS | `com.company.hrms.insurance` | `features/insurance` |
| `06` | PRJ | `com.company.hrms.project` | `features/project` |
| `07` | TMS | `com.company.hrms.timesheet` | `features/timesheet` |
| `08` | PFM | `com.company.hrms.performance` | `features/performance` |
| `09` | RCT | `com.company.hrms.recruitment` | `features/recruitment` |
| `10` | TRN | `com.company.hrms.training` | `features/training` |
| `11` | WFL | `com.company.hrms.workflow` | `features/workflow` |
| `12` | NTF | `com.company.hrms.notification` | `features/notification` |
| `13` | DOC | `com.company.hrms.document` | `features/document` |
| `14` | RPT | `com.company.hrms.reporting` | `features/report` |

### Backend Naming Standards

| Element | Format | Example |
|:---|:---|:---|
| Controller (Command) | `HR{DD}{Screen}CmdController` | `HR01UserCmdController` |
| Controller (Query) | `HR{DD}{Screen}QryController` | `HR01UserQryController` |
| Application Service | `{Verb}{Noun}ServiceImpl` | `CreateUserServiceImpl` |
| Domain Service | `{BusinessEvent}DomainService` | `AccountLockingDomainService` |
| Request DTO | `{Verb}{Noun}Request` | `CreateUserRequest` |
| Response DTO | `{Noun}{Type}Response` | `UserDetailResponse` |
| Domain Event | `{Aggregate}{PastVerb}Event` | `UserCreatedEvent` |
| Repository Interface | `I{Noun}Repository` | `IUserRepository` |
| Repository Impl | `{Noun}RepositoryImpl` | `UserRepositoryImpl` |
| DAO | `{Noun}DAO` | `UserDAO` |
| PO (Persistence) | `{Noun}PO` | `UserPO` |
| Mapper (MyBatis) | `{Noun}Mapper` | `UserMapper` |

### Frontend Naming Standards

| Element | Format | Example |
|:---|:---|:---|
| Page Component | `HR{DD}{PageName}Page.tsx` | `HR01LoginPage.tsx` |
| Page Code | `HR{DD}-P{NN}` | `HR01-P01` (Login page) |
| Modal Code | `HR{DD}-M{NN}` | `HR01-M01` (User edit modal) |
| Component | `PascalCase` | `EmployeeCard.tsx` |
| Hook | `use{Name}.ts` | `useLogin.ts` |
| Factory | `{Name}ViewModelFactory.ts` | `UserViewModelFactory.ts` |
| API Module | `{Feature}Api.ts` | `AuthApi.ts` |
| Types | `{Feature}Types.ts` | `AuthTypes.ts` |

### API Path Conventions

```
/api/v{version}/{resource-plural}/{id}/{sub-resource}/{action}
```

**Examples:**
```
GET    /api/v1/employees              # List employees
GET    /api/v1/employees/{id}         # Get employee detail
POST   /api/v1/employees              # Create employee
PUT    /api/v1/employees/{id}         # Update employee
DELETE /api/v1/employees/{id}         # Delete employee
GET    /api/v1/employees/{id}/leaves  # Get employee's leaves
POST   /api/v1/employees/{id}/terminate  # Terminate employee (action)
```

---

## 🛠️ Technology Stack

### Backend

| Category | Technology | Version | Notes |
|:---|:---|:---|:---|
| Framework | Spring Boot | 3.1.x | Core framework |
| Microservices | Spring Cloud | 2023.x | Service discovery, config |
| API Gateway | Spring Cloud Gateway | - | Routing, auth, rate limiting |
| Service Discovery | Eureka | - | Service registry |
| Auth | Spring Security + OAuth2 + JWT | - | Authentication & authorization |
| Database | PostgreSQL | 15+ | One DB per service |
| ORM (Legacy) | MyBatis | 3.5.x | Existing features, complex queries |
| ORM (New) | Querydsl + JPA | 5.0.0 | New features, see [Fluent-Query-Engine.md](Fluent-Query-Engine.md) |
| Cache | Redis | 7+ | Session, cache |
| Message Queue | Kafka | 3+ | Event-driven communication |
| Distributed Tracing | Sleuth + Zipkin | - | Request tracing |

### Frontend

| Category | Technology | Version | Notes |
|:---|:---|:---|:---|
| Framework | React | 18.x | UI library |
| Language | TypeScript | 5.x | Type safety |
| Build Tool | Vite | 5.x | Fast build |
| State Management | Redux Toolkit | - | Global state |
| UI Library | Ant Design | 5.x | Component library |
| Charts | Apache ECharts | - | Data visualization |
| HTTP Client | Axios | - | API communication |
| Routing | React Router | 6.x | SPA routing |
| Testing | Vitest + RTL | - | Unit & component tests |

---

## 💡 Key Concepts for AI Assistants

### 1. Always Read Design Documents First

**Before implementing any feature:**
1. Check `spec/{NN}_{ServiceName}服務系統設計書.md`
2. Review API specifications (Chapter 9)
3. Study UX flow diagrams (Chapter 3)
4. Understand domain models (Chapter 7)

### 2. Service-Method 1:1 Mapping

**CRITICAL:** Controller method names directly map to service bean names.

```java
// Controller
public ResponseEntity<CreateUserResponse> createUser(...) { }
// → Service bean: "createUserServiceImpl"

public ResponseEntity<UpdateUserResponse> updateUser(...) { }
// → Service bean: "updateUserServiceImpl"
```

**Naming pattern:**
- Method: `{verb}{Noun}` (camelCase)
- Service: `{Verb}{Noun}ServiceImpl` (PascalCase + Impl suffix)
- Bean name: `{verb}{Noun}ServiceImpl` (camelCase + Impl suffix)

### 3. Domain Logic Belongs in Domain Layer

**WRONG:**
```java
@Service
public class CreateUserServiceImpl implements CommandApiService<...> {
    public CreateUserResponse execCommand(...) {
        // ❌ Business logic in Application Layer
        if (email.contains("@")) { ... }
        if (password.length() < 8) { ... }
    }
}
```

**CORRECT:**
```java
// Application Layer - Orchestration only
@Service
public class CreateUserServiceImpl implements CommandApiService<...> {
    public CreateUserResponse execCommand(...) {
        User user = User.create(req.getName(), new Email(req.getEmail()));
        userRepository.save(user);
        return ...;
    }
}

// Domain Layer - Business logic
public class User extends AggregateRoot<UserId> {
    public static User create(String name, Email email) {
        // ✅ Validation in domain
        Objects.requireNonNull(name, "Name is required");
        if (email == null) throw new InvalidEmailException();
        return new User(...);
    }
}

// Value Object with validation
public class Email {
    private final String value;

    public Email(String value) {
        if (!value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new InvalidEmailException();
        }
        this.value = value;
    }
}
```

### 4. Frontend Factory Pattern is Mandatory

**Never skip the Factory transformation:**

```typescript
// API response
interface UserDto {
  id: string;
  first_name: string;
  last_name: string;
  role_list: string[];
  status: 'ACTIVE' | 'INACTIVE';
}

// ViewModel (what UI needs)
interface UserViewModel {
  id: string;
  fullName: string;
  isAdmin: boolean;
  displayStatus: string;
}

// Factory
export class UserViewModelFactory {
  static createFromDTO(dto: UserDto): UserViewModel {
    return {
      id: dto.id,
      fullName: `${dto.first_name} ${dto.last_name}`,
      isAdmin: dto.role_list.includes('ADMIN'),
      displayStatus: dto.status === 'ACTIVE' ? '在職' : '離職'
    };
  }
}
```

### 5. Complex Business Logic Has Specifications

**For complex rules (variable hours, tax calculation, etc.), check:**
- `spec/logic_spec/variable_hours_rules.md` - Taiwan labor law variable hours
- `spec/logic_spec/occupational_injury_compensation.md` - Work injury compensation
- `spec/logic_spec/tax_insurance_tables_2025.md` - 2025 tax/insurance tables
- `spec/logic_spec/sso_account_linking.md` - SSO account linking flow
- `spec/logic_spec/regulatory_parameters_and_audit.md` - Regulatory parameters

**Example: Variable Hours Calculation**

If implementing overtime calculation for 2-week variable hours:
1. Read `spec/logic_spec/variable_hours_rules.md`
2. Follow the exact calculation logic specified
3. Use the provided formulas and rate tables

### 6. Event-Driven Communication Patterns

**When to publish events:**
- After successful state changes (e.g., user created, employee terminated)
- When other services need to react (e.g., insurance enrollment after employee creation)

**Event naming:** `{Aggregate}{PastVerb}Event`

Examples:
- `EmployeeCreatedEvent`
- `LeaveApprovedEvent`
- `PayrollCalculatedEvent`
- `ProjectCompletedEvent`

### 7. Test-Driven Development (TDD)

**This project strictly follows TDD approach:** See `frontend/TEST_SUMMARY.md` for detailed test coverage.

**Always follow RED-GREEN-REFACTOR:**

1. **RED:** Write failing test first
2. **GREEN:** Write minimal code to pass
3. **REFACTOR:** Improve code quality

**Current Test Coverage (Frontend):**
- ✅ 17 test files implemented
- ✅ Factory tests: 100% coverage for all implemented features
- ✅ Component tests: All major UI components tested
- ✅ Hook tests: All custom hooks tested
- 🎯 Target: 80%+ code coverage

**Coverage requirements:**
- Domain logic: 100% (mandatory)
- API endpoints: Integration tests required
- Frontend factories: Unit tests required (mandatory)
- Frontend components: Component tests required
- Frontend hooks: Hook tests required

**Test Organization:**
```
src/features/{domain}/
├── factory/
│   ├── UserViewModelFactory.ts
│   └── UserViewModelFactory.test.ts     # ✅ Required
├── components/
│   ├── LoginForm.tsx
│   └── LoginForm.test.tsx               # ✅ Required
└── hooks/
    ├── useLogin.ts
    └── useLogin.test.ts                 # ✅ Required
```

### 8. Documentation Standards

**Swagger for Backend:**
```java
@Operation(summary = "新增使用者", operationId = "createUser")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "成功"),
    @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
    @ApiResponse(responseCode = "401", description = "未授權")
})
```

**TSDoc for Frontend:**
```typescript
/**
 * 將 API 回傳的使用者 DTO 轉換為前端 ViewModel
 * @param dto - API 回傳的原始資料
 * @returns 前端顯示用的 ViewModel
 */
export function createUserViewModel(dto: UserDto): UserViewModel {
  // ...
}
```

---

## 🔧 Common Development Tasks

### Task 1: Add a New Query Endpoint

**Scenario:** Add `GET /api/v1/employees/{id}/leaves` to get employee's leave records.

**Steps:**

1. **Check Design Document:**
   ```bash
   # Read the design spec
   cat spec/03_考勤管理服務系統設計書.md
   ```

2. **Create Response DTO:**
   ```java
   // api/response/leave/EmployeeLeaveListResponse.java
   public class EmployeeLeaveListResponse {
       private List<LeaveItem> leaves;
       // getters/setters
   }
   ```

3. **Add Controller Method:**
   ```java
   // HR03LeaveQryController.java
   @GetMapping("/employees/{employeeId}/leaves")
   public ResponseEntity<EmployeeLeaveListResponse> getEmployeeLeaves(
           @PathVariable String employeeId,
           @CurrentUser JWTModel currentUser) throws Exception {
       return ResponseEntity.ok(getResponse(
           new GetEmployeeLeavesRequest(employeeId), currentUser));
   }
   ```

4. **Implement Service:**
   ```java
   @Service("getEmployeeLeavesServiceImpl")
   public class GetEmployeeLeavesServiceImpl
           implements QueryApiService<GetEmployeeLeavesRequest, EmployeeLeaveListResponse> {

       @Override
       public EmployeeLeaveListResponse getResponse(
               GetEmployeeLeavesRequest req, JWTModel currentUser) {
           List<Leave> leaves = leaveRepository.findByEmployeeId(req.getEmployeeId());
           return new EmployeeLeaveListResponse(leaves);
       }
   }
   ```

### Task 2: Add a New React Page

**Scenario:** Create employee detail page (HR02-P02).

**Steps:**

1. **Create Page Component:**
   ```typescript
   // src/pages/HR02EmployeeDetailPage.tsx
   export const HR02EmployeeDetailPage: React.FC = () => {
     const { id } = useParams();
     const { employee, loading } = useEmployeeDetail(id);

     if (loading) return <Spin />;

     return (
       <div>
         <EmployeeHeader employee={employee} />
         <EmployeeTabs employee={employee} />
       </div>
     );
   }
   ```

2. **Create Custom Hook:**
   ```typescript
   // src/features/organization/hooks/useEmployeeDetail.ts
   export const useEmployeeDetail = (id: string) => {
     const { data, isLoading } = useQuery({
       queryKey: ['employee', id],
       queryFn: () => EmployeeApi.getDetail(id)
     });

     const employee = data
       ? EmployeeViewModelFactory.createFromDTO(data)
       : null;

     return { employee, loading: isLoading };
   }
   ```

3. **Create Factory:**
   ```typescript
   // src/features/organization/factory/EmployeeViewModelFactory.ts
   export class EmployeeViewModelFactory {
     static createFromDTO(dto: EmployeeDto): EmployeeViewModel {
       return {
         id: dto.id,
         fullName: `${dto.first_name} ${dto.last_name}`,
         departmentName: dto.department?.name ?? '-',
         statusLabel: this.getStatusLabel(dto.status)
       };
     }

     private static getStatusLabel(status: string): string {
       const labels = {
         'ACTIVE': '在職',
         'RESIGNED': '離職',
         'ON_LEAVE': '留職停薪'
       };
       return labels[status] ?? '未知';
     }
   }
   ```

4. **Add Route:**
   ```typescript
   // src/App.tsx
   <Route path="/employees/:id" element={<HR02EmployeeDetailPage />} />
   ```

### Task 3: Publish a Domain Event

**Scenario:** Publish `EmployeeCreatedEvent` after creating an employee.

**Steps:**

1. **Define Event:**
   ```java
   // domain/event/EmployeeCreatedEvent.java
   public class EmployeeCreatedEvent extends DomainEvent {
       private String employeeId;
       private String employeeName;
       private String email;

       // constructors, getters
   }
   ```

2. **Publish in Service:**
   ```java
   @Service("createEmployeeServiceImpl")
   public class CreateEmployeeServiceImpl implements CommandApiService<...> {
       @Autowired
       private EventPublisher eventPublisher;

       @Override
       public CreateEmployeeResponse execCommand(...) {
           Employee emp = Employee.create(...);
           employeeRepository.save(emp);

           // Publish event
           eventPublisher.publish(new EmployeeCreatedEvent(
               emp.getId(),
               emp.getName(),
               emp.getEmail()
           ));

           return new CreateEmployeeResponse(emp.getId());
       }
   }
   ```

3. **Subscribe in Another Service:**
   ```java
   // In IAM Service
   @Service
   public class EmployeeEventListener {
       @KafkaListener(topics = "employee.created")
       public void handleEmployeeCreated(EmployeeCreatedEvent event) {
           // Create user account for new employee
           User user = User.createFromEmployee(
               event.getEmployeeId(),
               event.getEmail()
           );
           userRepository.save(user);
       }
   }
   ```

---

## 🧪 Testing Guidelines

### Backend Testing

**Unit Test (Domain Logic):**

```java
class UserTest {
    @Test
    void shouldCreateUserWithValidData() {
        // Given
        Email email = new Email("john@example.com");

        // When
        User user = User.create("John Doe", email);

        // Then
        assertNotNull(user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals(email, user.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        assertThrows(InvalidEmailException.class, () -> {
            new Email("invalid-email");
        });
    }
}
```

**Integration Test (API):**

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class HR01UserCmdControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        CreateUserRequest request = new CreateUserRequest(
            "John Doe",
            "john@example.com"
        );

        // When
        ResponseEntity<CreateUserResponse> response = restTemplate.postForEntity(
            "/api/v1/users",
            request,
            CreateUserResponse.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getUserId());
    }
}
```

### Frontend Testing

**Factory Unit Test:**

```typescript
import { describe, it, expect } from 'vitest';
import { UserViewModelFactory } from './UserViewModelFactory';

describe('UserViewModelFactory', () => {
  it('should transform DTO to ViewModel correctly', () => {
    // Given
    const dto: UserDto = {
      id: '1',
      first_name: 'John',
      last_name: 'Doe',
      role_list: ['ADMIN'],
      status: 'ACTIVE'
    };

    // When
    const viewModel = UserViewModelFactory.createFromDTO(dto);

    // Then
    expect(viewModel.fullName).toBe('John Doe');
    expect(viewModel.isAdmin).toBe(true);
    expect(viewModel.displayStatus).toBe('在職');
  });
});
```

**Component Test:**

```typescript
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { LoginForm } from './LoginForm';

describe('LoginForm', () => {
  it('should call onSubmit when form is submitted', async () => {
    // Given
    const handleSubmit = vi.fn();
    render(<LoginForm onSubmit={handleSubmit} />);

    // When
    fireEvent.change(screen.getByLabelText('Email'), {
      target: { value: 'test@example.com' }
    });
    fireEvent.change(screen.getByLabelText('Password'), {
      target: { value: 'password123' }
    });
    fireEvent.click(screen.getByRole('button', { name: 'Login' }));

    // Then
    expect(handleSubmit).toHaveBeenCalledWith({
      email: 'test@example.com',
      password: 'password123'
    });
  });
});
```

---

## 📚 Important Files Reference

### Essential Design Documents

| File | Purpose | When to Read |
|:---|:---|:---|
| `spec/系統架構設計文件.md` | Overall architecture, DDD layers, tech stack | Starting development, architecture questions |
| `spec/系統架構設計文件_命名規範.md` | Naming conventions for all components | Creating any new files/classes |
| `spec/系統實作合規性檢查報告.md` | Implementation compliance check against specs | Before starting new features, reviewing progress |
| `spec/系統設計書與需求分析書合規性檢查報告.md` | Design vs requirements compliance | Validating design decisions |
| `spec/系統設計書命名規範合規性檢查.md` | Naming convention compliance check | Ensuring consistent naming |
| `spec/{NN}_{Service}服務系統設計書.md` | Individual service design, API specs, domain models | Implementing features for that service |
| `spec/logic_spec/*.md` | Complex business logic specifications | Implementing payroll, attendance, insurance features |
| `frontend/TEST_SUMMARY.md` | TDD test summary, progress tracking | Understanding test coverage, writing new tests |

### Development Guidelines

| File | Purpose |
|:---|:---|
| `backend/架構說明與開發規範.md` | Backend quick reference guide |
| `frontend/架構說明與開發規範.md` | Frontend quick reference guide |
| `Fluent-Query-Engine.md` | **Querydsl 查詢引擎規範** - 新功能持久層技術選擇指引 |
| `SA/人力資源暨專案管理系統_正式需求規格書.md` | Original business requirements |
| `SA/PM需求審查報告.md` | PM review findings |

### Configuration Files

| File | Purpose |
|:---|:---|
| `backend/pom.xml` | Maven parent POM |
| `backend/hrms-{service}/pom.xml` | Individual service dependencies |
| `frontend/package.json` | NPM dependencies |
| `frontend/tsconfig.json` | TypeScript configuration |
| `frontend/vite.config.ts` | Vite build configuration |
| `frontend/vitest.config.ts` | Vitest test configuration |

---

## 🚨 Common Pitfalls to Avoid

### ❌ Don't: Mix Layers

```java
// WRONG - Domain layer depends on Infrastructure
public class User {
    @Autowired
    private UserRepository userRepository;  // ❌ NO framework dependencies in Domain
}
```

### ✅ Do: Keep Domain Pure

```java
// CORRECT - Domain is pure, Repository injected in Application layer
public class User {
    // Pure POJO, no framework dependencies
    private UserId id;
    private Email email;

    public static User create(String name, Email email) { ... }
}

@Service
public class CreateUserServiceImpl {
    @Autowired
    private IUserRepository userRepository;  // ✅ Injected in Application layer
}
```

### ❌ Don't: Skip Factory in Frontend

```typescript
// WRONG
const { data } = useQuery(...);
return <div>{data.first_name}</div>;  // ❌ Direct API data usage
```

### ✅ Do: Always Use Factory

```typescript
// CORRECT
const { data } = useQuery(...);
const viewModel = UserViewModelFactory.createFromDTO(data);  // ✅
return <div>{viewModel.fullName}</div>;
```

### ❌ Don't: Hardcode Service Injection

```java
// WRONG
@RestController
public class HR01UserCmdController {
    @Autowired
    private CreateUserServiceImpl createUserService;  // ❌ Breaks dynamic resolution
}
```

### ✅ Do: Use Base Controller Methods

```java
// CORRECT
@RestController
public class HR01UserCmdController extends CommandBaseController {
    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(...) {
        return ResponseEntity.ok(execCommand(request, currentUser));  // ✅
    }
}
```

---

## 🎓 Learning Resources

### Understanding the Codebase

1. **Start with:** `README.md` - Project overview
2. **Then read:** `spec/系統架構設計文件.md` - Architecture understanding
3. **Study:** `spec/系統架構設計文件_命名規範.md` - Naming patterns
4. **Review:** Individual service design docs for feature implementation

### Key Architectural Concepts

- **DDD (Domain-Driven Design):** Business logic in Domain layer, orchestration in Application layer
- **CQRS:** Separate read/write paths for scalability
- **Event Sourcing:** Domain events for service communication
- **Microservices:** Independent deployable services per domain
- **Saga Pattern:** Distributed transaction coordination (see Payroll service)

---

## 📞 Contact & Support

**Project Manager:** PM
**System Analyst:** SA
**Last Document Update:** 2025-12-09

---

## 🔄 Changelog

| Version | Date | Changes |
|:---|:---|:---|
| 3.1 | 2025-12-09 | **Major Update:** Updated implementation status (9 frontend features complete), added compliance reports, updated microservices table with actual progress, improved git workflow documentation, added TEST_SUMMARY.md reference |
| 3.0 | 2025-12-08 | Initial CLAUDE.md creation with comprehensive AI assistant guidance |
| 2.0 | 2025-12-07 | All 14 services design completed |
| 1.0 | 2025-12-03 | Initial architecture design |

---

**Remember:** This is a highly structured, enterprise-grade system. Always follow the design documents, naming conventions, and architectural patterns. When in doubt, check the spec documents first!
