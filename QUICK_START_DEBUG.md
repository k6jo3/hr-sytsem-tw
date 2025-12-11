# 🚀 VS Code Debug 快速開始指南

## 📋 前置準備

### 1. 安裝必要軟體

| 軟體 | 用途 | 下載連結 |
|:---|:---|:---|
| **Java 17+** | 後端運行環境 | https://adoptium.net/ |
| **Node.js 18+** | 前端運行環境 | https://nodejs.org/ |
| **PostgreSQL 15** | 資料庫 | https://www.postgresql.org/ |
| **Redis** (可選) | 快取 | https://redis.io/ |
| **Docker Desktop** (推薦) | 容器化部署 | https://www.docker.com/ |

### 2. VS Code 擴充套件

必裝：
- ✅ **Extension Pack for Java** (微軟官方)
- ✅ **Debugger for Java** (微軟官方)
- ✅ **Spring Boot Extension Pack**
- ✅ **Vite** (Vite 支援)
- ✅ **ESLint** (程式碼檢查)
- ✅ **Prettier** (程式碼格式化)

可選：
- **Thunder Client** (API 測試)
- **GitLens** (Git 增強)
- **Database Client** (資料庫管理)

---

## 🎯 快速啟動步驟

### 方法 1：使用 Docker（推薦）

#### **步驟 1：啟動資料庫**

```bash
# 在專案根目錄執行
cd F:\javawork\hr-sytsem-2

# 啟動 PostgreSQL
docker run -d \
  --name hrms-postgres \
  -e POSTGRES_DB=hrms_iam \
  -e POSTGRES_USER=hrms_user \
  -e POSTGRES_PASSWORD=hrms_password \
  -p 5432:5432 \
  postgres:15

# 啟動 Redis（可選）
docker run -d \
  --name hrms-redis \
  -p 6379:6379 \
  redis:7-alpine

# 檢查容器狀態
docker ps
```

#### **步驟 2：初始化資料庫**

```bash
# 使用 psql 連接並執行初始化腳本
docker exec -it hrms-postgres psql -U hrms_user -d hrms_iam

# 在 psql 中執行
\i /path/to/backend/hrms-iam/src/main/resources/db/schema.sql
\i /path/to/backend/hrms-iam/src/main/resources/db/data.sql
\q
```

或者直接在 VS Code 使用 Database Client 擴充套件執行 SQL 腳本。

#### **步驟 3：安裝前端依賴**

```bash
cd frontend
npm install
```

#### **步驟 4：啟動 Debug**

在 VS Code 中：

1. **按 F5** 或點擊左側 Debug 面板
2. 選擇 **"Full Stack Debug"** 配置
3. 點擊綠色播放按鈕 ▶️

這會同時啟動：
- 🟢 後端 IAM 服務（Port 8081）
- 🟢 前端 React Dev Server（Port 5173）
- 🟢 Chrome 瀏覽器並開啟 http://localhost:5173

---

### 方法 2：本機安裝資料庫

#### **步驟 1：安裝並啟動 PostgreSQL**

1. 下載並安裝 PostgreSQL 15
2. 使用 pgAdmin 或命令列建立資料庫：

```sql
CREATE DATABASE hrms_iam;
CREATE USER hrms_user WITH PASSWORD 'hrms_password';
GRANT ALL PRIVILEGES ON DATABASE hrms_iam TO hrms_user;
```

3. 執行初始化腳本：
   - `backend/hrms-iam/src/main/resources/db/schema.sql`
   - `backend/hrms-iam/src/main/resources/db/data.sql`

#### **步驟 2：（可選）安裝並啟動 Redis**

```bash
# Windows: 使用 Chocolatey
choco install redis-64

# 或使用 WSL2 安裝
wsl --install
# 在 WSL 中：
sudo apt update
sudo apt install redis-server
sudo service redis-server start
```

#### **步驟 3-4：同方法 1**

---

## 🔍 Debug 使用方式

### **1. 後端 Debug**

#### **設定中斷點（Breakpoint）**

在 Java 檔案中，點擊行號左側設定紅點：

```java
// UserRepositoryImpl.java
@Override
public Optional<User> findByUsername(String username) {
    UserPO po = userDAO.selectByUsername(username);  // ⬅️ 點擊這行左側設定中斷點
    return Optional.ofNullable(po).map(this::toDomain);
}
```

#### **Debug 面板功能**

| 按鈕 | 快捷鍵 | 功能 |
|:---|:---|:---|
| ▶️ Continue | F5 | 繼續執行到下一個中斷點 |
| ⤵️ Step Over | F10 | 執行當前行，不進入函式 |
| ⬇️ Step Into | F11 | 進入函式內部 |
| ⬆️ Step Out | Shift+F11 | 跳出當前函式 |
| 🔄 Restart | Ctrl+Shift+F5 | 重新啟動 Debug |
| ⏹️ Stop | Shift+F5 | 停止 Debug |

#### **查看變數**

- **Variables 面板**：查看當前作用域的所有變數
- **Watch 面板**：監控特定表達式（如 `user.getEmail().getValue()`）
- **Call Stack**：查看函式調用堆疊

#### **測試 API**

使用內建的 HTTP Client 或 Thunder Client：

```http
### 測試登入 API
POST http://localhost:8081/api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "Admin@123"
}
```

---

### **2. 前端 Debug**

#### **設定中斷點**

在 TypeScript/React 檔案中設定中斷點：

```typescript
// useLogin.ts
export const useLogin = () => {
  const handleLogin = async (credentials: LoginCredentials) => {
    const response = await AuthApi.login(credentials);  // ⬅️ 設定中斷點
    return response;
  };
};
```

#### **瀏覽器 DevTools**

Chrome/Edge 會自動開啟，可以：
- 🔍 **Sources 面板**：查看 Source Maps
- 📊 **Network 面板**：監控 API 請求
- 🎨 **Elements 面板**：檢查 DOM 結構
- ⚠️ **Console 面板**：查看錯誤訊息

#### **React DevTools**

安裝 React DevTools 擴充套件後可以：
- 🌲 查看組件樹
- 📦 檢查 Props 和 State
- 🔄 追蹤重新渲染

---

## 🧪 測試登入功能

### **使用預設管理員帳號**

根據 `data.sql` 初始化腳本：

| 欄位 | 值 |
|:---|:---|
| **Username** | `admin` |
| **Password** | `Admin@123` |
| **Email** | `admin@example.com` |
| **Role** | `SYSTEM_ADMIN` |

### **測試流程**

1. **啟動 Debug**（F5）
2. **開啟瀏覽器** → http://localhost:5173
3. **進入登入頁面**（應該是 HR01-P01）
4. **輸入帳號密碼**：
   - Username: `admin`
   - Password: `Admin@123`
5. **點擊登入按鈕**

### **Debug 追蹤路徑**

前端 → 後端的完整調用鏈：

```
1. LoginForm.tsx (React Component)
   ├─ onClick 事件觸發
   └─ 調用 useLogin hook

2. useLogin.ts (Custom Hook)
   ├─ 調用 AuthApi.login()
   └─ axios.post('/api/v1/auth/login')

3. Vite Proxy 轉發
   ├─ http://localhost:5173/api/v1/auth/login
   └─ → http://localhost:8081/api/v1/auth/login

4. HR01AuthCmdController.java (Spring Controller)
   ├─ @PostMapping("/login")
   └─ 調用 execCommand()

5. LoginServiceImpl.java (Application Service)
   ├─ 查找 User
   ├─ 驗證密碼
   ├─ 生成 JWT Token
   └─ 返回 LoginResponse

6. 前端接收回應
   ├─ UserViewModelFactory.createFromDTO()
   ├─ 儲存 Token 到 localStorage
   └─ 跳轉到首頁
```

---

## 🐛 常見問題排除

### **問題 1：後端啟動失敗 - 資料庫連線錯誤**

```
Caused by: org.postgresql.util.PSQLException: Connection refused
```

**解決方案**：
1. 確認 PostgreSQL 已啟動：`docker ps` 或檢查本機服務
2. 檢查連線參數是否正確（Host、Port、使用者、密碼）
3. 測試連線：`psql -h localhost -U hrms_user -d hrms_iam`

---

### **問題 2：前端無法連接後端 - CORS 錯誤**

```
Access to XMLHttpRequest at 'http://localhost:8081' has been blocked by CORS policy
```

**解決方案**：
1. 確認 `vite.config.ts` 中 proxy 已正確配置
2. 檢查後端 `SecurityConfig.java` 中是否允許 CORS
3. 如果直接調用後端，需要在後端添加 CORS 配置：

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true);
    }
}
```

---

### **問題 3：中斷點不生效**

**後端**：
- ✅ 確認是在 Debug 模式下啟動（F5），不是 Run 模式
- ✅ 檢查 `launch.json` 中 `mainClass` 是否正確
- ✅ 確認代碼已重新編譯（Ctrl+Shift+B）

**前端**：
- ✅ 確認 Source Maps 已啟用（`vite.config.ts` 預設開啟）
- ✅ 使用 Chrome/Edge 的 Sources 面板確認檔案已載入
- ✅ 嘗試在瀏覽器 DevTools 中直接設定中斷點

---

### **問題 4：熱重載不生效**

**後端**：
- 安裝 Spring Boot DevTools 依賴（已在 pom.xml 中）
- VS Code 中啟用 Auto Save

**前端**：
- Vite 預設支援 HMR（Hot Module Replacement）
- 如果不生效，嘗試：`Ctrl+C` 停止，重新 `npm run dev`

---

## 📝 其他有用的 VS Code 快捷鍵

| 功能 | Windows/Linux | macOS |
|:---|:---|:---|
| 開啟命令面板 | Ctrl+Shift+P | Cmd+Shift+P |
| 快速開啟檔案 | Ctrl+P | Cmd+P |
| 切換側邊欄 | Ctrl+B | Cmd+B |
| 開啟終端機 | Ctrl+` | Cmd+` |
| 多游標編輯 | Ctrl+Alt+↓/↑ | Opt+Cmd+↓/↑ |
| 全域搜尋 | Ctrl+Shift+F | Cmd+Shift+F |
| 重構重新命名 | F2 | F2 |
| 轉到定義 | F12 | F12 |

---

## 🎓 推薦學習資源

- **VS Code Java Debug**：https://code.visualstudio.com/docs/java/java-debugging
- **Vite Proxy 配置**：https://vitejs.dev/config/server-options.html#server-proxy
- **Spring Boot DevTools**：https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.devtools

---

**祝 Debug 順利！** 🎉
