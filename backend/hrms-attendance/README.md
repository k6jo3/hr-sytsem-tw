# HRMS Attendance Service (HR03)

## 服務說明

Attendance 服務 - 負責處理attendance相關業務邏輯

## 架構

採用 DDD (領域驅動設計) 四層架構：

```
com.company.hrms.attendance/
├── api/                 # Interface Layer (介面層)
│   ├── controller/      # REST Controllers
│   ├── request/         # Request DTOs
│   └── response/        # Response DTOs
├── application/         # Application Layer (應用層)
│   ├── service/         # Use Case Services
│   └── factory/         # Service Factories
├── domain/              # Domain Layer (領域層)
│   ├── model/
│   │   ├── aggregate/   # Aggregate Roots
│   │   ├── entity/      # Entities
│   │   └── valueobject/ # Value Objects
│   ├── service/         # Domain Services
│   ├── repository/      # Repository Interfaces
│   └── event/           # Domain Events
└── infrastructure/      # Infrastructure Layer (基礎設施層)
    ├── repository/      # Repository Implementations
    ├── dao/             # Data Access Objects
    ├── po/              # Persistence Objects
    └── mapper/          # MyBatis Mappers
```

## 端口

- 服務端口: 8083
- 資料庫: hrms_attendance

## 啟動

```bash
mvn spring-boot:run
```

## API 文檔

Swagger UI: http://localhost:8083/swagger-ui.html
