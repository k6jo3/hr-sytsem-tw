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

# Docker Compose 用 http://hrms-gateway:8080，Azure 用 https://hrms-gateway
ENV GATEWAY_URL=http://hrms-gateway:8080

CMD ["/bin/sh", "-c", "sed -i \"s|\\${GATEWAY_URL}|$GATEWAY_URL|g\" /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'"]
