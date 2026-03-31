# 前端 Multi-stage Dockerfile

# ===== Stage 1: Build =====
FROM node:20-alpine AS builder

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci --ignore-scripts

COPY . .

# Azure 部署時透過 build-arg 設定 API base URL 指向 Gateway external URL
# Docker Compose 不傳此參數，預設為 /api/v1（走 nginx proxy）
ARG VITE_API_BASE_URL
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}

RUN npm run build

# ===== Stage 2: Nginx =====
FROM nginx:alpine

# 複製自訂 Nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
