# HR System 3.0 - Backend Architecture

## 📋 Project Structure

```
backend/
├── hrms-common/              # 共用模組
│   ├── controller/           # Base Controllers
│   ├── service/              # Service Interfaces
│   ├── factory/              # Service Factories
│   ├── aspect/               # AOP Aspects
│   └── exception/            # Common Exceptions
│
├── hrms-iam/                 # HR01 - IAM Service
├── hrms-organization/        # HR02 - Organization Service
├── hrms-attendance/          # HR03 - Attendance Service
├── hrms-payroll/             # HR04 - Payroll Service
├── hrms-insurance/           # HR05 - Insurance Service
├── hrms-project/             # HR06 - Project Service
├── hrms-timesheet/           # HR07 - Timesheet Service
├── hrms-performance/         # HR08 - Performance Service
├── hrms-recruitment/         # HR09 - Recruitment Service
├── hrms-training/            # HR10 - Training Service
├── hrms-workflow/            # HR11 - Workflow Service
├── hrms-notification/        # HR12 - Notification Service
├── hrms-document/            # HR13 - Document Service
├── hrms-reporting/           # HR14 - Reporting Service
│
├── pom.xml                   # Parent POM
└── README.md                 # This file
```

## 🏗️ Microservices Architecture

### 14 Microservices

| Code | Service | Port | Database | Description |
|:---:|:---|:---:|:---|:---|
| **01** | IAM | 8081 | hrms_iam | Authentication & Authorization |
| **02** | Organization | 8082 | hrms_organization | Employee & Org Management |
| **03** | Attendance | 8083 | hrms_attendance | Time & Attendance |
| **04** | Payroll | 8084 | hrms_payroll | Salary Calculation |
| **05** | Insurance | 8085 | hrms_insurance | Labor & Health Insurance |
| **06** | Project | 8086 | hrms_project | Project Management |
| **07** | Timesheet | 8087 | hrms_timesheet | Timesheet Tracking |
| **08** | Performance | 8088 | hrms_performance | Performance Review |
| **09** | Recruitment | 8089 | hrms_recruitment | Recruitment Management |
| **10** | Training | 8090 | hrms_training | Training & Development |
| **11** | Workflow | 8091 | hrms_workflow | Workflow Engine |
| **12** | Notification | 8092 | hrms_notification | Notification Service |
| **13** | Document | 8093 | hrms_document | Document Management |
| **14** | Reporting | 8094 | hrms_reporting | Reporting & Analytics |

## 🎯 DDD (Domain-Driven Design) Architecture

每個微服務遵循嚴格的四層架構：

```
com.company.hrms.{service}/
├── api/                      # Interface Layer (介面層)
│   ├── controller/           # REST Controllers (CQRS分離)
│   │   ├── HR{DD}{Screen}CmdController.java
│   │   └── HR{DD}{Screen}QryController.java
│   ├── request/              # Request DTOs
│   └── response/             # Response DTOs
│
├── application/              # Application Layer (應用層)
│   ├── service/              # Use Case Services
│   │   └── {Verb}{Noun}ServiceImpl.java
│   └── factory/              # Service Factories
│
├── domain/                   # Domain Layer (領域層 - CORE)
│   ├── model/
│   │   ├── aggregate/        # Aggregate Roots
│   │   ├── entity/           # Entities
│   │   └── valueobject/      # Value Objects
│   ├── service/              # Domain Services
│   ├── repository/           # Repository Interfaces
│   │   └── I{Noun}Repository.java
│   └── event/                # Domain Events
│       └── {Aggregate}{PastVerb}Event.java
│
└── infrastructure/           # Infrastructure Layer (基礎設施層)
    ├── repository/           # Repository Implementations
    │   └── {Noun}RepositoryImpl.java
    ├── dao/                  # Data Access Objects
    │   └── {Noun}DAO.java
    ├── po/                   # Persistence Objects
    │   └── {Noun}PO.java
    └── mapper/               # MyBatis Mappers
        └── {Noun}Mapper.java
```

## 🔧 Technology Stack

| Category | Technology | Version |
|:---|:---|:---|
| Framework | Spring Boot | 3.1.5 |
| Microservices | Spring Cloud | 2023.0.0 |
| Service Discovery | Eureka | - |
| API Gateway | Spring Cloud Gateway | - |
| Database | PostgreSQL | 15+ |
| ORM | MyBatis | 3.0.3 |
| Cache | Redis | 7+ |
| Message Queue | Kafka | 3+ |
| API Documentation | SpringDoc OpenAPI | 2.2.0 |
| Object Mapping | MapStruct | 1.5.5 |

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 15+
- Redis 7+
- Kafka 3+
- Eureka Server (optional for local dev)

### Build All Services

```bash
mvn clean install
```

### Run Individual Service

```bash
cd hrms-iam
mvn spring-boot:run
```

### Run All Services

```bash
# Terminal 1
cd hrms-iam && mvn spring-boot:run

# Terminal 2
cd hrms-organization && mvn spring-boot:run

# ... repeat for other services
```

## 📝 Naming Conventions

### Controller Naming

| Type | Format | Example | HTTP Methods |
|:---|:---|:---|:---|
| Command | `HR{DD}{Screen}CmdController` | `HR01UserCmdController` | POST, PUT, DELETE |
| Query | `HR{DD}{Screen}QryController` | `HR01UserQryController` | GET |

### Service Naming

| Element | Format | Example |
|:---|:---|:---|
| Application Service | `{Verb}{Noun}ServiceImpl` | `CreateUserServiceImpl` |
| Domain Service | `{Event}DomainService` | `PasswordEncryptionDomainService` |
| Bean Name | `{verb}{Noun}ServiceImpl` | `createUserServiceImpl` |

### Repository Naming

| Element | Format | Example |
|:---|:---|:---|
| Interface | `I{Noun}Repository` | `IUserRepository` |
| Implementation | `{Noun}RepositoryImpl` | `UserRepositoryImpl` |
| DAO | `{Noun}DAO` | `UserDAO` |

### Request/Response Naming

| Element | Format | Example |
|:---|:---|:---|
| Request | `{Verb}{Noun}Request` | `CreateUserRequest` |
| Response | `{Noun}{Type}Response` | `UserDetailResponse` |

### Domain Event Naming

| Element | Format | Example |
|:---|:---|:---|
| Domain Event | `{Aggregate}{PastVerb}Event` | `UserCreatedEvent` |

## 🎯 Key Design Patterns

### 1. CQRS (Command Query Responsibility Segregation)

Controllers 分離為 Command 和 Query：

```java
// Command Controller - 寫入操作
@RestController
@RequestMapping("/api/v1/users")
public class HR01UserCmdController extends CommandBaseController {
    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(...) { }
}

// Query Controller - 讀取操作
@RestController
@RequestMapping("/api/v1/users")
public class HR01UserQryController extends QueryBaseController {
    @GetMapping
    public ResponseEntity<UserListResponse> getUserList(...) { }
}
```

### 2. Service Factory Pattern

Controller方法名自動對應Service Bean名稱：

```java
// Controller method: createUser
// → Resolves to bean: "createUserServiceImpl"
@PostMapping
public ResponseEntity<CreateUserResponse> createUser(...) {
    return ResponseEntity.ok(execCommand(request, currentUser));
}
```

### 3. Repository Pattern

Domain Layer定義介面，Infrastructure Layer實作：

```java
// Domain Layer - Interface
public interface IUserRepository {
    void save(User user);
    Optional<User> findById(UserId id);
}

// Infrastructure Layer - Implementation
@Repository
public class UserRepositoryImpl implements IUserRepository {
    // Implementation with MyBatis
}
```

### 4. Event-Driven Architecture

使用Kafka進行服務間非同步通訊：

```java
// Publishing events
@Service
public class CreateEmployeeServiceImpl {
    @Autowired
    private EventPublisher eventPublisher;
    
    public void execute() {
        // Business logic
        eventPublisher.publish(new EmployeeCreatedEvent(...));
    }
}

// Subscribing to events
@Service
public class EmployeeEventListener {
    @KafkaListener(topics = "employee.created")
    public void handleEmployeeCreated(EmployeeCreatedEvent event) {
        // Handle event
    }
}
```

## 🗄️ Database Setup

### Create Databases

```sql
-- Create all databases
CREATE DATABASE hrms_iam;
CREATE DATABASE hrms_organization;
CREATE DATABASE hrms_attendance;
CREATE DATABASE hrms_payroll;
CREATE DATABASE hrms_insurance;
CREATE DATABASE hrms_project;
CREATE DATABASE hrms_timesheet;
CREATE DATABASE hrms_performance;
CREATE DATABASE hrms_recruitment;
CREATE DATABASE hrms_training;
CREATE DATABASE hrms_workflow;
CREATE DATABASE hrms_notification;
CREATE DATABASE hrms_document;
CREATE DATABASE hrms_reporting;

-- Create user
CREATE USER hrms_user WITH PASSWORD 'hrms_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE hrms_iam TO hrms_user;
GRANT ALL PRIVILEGES ON DATABASE hrms_organization TO hrms_user;
-- ... repeat for all databases
```

## 📚 API Documentation

Each service provides Swagger UI at:
```
http://localhost:{port}/swagger-ui.html
```

Examples:
- IAM: http://localhost:8081/swagger-ui.html
- Organization: http://localhost:8082/swagger-ui.html
- Attendance: http://localhost:8083/swagger-ui.html

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

### Run Tests for Specific Service

```bash
cd hrms-iam
mvn test
```

## 📖 Documentation

- **Architecture Guide**: `架構說明與開發規範.md`
- **System Design**: `../spec/系統架構設計文件.md`
- **Naming Conventions**: `../spec/系統架構設計文件_命名規範.md`
- **AI Assistant Guide**: `../CLAUDE.md`

## 🔒 Security

- JWT-based authentication
- RBAC (Role-Based Access Control)
- Multi-tenancy support
- API rate limiting (via Gateway)
- Input validation
- SQL injection prevention (MyBatis)

## 📊 Monitoring

- Spring Boot Actuator
- Prometheus metrics
- Distributed tracing with Zipkin
- Centralized logging with ELK

## 🚨 Common Pitfalls

### ❌ Don't: Mix Domain and Infrastructure

```java
// WRONG - Domain depends on Infrastructure
public class User {
    @Autowired
    private UserRepository repository;  // ❌
}
```

### ✅ Do: Keep Domain Pure

```java
// CORRECT - Pure domain model
public class User {
    private UserId id;
    private Email email;
    
    public static User create(String name, Email email) {
        // Pure business logic
        return new User(...);
    }
}
```

### ❌ Don't: Put Business Logic in Application Layer

```java
// WRONG
@Service
public class CreateUserServiceImpl {
    public void execute() {
        if (email.contains("@")) { }  // ❌ Business logic here
    }
}
```

### ✅ Do: Business Logic in Domain Layer

```java
// CORRECT - Domain Layer
public class Email {
    private final String value;
    
    public Email(String value) {
        if (!isValid(value)) {  // ✅ Validation in domain
            throw new InvalidEmailException();
        }
        this.value = value;
    }
}
```

## 📞 Support

For questions or issues, please refer to:
- System Design Documents: `../spec/`
- Development Guidelines: `架構說明與開發規範.md`
- CLAUDE.md for AI assistance

---

**Version:** 3.0  
**Last Updated:** 2025-12-08  
**Architecture:** Microservices + DDD + CQRS + Event-Driven
