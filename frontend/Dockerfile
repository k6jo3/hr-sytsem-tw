# 前端 Multi-stage Dockerfile

# ===== Stage 1: Build =====
FROM node:20-alpine AS builder

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci --ignore-scripts

COPY . .
RUN npm run build

# ===== Stage 2: Nginx =====
FROM nginx:alpine

# 複製自訂 Nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80

# 等待 Gateway DNS 可解析後再啟動 nginx（避免 DNS 快取失敗結果）
CMD ["/bin/sh", "-c", "echo 'Waiting for gateway DNS...' && for i in $(seq 1 30); do nslookup hrms-gateway >/dev/null 2>&1 && break; echo \"Retry $i...\"; sleep 2; done && echo 'Gateway resolved, starting nginx' && nginx -g 'daemon off;'"]
