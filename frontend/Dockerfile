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

# 延遲 5 秒等 DNS resolver ready 後再啟動 nginx
CMD ["/bin/sh", "-c", "sleep 5 && nginx -g 'daemon off;'"]
