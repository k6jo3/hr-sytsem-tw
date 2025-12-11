# 🎯 Debug 環境配置總結

## ✅ 已完成的配置

### 1. **VS Code Debug 配置**

已建立以下配置檔案：

```
.vscode/
├── launch.json     ✅ Debug 啟動配置
├── tasks.json      ✅ 建置任務配置
└── settings.json   ✅ 工作區設定
```

**可用的 Debug 配置**：
- 🟢 **Debug IAM Service** - 單獨啟動後端
- 🟢 **Debug Frontend (Chrome)** - 單獨啟動前端（Chrome）
- 🟢 **Debug Frontend (Edge)** - 單獨啟動前端（Edge）
- 🟢 **Full Stack Debug** - 同時啟動前後端

---

### 2. **前端代理配置**

**已更新 `vite.config.ts`**：
```typescript
server: {
  port: 5173,
  proxy: {
    '/api': {
      target: 'http://localhost:8081',  // 代理到後端
      changeOrigin: true
    }
  }
}
```

**已建立環境變數 `.env.development`**：
```env
VITE_API_BASE_URL=http://localhost:8081
VITE_APP_TITLE=HRMS 人力資源管理系統
```

---

### 3. **Docker Compose 配置**

**已建立 `docker-compose.dev.yml`**：

包含服務：
- ✅ PostgreSQL 15（Port 5432）
- ✅ Redis 7（Port 6379）
- ✅ pgAdmin 4（Port 5050）- Web 資料庫管理介面
- ⚠️ Kafka（預設註解，需要時取消註解）

---

## 🚀 快速啟動（3 種方式）

### **方式 1：一鍵啟動（推薦）** ⭐

```bash
# 1. 啟動基礎設施（資料庫 + Redis）
docker-compose -f docker-compose.dev.yml up -d

# 2. 在 VS Code 按 F5，選擇 "Full Stack Debug"
```

✅ 最簡單，適合日常開發

---

### **方式 2：逐步啟動**

```bash
# 1. 啟動資料庫
docker-compose -f docker-compose.dev.yml up -d postgres redis

# 2. 初始化資料庫（首次執行）
docker exec -i hrms-postgres psql -U hrms_user -d hrms_iam < backend/hrms-iam/src/main/resources/db/schema.sql
docker exec -i hrms-postgres psql -U hrms_user -d hrms_iam < backend/hrms-iam/src/main/resources/db/data.sql

# 3. 在 VS Code Debug 面板選擇 "Debug IAM Service"，按 F5

# 4. 開啟新終端，啟動前端
cd frontend
npm run dev
```

✅ 適合需要單獨控制前後端的情況

---

### **方式 3：使用 VS Code Tasks**

在 VS Code 命令面板（Ctrl+Shift+P）執行：

1. `Tasks: Run Task` → `docker: start postgres`
2. `Tasks: Run Task` → `docker: start redis`
3. 按 F5 選擇 "Full Stack Debug"

✅ 適合不想使用命令列的情況

---

## 🔍 測試登入功能

### **預設管理員帳號**

| 欄位 | 值 |
|:---|:---|
| Username | `admin` |
| Password | `Admin@123` |
| Email | `admin@example.com` |
| Role | `SYSTEM_ADMIN` |

### **測試步驟**

1. **啟動環境**（選擇上述任一方式）
2. **開啟瀏覽器** → http://localhost:5173
3. **登入頁面輸入**：
   - Username: `admin`
   - Password: `Admin@123`
4. **設定中斷點測試**：

   **後端**（Java）：
   ```java
   // LoginServiceImpl.java
   @Override
   public LoginResponse execCommand(LoginRequest req, ...) {
       // ⬅️ 這裡設定中斷點
       User user = userRepository.findByUsername(req.getUsername())...
   }
   ```

   **前端**（TypeScript）：
   ```typescript
   // useLogin.ts
   const response = await AuthApi.login(credentials);  // ⬅️ 這裡設定中斷點
   ```

---

## 🎯 請求流程圖

```
前端                         Vite Proxy              後端
┌──────────────┐             ┌──────────┐            ┌─────────────┐
│              │             │          │            │             │
│  React       │  API 請求   │  Port    │  轉發請求  │  Spring     │
│  Port 5173   │ ─────────> │  5173    │ ────────> │  Port 8081  │
│              │             │          │            │             │
│  /api/v1/    │             │  Proxy   │            │  /api/v1/   │
│  auth/login  │             │  設定    │            │  auth/login │
│              │             │          │            │             │
│              │  <─────────  │          │ <────────  │             │
│              │   回傳結果   │          │  回傳結果  │             │
└──────────────┘             └──────────┘            └─────────────┘
                                                             │
                                                             │ JDBC
                                                             ▼
                                                      ┌─────────────┐
                                                      │ PostgreSQL  │
                                                      │ Port 5432   │
                                                      └─────────────┘
```

---

## 🛠️ 管理工具

### **資料庫管理（pgAdmin）**

啟動後訪問：http://localhost:5050

**登入資訊**：
- Email: `admin@hrms.com`
- Password: `admin123`

**新增伺服器連線**：
1. 右鍵 Servers → Create → Server
2. General Tab：
   - Name: `HRMS Local`
3. Connection Tab：
   - Host: `postgres`（容器內）或 `localhost`（本機）
   - Port: `5432`
   - Database: `hrms_iam`
   - Username: `hrms_user`
   - Password: `hrms_password`

---

### **API 測試工具**

#### **選項 1：Swagger UI**（推薦）

啟動後端後訪問：http://localhost:8081/swagger-ui.html

✅ 自動產生，包含所有 API
✅ 可以直接測試
✅ 包含完整的參數說明

#### **選項 2：VS Code Extension - Thunder Client**

1. 安裝 Thunder Client 擴充套件
2. 建立新請求：

```http
POST http://localhost:8081/api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "Admin@123"
}
```

#### **選項 3：使用 curl**

```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@123"}'
```

---

## 🐛 疑難排解

### **常見問題 1：後端無法連接資料庫**

**症狀**：
```
org.postgresql.util.PSQLException: Connection refused
```

**檢查清單**：
```bash
# 1. 確認容器運行
docker ps | grep postgres

# 2. 查看容器日誌
docker logs hrms-postgres

# 3. 測試連線
docker exec -it hrms-postgres psql -U hrms_user -d hrms_iam -c "SELECT 1;"

# 4. 檢查 application.yml 設定
# 確認 DB_HOST, DB_PORT, DB_NAME, DB_USERNAME, DB_PASSWORD
```

---

### **常見問題 2：前端 CORS 錯誤**

**症狀**：
```
Access to XMLHttpRequest blocked by CORS policy
```

**解決方案**：

✅ **已配置 Vite Proxy**，通常不會遇到此問題

如果直接調用後端（不經過 Proxy），需要確認：

1. 前端使用相對路徑（`/api/v1/...`）而非絕對路徑
2. 後端允許 CORS（檢查 `SecurityConfig.java`）

---

### **常見問題 3：中斷點不生效**

**後端（Java）**：
- ✅ 確認使用 Debug 模式（F5），不是 Run 模式
- ✅ 重新編譯：`Ctrl+Shift+B` 或 `mvn compile`
- ✅ 檢查 `launch.json` 中 `mainClass` 正確

**前端（TypeScript）**：
- ✅ Source Maps 已啟用（Vite 預設開啟）
- ✅ Chrome DevTools → Sources 面板檢查檔案
- ✅ 嘗試在瀏覽器 DevTools 中設定中斷點

---

### **常見問題 4：Docker 容器無法啟動**

**症狀**：
```
Error response from daemon: Conflict. The container name "/hrms-postgres" is already in use
```

**解決方案**：
```bash
# 停止並移除舊容器
docker stop hrms-postgres hrms-redis hrms-pgadmin
docker rm hrms-postgres hrms-redis hrms-pgadmin

# 或使用 docker-compose
docker-compose -f docker-compose.dev.yml down

# 重新啟動
docker-compose -f docker-compose.dev.yml up -d
```

---

## 📚 相關文件

| 文件 | 說明 |
|:---|:---|
| `QUICK_START_DEBUG.md` | 📖 詳細 Debug 教學 |
| `執行清單.md` | 📋 開發進度追蹤 |
| `CLAUDE.md` | 🤖 AI 助理開發指南 |
| `.vscode/launch.json` | ⚙️ Debug 配置 |
| `docker-compose.dev.yml` | 🐳 Docker 環境配置 |

---

## 🎓 快捷鍵速查

| 功能 | 快捷鍵 |
|:---|:---|
| 啟動 Debug | `F5` |
| 停止 Debug | `Shift+F5` |
| 重新啟動 | `Ctrl+Shift+F5` |
| Step Over | `F10` |
| Step Into | `F11` |
| Step Out | `Shift+F11` |
| 設定中斷點 | `F9` |
| 開啟終端 | `` Ctrl+` `` |
| 命令面板 | `Ctrl+Shift+P` |

---

## ✅ 環境檢查清單

使用前確認：

- [ ] Java 17+ 已安裝（`java -version`）
- [ ] Node.js 18+ 已安裝（`node -v`）
- [ ] Docker Desktop 已安裝並運行（`docker ps`）
- [ ] VS Code 已安裝必要擴充套件（Java、Vite、Debugger）
- [ ] Docker 容器已啟動（`docker ps | grep hrms`）
- [ ] 前端依賴已安裝（`cd frontend && npm install`）
- [ ] 資料庫已初始化（執行 schema.sql 和 data.sql）

---

## 🎉 開始開發吧！

所有配置已完成，您現在可以：

1. **按 F5** 啟動 Full Stack Debug
2. **開啟 http://localhost:5173** 測試前端
3. **使用預設帳號登入** 測試功能
4. **設定中斷點** 追蹤程式流程
5. **查看 Swagger** (http://localhost:8081/swagger-ui.html) 測試 API

**祝開發順利！** 🚀
