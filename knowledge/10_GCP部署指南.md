# GCP 部署指南 (Google Cloud Platform Deployment Guide)

> **前置閱讀：** `06_CICD與系統部署指南.md`（Dockerfile 範本與 GitHub Actions 腳本）
> **本文件定位：** 從零開始的逐步操作指引，補充 06 文件未涵蓋的 GCP 控制台操作與常見錯誤排除

---

## 一、 前置需求確認

### 1.1 本機工具安裝

```bash
# 1. 安裝 gcloud CLI（Google Cloud SDK）
# Windows: 下載安裝包 https://cloud.google.com/sdk/docs/install
# 驗證安裝
gcloud --version

# 2. 確認 Docker 已安裝
docker --version   # 需要 24.x 以上

# 3. 確認 Java & Maven（後端建置用）
java -version      # 需要 21+
mvn -version       # 需要 3.9+
```

### 1.2 GCP 帳號與計費設定

1. 前往 https://console.cloud.google.com/ 建立帳號（需信用卡，但有 $300 免費額度）
2. 建立新 Project（建議命名：`hrms-portfolio`）
3. **必做：設定 Budget Alert**，避免意外超支：
   - GCP Console → Billing → Budgets & Alerts
   - 設定月上限 $30，80% / 100% 各發一次 Email 警示

---

## 二、 一次性 GCP 環境初始化

> **這個章節只需要做一次**，之後每次部署只需執行第四章的指令。

### Step 1：登入並設定專案

```bash
# 登入 GCP（會開啟瀏覽器）
gcloud auth login

# 設定預設專案（換成你的 Project ID）
gcloud config set project hrms-portfolio

# 確認設定正確
gcloud config list
```

### Step 2：啟用必要 API

```bash
# 一次啟用所有需要的 API（約需 1-2 分鐘）
gcloud services enable \
  run.googleapis.com \
  sqladmin.googleapis.com \
  artifactregistry.googleapis.com \
  secretmanager.googleapis.com \
  vpcaccess.googleapis.com \
  cloudresourcemanager.googleapis.com
```

### Step 3：建立 Artifact Registry（Docker Image 倉庫）

```bash
# 建立 Docker repository（選 asia-east1 台灣最近的節點）
gcloud artifacts repositories create hrms-repo \
  --repository-format=docker \
  --location=asia-east1 \
  --description="HRMS microservices images"

# 設定 Docker 認證（讓 docker push 可以上傳到 GCP）
gcloud auth configure-docker asia-east1-docker.pkg.dev
```

### Step 4：建立 Cloud SQL（PostgreSQL）

```bash
# 建立 PostgreSQL 15 實例（最小規格，約 $10/月）
# 注意：這個指令執行需要 3-5 分鐘
gcloud sql instances create hrms-db \
  --database-version=POSTGRES_15 \
  --tier=db-f1-micro \
  --region=asia-east1 \
  --storage-size=10GB \
  --storage-type=SSD \
  --no-assign-ip \
  --enable-google-private-path

# ⚠️ --no-assign-ip 表示只用 Private IP，不開 Public IP（更安全）
# ⚠️ 這會讓 Cloud Run 必須透過 VPC Connector 才能連到 DB（見 Step 6）

# 建立各服務的資料庫
gcloud sql databases create hrms_iam      --instance=hrms-db
gcloud sql databases create hrms_org      --instance=hrms-db
gcloud sql databases create hrms_att      --instance=hrms-db
# 之後其他服務照樣新增...

# 建立資料庫使用者
gcloud sql users create hrms_app \
  --instance=hrms-db \
  --password=YOUR_SECURE_PASSWORD_HERE
```

### Step 5：將密碼存入 Secret Manager

> 絕對不能把密碼放在 `application.yml` 或 GitHub！Secret Manager 是正確做法。

```bash
# 儲存 DB 密碼
echo -n "YOUR_SECURE_PASSWORD_HERE" | \
  gcloud secrets create DB_PASSWORD --data-file=-

# 儲存 JWT Secret
echo -n "YOUR_JWT_SECRET_64CHARS_MIN" | \
  gcloud secrets create JWT_SECRET --data-file=-

# 確認 Secret 已建立
gcloud secrets list
```

### Step 6：建立 VPC Connector（讓 Cloud Run 連到 Cloud SQL Private IP）

```bash
# 啟用 VPC Access API（若前面已啟用可跳過）
gcloud services enable vpcaccess.googleapis.com

# 建立 Connector（讓 Cloud Run 能進入 VPC 網路）
gcloud compute networks vpc-access connectors create hrms-connector \
  --region=asia-east1 \
  --range=10.8.0.0/28

# 確認建立成功
gcloud compute networks vpc-access connectors describe hrms-connector \
  --region=asia-east1
```

### Step 7：建立 Service Account（給 GitHub Actions 用）

```bash
# 建立 Service Account
gcloud iam service-accounts create github-deployer \
  --display-name="GitHub Actions Deployer"

# 賦予必要權限
PROJECT_ID=$(gcloud config get-value project)
SA_EMAIL="github-deployer@${PROJECT_ID}.iam.gserviceaccount.com"

gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:${SA_EMAIL}" \
  --role="roles/run.admin"

gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:${SA_EMAIL}" \
  --role="roles/artifactregistry.writer"

gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:${SA_EMAIL}" \
  --role="roles/iam.serviceAccountUser"

gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:${SA_EMAIL}" \
  --role="roles/secretmanager.secretAccessor"

# 產生 JSON Key（存到 GitHub Secrets 用）
gcloud iam service-accounts keys create github-deployer-key.json \
  --iam-account=$SA_EMAIL

# ⚠️ 這個 JSON 檔包含機敏資訊，用完後刪除本機檔案！
cat github-deployer-key.json  # 複製這個內容到 GitHub Secrets
rm github-deployer-key.json   # 刪除本機檔案
```

---

## 三、 部署單一微服務（以 IAM 為例）

### Step 1：建置 Docker Image

```bash
# 在專案根目錄執行
cd D:/git/hr-system2/hr-sytsem-2/backend

# 定義 Image URL（格式：{region}-docker.pkg.dev/{project}/{repo}/{service}:{tag}）
IMAGE_URL="asia-east1-docker.pkg.dev/hrms-portfolio/hrms-repo/hrms-iam"

# Multi-stage Build（包含 Maven 編譯）
docker build \
  -f hrms-iam/Dockerfile \
  -t ${IMAGE_URL}:latest \
  -t ${IMAGE_URL}:$(git rev-parse --short HEAD) \
  .

# 推送到 Artifact Registry
docker push ${IMAGE_URL}:latest
```

### Step 2：取得 Cloud SQL Private IP

```bash
# 查看 Cloud SQL 的 Private IP
gcloud sql instances describe hrms-db \
  --format="value(ipAddresses[0].ipAddress)"
# 記下這個 IP，如：10.23.128.3
```

### Step 3：部署到 Cloud Run

```bash
DB_PRIVATE_IP="10.23.128.3"  # 換成你的 IP

gcloud run deploy hrms-iam-service \
  --image=${IMAGE_URL}:latest \
  --region=asia-east1 \
  --platform=managed \
  --allow-unauthenticated \
  --min-instances=0 \
  --max-instances=3 \
  --memory=512Mi \
  --cpu=1 \
  --vpc-connector=hrms-connector \
  --vpc-egress=private-ranges-only \
  --set-env-vars="SPRING_PROFILES_ACTIVE=prod" \
  --set-env-vars="SPRING_DATASOURCE_URL=jdbc:postgresql://${DB_PRIVATE_IP}:5432/hrms_iam" \
  --set-env-vars="SPRING_DATASOURCE_USERNAME=hrms_app" \
  --set-secrets="SPRING_DATASOURCE_PASSWORD=DB_PASSWORD:latest" \
  --set-secrets="APP_JWT_SECRET=JWT_SECRET:latest"

# 部署完成後查看服務 URL
gcloud run services describe hrms-iam-service \
  --region=asia-east1 \
  --format="value(status.url)"
```

---

## 四、 GitHub Actions 自動部署設定

### 4.1 加入 GitHub Secrets

前往 GitHub Repo → Settings → Secrets and variables → Actions，新增：

| Secret 名稱 | 值 |
|:---|:---|
| `GCP_CREDENTIALS` | `github-deployer-key.json` 的完整內容 |
| `GCP_PROJECT_ID` | `hrms-portfolio`（你的 Project ID）|
| `DB_PRIVATE_IP` | Cloud SQL 的 Private IP |

### 4.2 workflow 檔案

> 詳細 GitHub Actions YAML 請參考 `06_CICD與系統部署指南.md`。
> 以下補充 Secret Manager 版本的環境變數注入方式：

```yaml
# 在 Deploy to Cloud Run 步驟中，改用 --set-secrets 取代明文密碼
- name: Deploy to Cloud Run
  run: |
    gcloud run deploy hrms-iam-service \
      --image=${{ env.IMAGE_URL }}:${{ github.sha }} \
      --region=asia-east1 \
      --set-secrets="SPRING_DATASOURCE_PASSWORD=DB_PASSWORD:latest" \
      --set-secrets="APP_JWT_SECRET=JWT_SECRET:latest"
  # ↑ Secret 從 Secret Manager 動態注入，GitHub Secrets 只存非機敏的 Project ID 和 SA 金鑰
```

---

## 五、 驗證部署成功

```bash
# 1. 確認服務狀態
gcloud run services list --region=asia-east1

# 2. 查看即時 Log（排錯必用）
gcloud logging read \
  "resource.type=cloud_run_revision AND resource.labels.service_name=hrms-iam-service" \
  --limit=50 \
  --format="value(textPayload)"

# 3. 測試 API
SERVICE_URL=$(gcloud run services describe hrms-iam-service \
  --region=asia-east1 --format="value(status.url)")

curl -X POST "${SERVICE_URL}/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"test123"}'
```

---

## 六、 常見錯誤與解決方法

### 錯誤 1：`PERMISSION_DENIED` 在部署時

```
ERROR: (gcloud.run.deploy) PERMISSION_DENIED: Permission 'iam.serviceaccounts.actAs' denied
```

**原因：** Service Account 缺少 `iam.serviceAccountUser` 角色。

```bash
gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:${SA_EMAIL}" \
  --role="roles/iam.serviceAccountUser"
```

---

### 錯誤 2：Cloud Run 無法連接 Cloud SQL

```
Connection refused: 10.23.128.3:5432
```

**排查順序：**
1. 確認 VPC Connector 已建立且狀態為 `READY`
2. 確認 Cloud Run 部署時有 `--vpc-connector=hrms-connector`
3. 確認 Cloud SQL 的 Private IP 沒打錯

```bash
# 檢查 VPC Connector 狀態
gcloud compute networks vpc-access connectors describe hrms-connector \
  --region=asia-east1
```

---

### 錯誤 3：Secret Manager 取不到 Secret

```
ERROR: The given data must be a string, was: <class 'NoneType'>
```

**原因：** Cloud Run 的 Service Account 沒有 `secretmanager.secretAccessor` 權限。

```bash
# 取得 Cloud Run 使用的 Service Account
gcloud run services describe hrms-iam-service \
  --region=asia-east1 \
  --format="value(spec.template.spec.serviceAccountName)"

# 補授權（把上面查到的 SA Email 填入）
gcloud secrets add-iam-policy-binding DB_PASSWORD \
  --member="serviceAccount:CLOUD_RUN_SA_EMAIL" \
  --role="roles/secretmanager.secretAccessor"
```

---

### 錯誤 4：Docker Image 推送失敗

```
unauthorized: You don't have the needed permissions
```

```bash
# 重新設定 Docker 認證
gcloud auth configure-docker asia-east1-docker.pkg.dev --quiet
```

---

### 錯誤 5：Cloud Run 啟動後立即 crash（OOM）

**原因：** Spring Boot + Java 21 預設記憶體需求比 Cloud Run 預設值高。

```bash
# 增加記憶體限制（改為 1GB）
gcloud run services update hrms-iam-service \
  --region=asia-east1 \
  --memory=1Gi
```

---

## 七、 費用控制技巧

| 技巧 | 說明 |
|:---|:---|
| `--min-instances=0` | 沒有流量時縮到 0，不計費。冷啟動約 3-5 秒（demo 可接受）|
| 只部署 3 個服務 | IAM + ORG + ATT 足夠展示，不需全部 14 個都上線 |
| Cloud SQL 選 `db-f1-micro` | 最便宜的規格，portfolio 夠用 |
| 不用 Memorystore | 成本最低 $35/月，改用 Spring Cache 的 in-memory 替代 |
| 設 Budget Alert | 防止意外超支，務必設定 |

---

## 八、 參考資源

| 資源 | 連結（請自行搜尋最新文件）|
|:---|:---|
| Cloud Run 快速入門 | 搜尋：`Cloud Run quickstart Spring Boot` |
| Cloud SQL Java 連線 | 搜尋：`Cloud SQL Java connector Spring Boot` |
| Secret Manager 使用 | 搜尋：`Secret Manager Spring Boot GCP` |
| VPC Connector 設定 | 搜尋：`Cloud Run VPC Connector private Cloud SQL` |
| gcloud CLI 完整參考 | 搜尋：`gcloud reference` |
