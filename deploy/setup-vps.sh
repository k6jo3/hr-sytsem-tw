#!/bin/bash
# VPS 初始設置腳本
# 在全新 VPS 上執行一次即可
# 用法：bash setup-vps.sh

set -e

echo "=== HRMS VPS 初始化設置 ==="

# 1. 安裝 Docker
if ! command -v docker &> /dev/null; then
    echo ">>> 安裝 Docker..."
    curl -fsSL https://get.docker.com | sh
    sudo usermod -aG docker $USER
    echo "Docker 已安裝，請重新登入讓群組生效"
fi

# 2. 安裝 Docker Compose Plugin
if ! docker compose version &> /dev/null; then
    echo ">>> 安裝 Docker Compose Plugin..."
    sudo apt-get update && sudo apt-get install -y docker-compose-plugin
fi

# 3. Clone 專案
if [ ! -d ~/hr-system-2 ]; then
    echo ">>> Clone 專案..."
    git clone https://github.com/k6jo3/hr-system-2.git ~/hr-system-2
fi

cd ~/hr-system-2

# 4. 建立 .env
if [ ! -f .env ]; then
    echo ">>> 建立 .env..."
    cp .env.example .env
    # 產生隨機 JWT Secret
    JWT_SECRET=$(openssl rand -base64 48)
    sed -i "s|your-256-bit-secret.*|${JWT_SECRET}|" .env
    echo "請編輯 .env 設定資料庫密碼等參數"
fi

# 5. 防火牆
echo ">>> 設定防火牆（開放 80, 443）..."
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 22/tcp
sudo ufw --force enable

echo ""
echo "=== 設置完成 ==="
echo "後續步驟："
echo "  1. 編輯 .env 設定密碼"
echo "  2. docker compose -f docker-compose.prod.yml up -d"
echo "  3. (選用) 設定域名 DNS 指向此 VPS IP"
