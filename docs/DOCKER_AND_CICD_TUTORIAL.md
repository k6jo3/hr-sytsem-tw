# Docker 與 CI/CD 教學手冊

> **適用對象：** 想理解「為什麼這樣設計」的開發者，而非只是照抄指令的使用者。
>
> 本文件以 HRMS 專案（14 個 Spring Boot 微服務 + React 前端）為實例，
> 從每一行設定的「意圖」出發，解釋 Docker 容器化與 CI/CD 自動化的完整流程。

---

## 目錄

1. [專案架構總覽](#1-專案架構總覽)
2. [Dockerfile 解析 — 怎麼把程式變成容器](#2-dockerfile-解析--怎麼把程式變成容器)
   - 2.1 [後端 Dockerfile（Multi-stage Build）](#21-後端-dockerfilemulti-stage-build)
   - 2.2 [前端 Dockerfile](#22-前端-dockerfile)
   - 2.3 [.dockerignore 的作用](#23-dockerignore-的作用)
3. [Docker Compose 解析 — 怎麼把 18 個容器串起來](#3-docker-compose-解析--怎麼把-18-個容器串起來)
   - 3.1 [三份 Compose 檔案的定位](#31-三份-compose-檔案的定位)
   - 3.2 [docker-compose.yml（本地完整啟動）逐段解析](#32-docker-composeyml本地完整啟動逐段解析)
   - 3.3 [docker-compose.dev.yml（開發環境）](#33-docker-composedevyml開發環境)
   - 3.4 [docker-compose.prod.yml（生產部署）](#34-docker-composeprodyml生產部署)
4. [Nginx 反向代理 — 前端怎麼跟後端通訊](#4-nginx-反向代理--前端怎麼跟後端通訊)
5. [資料庫初始化 — 容器啟動時自動建立 14 個 DB](#5-資料庫初始化--容器啟動時自動建立-14-個-db)
6. [環境變數設計 — 一份程式碼跑在多個環境](#6-環境變數設計--一份程式碼跑在多個環境)
7. [CI/CD Pipeline 解析 — 從 push 到上線](#7-cicd-pipeline-解析--從-push-到上線)
   - 7.1 [ci-cd.yml（主流程）](#71-ci-cdyml主流程)
   - 7.2 [azure-cd.yml（Azure 部署）](#72-azure-cdymlazure-部署)
   - 7.3 [GitHub Secrets 設定清單](#73-github-secrets-設定清單)
8. [部署方案比較 — VPS vs Azure Container Apps](#8-部署方案比較--vps-vs-azure-container-apps)
9. [資源調優 — 怎麼在 8GB RAM 跑 18 個容器](#9-資源調優--怎麼在-8gb-ram-跑-18-個容器)
10. [常見問題 FAQ](#10-常見問題-faq)

---

## 1. 專案架構總覽

```
使用者瀏覽器
    │
    ▼
┌─────────────────┐
│  Nginx (:3000)  │  ← 靜態檔案 + 反向代理
└────────┬────────┘
         │ /api/*
         ▼
┌─────────────────┐
│ Gateway (:8080) │  ← 路由分發 + JWT 驗證
└────────┬────────┘
         │ 依 URL 路由
    ┌────┼────┬────┬─── ... ───┐
    ▼    ▼    ▼    ▼           ▼
  IAM  Org  Att  Pay  ...  Reporting
 :8081 :8082 :8083 :8084     :8094
    │    │    │    │           │
    └────┼────┼────┼─── ... ──┘
         ▼    ▼    ▼
    ┌─────────────────┐
    │ PostgreSQL :5432 │  ← 14 個獨立資料庫
    └─────────────────┘
    ┌──────┐  ┌───────┐
    │Redis │  │ Kafka │  ← 快取 + 事件驅動
    │:6379 │  │ :9092 │
    └──────┘  └───────┘
```

**為什麼需要容器化？**

這個專案有 18 個元件（14 微服務 + Gateway + PostgreSQL + Redis + Kafka）。如果每個都手動安裝、設定、啟動，光是環境準備就要半天，而且每台機器的設定都可能不同。Docker 把每個元件連同它的執行環境一起打包，確保「我的電腦跑得動 = 任何電腦都跑得動」。

---

## 2. Dockerfile 解析 — 怎麼把程式變成容器

### 2.1 後端 Dockerfile（Multi-stage Build）

**檔案位置：** `backend/Dockerfile`

```dockerfile
# ===== Stage 1: Build =====
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
```

**為什麼用 Multi-stage？** 最終 image 只需要 JRE 和 JAR 檔，不需要 Maven、原始碼、編譯產物。如果把所有東西塞在一個 stage，image 會從 ~150MB 膨脹到 ~800MB。Multi-stage 讓你在第一階段編譯，第二階段只取結果。

```dockerfile
# 先複製 pom.xml 利用 Docker layer cache
COPY pom.xml .
COPY hrms-common/pom.xml hrms-common/pom.xml
COPY hrms-gateway/pom.xml hrms-gateway/pom.xml
# ... 其餘 13 個模組的 pom.xml

RUN mvn dependency:go-offline -B -q 2>/dev/null || true
```

**為什麼先複製 pom.xml 再複製原始碼？** 這是 **Docker Layer Cache** 的核心技巧。Docker 每一行指令都會產生一個 layer，如果該行的輸入沒變，就直接用快取。pom.xml 很少改動，所以依賴下載這步通常會命中快取。如果反過來先 `COPY . .` 再 `mvn dependency:go-offline`，只要任何 `.java` 檔改了，依賴就得重新下載——白白浪費 5-10 分鐘。

```dockerfile
COPY . .

# Docker 容器內已有 JDK 21，替換 Windows 專用的 Toolchains 為容器內路徑
RUN echo '<?xml version="1.0" encoding="UTF-8"?>...<jdkHome>/opt/java/openjdk</jdkHome>...' > .mvn/toolchains.xml
```

**為什麼要覆寫 toolchains.xml？** 本機開發的 `toolchains.xml` 裡 `jdkHome` 指向 `${env.JAVA_HOME}`，在 Docker 容器內環境變數語法不被 Maven Toolchains 直接支援，所以在構建時直接覆寫為容器內的固定路徑 `/opt/java/openjdk`。

```dockerfile
ARG SERVICE=hrms-iam
RUN mvn package -pl ${SERVICE} -am -DskipTests -B -q
```

**為什麼用 `ARG` 而不是寫死服務名？** 因為我們有 14 個微服務 + 1 個 Gateway，共用同一個 Dockerfile。構建時透過 `--build-arg SERVICE=hrms-attendance` 指定要編譯哪個模組。`-pl` 是 Maven 的 project list 參數，`-am` 表示同時編譯它依賴的模組（如 `hrms-common`）。`-DskipTests` 是因為 CI 的測試已經在前一個 Job 跑過了，不需要在 Docker build 時再跑一次。

```dockerfile
# ===== Stage 2: Runtime =====
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S hrms && adduser -S hrms -G hrms
```

**為什麼用 `jre-alpine` 而不是 `jdk`？** 執行時不需要編譯器（javac），JRE 就夠了。Alpine Linux 是精簡版 Linux（~5MB），比 Ubuntu (~75MB) 小很多。

**為什麼建立專用使用者？** 安全最佳實踐。預設的 root 使用者權限太大，如果容器被攻破，攻擊者就有 root 權限。用專用使用者 `hrms` 執行，即使被攻破也只有有限權限。

```dockerfile
ARG SERVICE=hrms-iam
COPY --from=builder /app/${SERVICE}/target/*.jar app.jar

ENV JAVA_OPTS="-Xmx256m -Xms128m -XX:+UseG1GC -XX:MaxMetaspaceSize=128m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

**為什麼 JVM 參數用環境變數？** 這樣可以在 docker-compose 裡針對不同服務調整記憶體，而不需要重新 build image。例如 IAM 服務做的事比較多，可以設 `-Xmx384m`；Reporting 服務較輕量，`-Xmx192m` 就夠。

**為什麼用 `sh -c` 而不是直接 `java -jar`？** 因為 `ENTRYPOINT ["java", "-jar", "app.jar"]` 是 exec form，不會展開環境變數 `$JAVA_OPTS`。必須透過 shell (`sh -c`) 來解析環境變數。

### 2.2 前端 Dockerfile

**檔案位置：** `frontend/Dockerfile`

```dockerfile
# ===== Stage 1: Build =====
FROM node:20-alpine AS builder
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci --ignore-scripts
COPY . .
RUN npm run build

# ===== Stage 2: Nginx =====
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

**為什麼前端也要兩階段？** React 的 `npm run build` 會把 TypeScript/JSX 編譯成靜態 HTML/CSS/JS。部署時只需要這些靜態檔案 + Nginx 伺服器，不需要 Node.js 和 `node_modules`（~500MB）。最終 image 只有 ~30MB。

**為什麼用 `npm ci` 而不是 `npm install`？** `npm ci` 嚴格依照 `package-lock.json` 安裝，確保每次都是一模一樣的版本。`npm install` 可能會更新 lock 檔案，導致「明明沒改 code 但 build 出來不一樣」的問題。`--ignore-scripts` 跳過 postinstall 腳本，避免安全風險。

### 2.3 .dockerignore 的作用

```
.git
node_modules/
dist/
target/
*.log
.env
```

**為什麼需要 .dockerignore？** `COPY . .` 會複製整個目錄。如果沒有 .dockerignore，`.git`（可能幾百 MB）、`node_modules`（~500MB）、`target`（編譯產物）都會被送進 Docker build context，拖慢構建速度。更重要的是，`.env` 可能包含機密資訊，絕不能進入 image。

---

## 3. Docker Compose 解析 — 怎麼把 18 個容器串起來

### 3.1 三份 Compose 檔案的定位

| 檔案 | 用途 | 啟動什麼 |
|:---|:---|:---|
| `docker-compose.dev.yml` | 本地開發 | 只啟動 PostgreSQL + Redis（+ pgAdmin 可選） |
| `docker-compose.yml` | 本地完整啟動 | 全部 18 個容器，從原始碼 build |
| `docker-compose.prod.yml` | 生產/VPS 部署 | 全部容器，從 ghcr.io 拉取已建好的 image |

**為什麼要三份？** 因為三個場景的需求不同：

- **開發時**：後端用 IDE 啟動（熱更新、Debug），只需要資料庫和快取
- **本地驗證**：想看「全部跑起來是什麼樣子」，從原始碼 build
- **生產部署**：絕不能在伺服器上 build（耗時、不安全），只拉取 CI 建好的 image

### 3.2 docker-compose.yml（本地完整啟動）逐段解析

#### YAML 錨點（Anchor）— 消除重複

```yaml
x-service-common: &service-common
  restart: unless-stopped
  deploy:
    resources:
      limits:
        memory: 384M
  networks:
    - hrms-network

x-service-env: &service-env
  SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE: 3
  SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE: 1
  EUREKA_CLIENT_ENABLED: "false"
  SPRING_JPA_HIBERNATE_DDL_AUTO: update
  SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:29092
```

**為什麼用 YAML 錨點？** 14 個微服務的設定有 90% 相同（都要連同一個 Redis、同一個 Kafka、同樣的連線池設定）。如果每個服務都寫一次，就是 14 份重複設定——改一個參數要改 14 處。`&service-common` 定義模板，`<<: *service-common` 引用它，只需寫一次。

**為什麼連線池壓到 max=3, min=1？** 14 個服務 × 預設 10 連線 = 140 個連線，PostgreSQL 的 `max_connections=300` 很快就不夠用。壓到 3 後是 14 × 3 = 42 個連線，留下大量餘裕。demo 場景不需要高併發，3 個連線綽綽有餘。

**為什麼 `EUREKA_CLIENT_ENABLED: "false"`？** 本專案的服務發現有兩種模式：Eureka（動態）和直接 URL（靜態）。Docker 環境裡容器名稱就是 hostname（如 `hrms-iam`），不需要額外的服務發現，所以關閉 Eureka 省記憶體。

**為什麼 `DDL_AUTO: update`？** JPA 啟動時會自動比對 Entity 和資料庫 schema，缺少的欄位/表會自動建立。demo 環境這樣最方便——不需要手寫 migration 腳本。（正式環境會改用 Flyway/Liquibase。）

#### PostgreSQL 設定

```yaml
postgres:
  image: postgres:15-alpine
  environment:
    POSTGRES_DB: hrms_iam
    POSTGRES_USER: hrms_user
    POSTGRES_PASSWORD: hrms_password
    POSTGRES_INITDB_ARGS: "--encoding=UTF8"
  volumes:
    - postgres_data:/var/lib/postgresql/data
    - ./docker/init-databases.sql:/docker-entrypoint-initdb.d/01-init-databases.sql
    - ./backend/hrms-iam/src/main/resources/db/schema-local.sql:/docker-entrypoint-initdb.d/02-schema-iam.sql
    - ./docker/seed-iam.sql:/docker-entrypoint-initdb.d/03-seed-iam.sql
  command: postgres -c max_connections=300 -c shared_buffers=64MB -c work_mem=2MB
```

**`POSTGRES_DB: hrms_iam`** — 容器啟動時自動建立第一個資料庫 `hrms_iam`。

**`docker-entrypoint-initdb.d/` 的魔法** — PostgreSQL 官方 image 的機制：容器**第一次啟動**時，會依檔名順序執行這個目錄下的 `.sql` 和 `.sh` 檔案。我們利用這個機制：
  1. `01-init-databases.sql`：建立其餘 13 個資料庫
  2. `02-schema-iam.sql`：建立 IAM 服務的表結構
  3. `03-seed-iam.sql`：塞入預設帳號、角色、權限

**為什麼 `shared_buffers=64MB` 而不是預設的 128MB？** 預設值是為正式環境設計的。14 個微服務的 demo 場景，資料量極小，64MB 就夠了。省下的記憶體留給 Java 服務。

#### Kafka 設定

```yaml
kafka:
  image: confluentinc/cp-kafka:7.5.0
  environment:
    KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092
    KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
    KAFKA_HEAP_OPTS: "-Xmx256m -Xms128m"
  deploy:
    resources:
      limits:
        memory: 512M
```

**為什麼要兩個 listener？** `kafka:29092` 是給容器內部的服務連線用的（容器之間透過 Docker 網路溝通），`localhost:9092` 是給本機 IDE 開發時直接連線用的。如果只有一個，就只能二選一。

**為什麼 `REPLICATION_FACTOR: 1`？** 正式環境通常 3 個 broker、replication 3，確保資料不會因為某台掛掉而遺失。但 demo 只有 1 個 broker，設 3 會啟動失敗。

**為什麼 Kafka 給 512M，其他服務給 384M？** Kafka 是 Java 寫的，本身就吃記憶體。256M heap + OS overhead 加起來至少要 384M，留點餘裕給 512M。微服務每個只需要 256M heap，384M 的 limit 就夠了。

#### 服務依賴關係

```yaml
hrms-iam:
  depends_on:
    postgres:
      condition: service_healthy
    redis:
      condition: service_healthy
    kafka:
      condition: service_started
```

**為什麼 PostgreSQL 和 Redis 用 `service_healthy`，Kafka 只用 `service_started`？**

`service_healthy` 表示要等到 healthcheck 通過才啟動。PostgreSQL 的 healthcheck 是 `pg_isready`，確保資料庫真的能接受連線了。如果不等，Spring Boot 啟動時連不上資料庫就會直接 crash。

Kafka 沒有設定 healthcheck，所以只能用 `service_started`（容器啟動了就算）。Kafka 啟動比較慢，但 Spring Boot 的 Kafka 連線有自動重試機制，所以不等也沒關係。

#### 資源限制

```yaml
deploy:
  resources:
    limits:
      memory: 384M
```

**為什麼要設記憶體限制？** 如果不設，Java 的 GC 會越吃越多記憶體，14 個服務可能把整台機器的記憶體吃光。設了 limit 後，Docker 會在容器超過 384M 時直接 kill 它，JVM 的 `-Xmx256m` 確保 heap 不超過 256M，加上 metaspace 和 native memory 約 100-128M，384M 剛好夠用。

### 3.3 docker-compose.dev.yml（開發環境）

```yaml
services:
  postgres:
    image: postgres:15-alpine
    ports:
      - "5432:5432"
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
  pgadmin:
    image: dpage/pgadmin4:latest
    ports:
      - "5050:80"
```

**為什麼開發環境只需要這些？** 因為開發時後端是用 IDE（IntelliJ）直接啟動的，可以 debug、熱部署、看 console log。只需要資料庫和快取是「外部依賴」才用 Docker 跑。

**pgAdmin 是什麼？** 一個網頁版的 PostgreSQL 管理介面，打開 `localhost:5050` 就能看到資料庫內容、執行 SQL、瀏覽表結構。開發時比用 psql 指令方便。

**為什麼 Kafka 被註解掉？** 開發時通常只開發單一服務，不需要跨服務事件通訊。需要時取消註解就好。

### 3.4 docker-compose.prod.yml（生產部署）

```yaml
hrms-iam:
  image: ${DOCKER_REGISTRY:-ghcr.io}/${DOCKER_REPO:-k6jo3/hr-system-2}/hrms-iam:${IMAGE_TAG:-latest}
  environment: &backend-env
    DB_HOST: postgres
    DB_USERNAME: ${DB_USERNAME:-hrms_user}
    DB_PASSWORD: ${DB_PASSWORD:-hrms_password}
    JWT_SECRET: ${JWT_SECRET:-your-256-bit-secret-key...}
    SPRING_PROFILES_ACTIVE: prod
```

**跟 docker-compose.yml 的關鍵差異：**

1. **沒有 `build`，只有 `image`** — 生產環境不從原始碼 build，直接拉取 CI/CD 建好的 image。這確保「測試過的 image = 部署的 image」，不會因為環境差異導致「CI 能過但部署掛了」。

2. **環境變數用 `${VAR:-default}` 語法** — 機密值（DB 密碼、JWT 密鑰）不寫死在檔案裡，而是從 `.env` 或系統環境變數讀取。`.env` 不進版控（在 `.gitignore` 裡），所以密碼不會被 push 到 GitHub。

3. **`SPRING_PROFILES_ACTIVE: prod`** — 告訴 Spring Boot 載入 `application-prod.yml` 設定檔，用正式環境的參數（如更嚴格的日誌、外部 DB 連線等）。

---

## 4. Nginx 反向代理 — 前端怎麼跟後端通訊

**檔案位置：** `frontend/nginx.conf`

```nginx
# API 反向代理 — 透過 Gateway 路由到各微服務
location /api/ {
    proxy_pass http://hrms-gateway:8080;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection "upgrade";
}

# SPA Fallback — React Router 支援
location / {
    root   /usr/share/nginx/html;
    try_files $uri $uri/ /index.html;
}
```

**為什麼需要反向代理？**

瀏覽器直接呼叫 `http://gateway:8080/api/...` 會遇到 **CORS（跨域）** 問題——瀏覽器不允許 `localhost:3000` 的頁面去呼叫 `localhost:8080` 的 API。反向代理讓前端和 API 都從同一個域名（`:3000`）出去，繞過 CORS。

**`proxy_set_header` 在做什麼？**

- `X-Real-IP`：把使用者的真實 IP 傳給後端（否則後端只看到 Nginx 容器的 IP）
- `X-Forwarded-Proto`：告訴後端原始請求是 HTTP 還是 HTTPS
- `Upgrade` + `Connection`：支援 WebSocket 連線（通知服務的即時推播用）

**`try_files $uri $uri/ /index.html` 是什麼意思？**

React 是 SPA（Single Page Application），所有路由都由前端 JavaScript 處理。當使用者直接訪問 `http://domain/attendance/list` 時，Nginx 的檔案系統裡沒有 `/attendance/list` 這個檔案，如果不設定 fallback 就會回 404。`try_files` 的意思是：「先找有沒有這個檔案，沒有的話就回傳 `index.html`，讓 React Router 去處理路由。」

---

## 5. 資料庫初始化 — 容器啟動時自動建立 14 個 DB

**檔案位置：** `docker/init-databases.sql`

```sql
-- HR02 組織員工服務
CREATE DATABASE hrms_organization;
-- HR03 考勤管理服務
CREATE DATABASE hrms_attendance;
-- ... 共 13 個

GRANT ALL PRIVILEGES ON DATABASE hrms_organization TO hrms_user;
-- ... 共 13 個
```

**為什麼每個微服務一個資料庫？**

這是微服務架構的 **Database per Service** 原則。每個服務擁有自己的資料庫，好處是：

1. **獨立部署**：改了考勤服務的 schema，不會影響薪資服務
2. **故障隔離**：考勤的 DB 掛了，其他服務照常運作
3. **技術自由**：未來某個服務可以換成 MongoDB 或 Elasticsearch

**為什麼 `hrms_iam` 不在這個腳本裡？** 因為 PostgreSQL 容器的 `POSTGRES_DB: hrms_iam` 環境變數已經會自動建立它。`docker-entrypoint-initdb.d` 是在預設 DB 建立之後才執行的。

**`seed-iam.sql` 在做什麼？** 塞入系統能運作的最小資料：
- 預設租戶（多租戶架構需要）
- 5 個內建角色（系統管理員、HR 管理員、主管、員工、專案經理）
- 31 個系統權限
- 5 個預設帳號（方便 demo 登入）
- 系統參數（登入失敗上限、帳號鎖定時間等）

---

## 6. 環境變數設計 — 一份程式碼跑在多個環境

**檔案位置：** `.env.example`

```ini
DOCKER_REGISTRY=ghcr.io
DOCKER_REPO=k6jo3/hr-system-2
IMAGE_TAG=latest
DB_USERNAME=hrms_user
DB_PASSWORD=hrms_password
JWT_SECRET=your-256-bit-secret-key...
HTTP_PORT=80
```

**為什麼不把密碼直接寫在 docker-compose 裡？**

1. `docker-compose.yml` 會被 push 到 GitHub（公開），密碼就洩漏了
2. 不同環境（開發/測試/正式）的密碼不同，寫死就只能服務一個環境
3. `.env` 在 `.gitignore` 裡，不會進版控

**使用方式：**
```bash
# 部署時
cp .env.example .env
vim .env  # 填入正式環境的密碼和金鑰
docker compose -f docker-compose.prod.yml up -d
```

**前端的環境變數為什麼不同？**

```ini
# frontend/.env.development
VITE_API_BASE_URL=/api/v1

# frontend/.env.production
VITE_API_BASE_URL=/api/v1
```

前端用 Vite 打包，環境變數前綴必須是 `VITE_`，而且是 **build time** 變數——打包時會被替換成字串常量。所以不像後端可以在啟動時讀取環境變數，前端的環境變數在 `npm run build` 時就決定了。

---

## 7. CI/CD Pipeline 解析 — 從 push 到上線

### 7.1 ci-cd.yml（主流程）

**觸發條件：**
```yaml
on:
  push:
    branches: [main]
  pull_request:
    branches: [main]
```

push 到 main 或開 PR 到 main 時自動觸發。PR 只跑測試（不 build image、不部署），push 到 main 才走完整流程。

#### Pipeline 流程圖

```
push to main
    │
    ├──────────────────────┐
    ▼                      ▼
┌──────────┐      ┌──────────────┐
│ Frontend │      │ Backend Test │
│ Build &  │      │ (mvn test)   │
│ Test     │      │              │
└────┬─────┘      └──────┬───────┘
     │                   │
     │   兩者都通過才繼續   │
     └─────────┬─────────┘
               ▼
     ┌─────────────────┐
     │ Docker Build ×15 │  ← 14 微服務 + 1 前端，並行構建
     │ Push to ghcr.io  │
     └────────┬────────┘
              ▼
     ┌─────────────────┐
     │  Deploy to VPS  │  ← SSH 到伺服器拉取 image
     └─────────────────┘
```

#### Job 1: 前端測試與建置

```yaml
frontend:
  runs-on: ubuntu-latest
  defaults:
    run:
      working-directory: frontend
  steps:
    - uses: actions/checkout@v4
    - uses: actions/setup-node@v4
      with:
        node-version: 20
        cache: npm
        cache-dependency-path: frontend/package-lock.json
    - run: npm ci
    - run: npm run test -- --run
    - run: npm run build
```

**`cache: npm`** — GitHub Actions 會快取 `node_modules`，下次跑時如果 `package-lock.json` 沒變就直接用快取，省掉 `npm ci` 的 30-60 秒。

**`npm run test -- --run`** — `--run` 告訴 Vitest 跑一次就結束（而不是進入 watch 模式）。CI 環境沒有人在看 watch，必須跑完就退出。

#### Job 2: 後端測試

```yaml
backend-test:
  runs-on: ubuntu-latest
  steps:
    - uses: actions/setup-java@v4
      with:
        distribution: temurin
        java-version: 21
        cache: maven
    - run: mvn test -B -q
```

**`-B`** = batch mode（不顯示下載進度條），**`-q`** = quiet（減少輸出）。CI 不需要互動式輸出，安靜跑完就好。

**`setup-java` 會自動設定 `JAVA_HOME`**，所以 `toolchains.xml` 裡的 `${env.JAVA_HOME}` 會正確解析到 CI runner 上的 JDK 路徑。

#### Job 3: Docker 建置（矩陣策略）

```yaml
docker-build:
  needs: [frontend, backend-test]
  if: github.ref == 'refs/heads/main' && github.event_name == 'push'
  strategy:
    matrix:
      service:
        - hrms-iam
        - hrms-organization
        # ... 共 14 個
```

**`needs: [frontend, backend-test]`** — 前端和後端測試都過了才建 image。不能讓測試失敗的程式碼進入 Docker image。

**`if: ... && github.event_name == 'push'`** — PR 不需要 build image，只有真正 merge 到 main 才 build。省 CI 時間和 registry 空間。

**`strategy.matrix`** — GitHub Actions 的並行策略。14 個服務會同時在 14 台 runner 上 build，而不是排隊一個一個來。14 個服務各要 5-10 分鐘，並行後總共只需要 10 分鐘。

```yaml
- uses: docker/login-action@v3
  with:
    registry: ghcr.io
    username: ${{ github.actor }}
    password: ${{ secrets.GITHUB_TOKEN }}
```

**`GITHUB_TOKEN`** 是 GitHub 自動提供的臨時 token，不需要手動建立。它有權限推送到 `ghcr.io`（GitHub Container Registry），這就是為什麼不需要額外設定 registry 的帳密。

```yaml
- uses: docker/build-push-action@v5
  with:
    tags: |
      ghcr.io/${{ env.REPO }}/${{ matrix.service }}:latest
      ghcr.io/${{ env.REPO }}/${{ matrix.service }}:${{ github.sha }}
```

**為什麼打兩個 tag？**
- `latest`：永遠指向最新版，方便快速部署
- `${{ github.sha }}`（如 `a1b2c3d`）：指向特定 commit，方便回滾。如果 latest 有問題，可以 `docker pull ...:a1b2c3d` 拉回上一個版本

#### Job 4: 部署到 VPS

```yaml
deploy:
  needs: [docker-build, docker-frontend]
  steps:
    - uses: appleboy/ssh-action@v1
      with:
        host: ${{ secrets.VPS_HOST }}
        username: ${{ secrets.VPS_USER }}
        key: ${{ secrets.VPS_SSH_KEY }}
        script: |
          cd ~/hr-system-2
          git pull origin main
          echo ${{ secrets.GITHUB_TOKEN }} | docker login ghcr.io -u ${{ github.actor }} --password-stdin
          docker compose -f docker-compose.prod.yml pull
          docker compose -f docker-compose.prod.yml up -d
          docker image prune -f
```

**整個部署就這幾行：**
1. SSH 進 VPS
2. `git pull` 拉最新的 `docker-compose.prod.yml`（可能更新了環境變數或新增了服務）
3. 登入 ghcr.io（VPS 需要認證才能拉取 private image）
4. `docker compose pull` 拉取所有最新 image
5. `docker compose up -d` 重啟有變更的容器（沒變的不會重啟）
6. `docker image prune -f` 清理舊 image 釋放磁碟空間

### 7.2 azure-cd.yml（Azure 部署）

```yaml
on:
  workflow_dispatch:  # 手動觸發
```

**為什麼 Azure 部署是手動的？** VPS 部署是自動的（push 到 main 就部署），但 Azure 是付費環境，不希望每次 push 都自動花錢啟動容器。需要 demo 時才手動觸發。

```yaml
- name: Deploy backend services
  run: |
    SERVICES=(hrms-iam hrms-organization ...)
    for SERVICE in "${SERVICES[@]}"; do
      az containerapp update \
        --name $SERVICE \
        --resource-group ${{ env.RESOURCE_GROUP }} \
        --image "ghcr.io/${{ env.REPO }}/${SERVICE}:latest" \
        2>/dev/null || echo "${SERVICE} not yet created, skipping..."
    done
```

**`az containerapp update`** — 更新 Azure Container App 的 image。如果容器 app 還沒建立（第一次部署），`2>/dev/null || echo "skipping"` 會跳過而不是讓整個 pipeline 失敗。

**為什麼不用 `az containerapp create`？** 因為 create 需要大量參數（CPU、記憶體、ingress、環境變數），第一次建立通常透過 Azure Portal 或 Terraform 手動完成。之後的更新只需要換 image。

### 7.3 GitHub Secrets 設定清單

到 GitHub → Settings → Secrets and variables → Actions 設定：

| Secret | 用途 | VPS 部署 | Azure 部署 |
|:---|:---|:---:|:---:|
| `VPS_HOST` | VPS 的 IP 或域名 | ✅ | — |
| `VPS_USER` | SSH 使用者名 | ✅ | — |
| `VPS_SSH_KEY` | SSH 私鑰（整把貼上） | ✅ | — |
| `AZURE_CREDENTIALS` | Azure Service Principal JSON | — | ✅ |
| `AZURE_RESOURCE_GROUP` | Azure 資源群組名稱 | — | ✅ |
| `AZURE_CAE_NAME` | Container Apps Environment 名稱 | — | ✅ |

**注意：`GITHUB_TOKEN` 不需要手動設定**，GitHub Actions 會自動產生。

---

## 8. 部署方案比較 — VPS vs Azure Container Apps

| | VPS（自管 VM） | Azure Container Apps |
|:---|:---|:---|
| **月費（24hr demo）** | $5-10/月（固定） | ~$5/月（按秒計費） |
| **月費（忘記關）** | 持續收費 | $0（自動 scale to zero） |
| **維運** | 要管 OS、防火牆、SSL | 零維運 |
| **擴縮容** | 手動加機器 | 自動 |
| **冷啟動** | 無（一直開著） | 有（Java ~30-60s） |
| **適合** | 長期穩定運行 | 間歇性 demo |

**本專案兩者都支援：** `ci-cd.yml` 自動部署到 VPS，需要 Azure 時手動觸發 `azure-cd.yml`。

---

## 9. 資源調優 — 怎麼在 8GB RAM 跑 18 個容器

**完整啟動的記憶體分配：**

| 元件 | 數量 | 單位記憶體 | 小計 |
|:---|:---:|:---|:---|
| Spring Boot 微服務 | 14 | 384 MB limit | ~5.2 GB |
| API Gateway | 1 | 384 MB limit | ~384 MB |
| PostgreSQL | 1 | 256 MB limit | ~256 MB |
| Redis | 1 | 96 MB limit | ~96 MB |
| Kafka | 1 | 512 MB limit | ~512 MB |
| Zookeeper | 1 | 256 MB limit | ~256 MB |
| Nginx (前端) | 1 | 64 MB limit | ~64 MB |
| **合計** | **20** | | **~6.8 GB** |

**進一步壓縮的手段：**

1. **降低 JVM heap**：`JAVA_OPTS: "-Xmx192m -Xms96m"` + memory limit 改 300M → 省 ~1.2 GB
2. **停用 Kafka**：加 `SPRING_KAFKA_ENABLED: "false"` → 省 ~768 MB（Kafka + Zookeeper）
3. **只啟動部分服務**：`docker compose up postgres redis hrms-gateway hrms-iam frontend`

---

## 10. 常見問題 FAQ

### Q: 為什麼只有一個 Dockerfile 但能 build 14 個不同的服務？

A: 透過 `ARG SERVICE=hrms-iam` 建置參數。Docker build 時指定 `--build-arg SERVICE=hrms-attendance`，Dockerfile 裡的 `${SERVICE}` 就會替換成 `hrms-attendance`，Maven 就只編譯那個模組。

### Q: 容器重啟後資料還在嗎？

A: 取決於有沒有掛 volume。`postgres_data` 和 `redis_data` 是 named volume，資料持久化在 Docker 管理的目錄裡，容器刪掉重建資料還在。只有 `docker compose down -v` 才會刪除 volume。

### Q: 為什麼本地開發不直接用 docker-compose.yml 啟動全部？

A: 可以，但 14 個 Java 服務要吃 5-6 GB RAM，而且從原始碼 build 要 10-20 分鐘。開發時通常只改一兩個服務，用 IDE 啟動那個服務就好，其他依賴用 `docker-compose.dev.yml` 提供。

### Q: CI 的 `mvn test` 不需要資料庫嗎？

A: 不需要。後端測試用 H2 內嵌資料庫（在 `application-test.yml` 設定），不依賴外部 PostgreSQL。這是為了讓測試可以在任何環境跑，包括沒有 Docker 的 CI runner。

### Q: Azure 部署的 Container App 要怎麼第一次建立？

A: 第一次需要手動建立（Azure Portal 或 CLI）：

```bash
# 建立 Container Apps Environment
az containerapp env create \
  --name hrms-env \
  --resource-group hrms-rg \
  --location eastasia

# 建立 IAM 服務（範例）
az containerapp create \
  --name hrms-iam \
  --resource-group hrms-rg \
  --environment hrms-env \
  --image ghcr.io/k6jo3/hr-system-2/hrms-iam:latest \
  --cpu 0.25 --memory 0.5Gi \
  --min-replicas 0 --max-replicas 1
```

之後每次 `azure-cd.yml` 只需要 `az containerapp update --image ...` 更新 image 就好。

### Q: 怎麼查看容器的日誌？

```bash
# 查看特定服務的日誌
docker compose logs -f hrms-iam

# 查看最近 100 行
docker compose logs --tail 100 hrms-iam

# 查看所有服務的錯誤
docker compose logs | grep -i error
```

### Q: 怎麼進入容器內部排查問題？

```bash
# 進入 PostgreSQL 容器
docker exec -it hrms-postgres psql -U hrms_user -d hrms_iam

# 進入微服務容器
docker exec -it hrms-iam sh

# 查看 Redis 內容
docker exec -it hrms-redis redis-cli
```
