import react from '@vitejs/plugin-react';
import path from 'path';
import { fileURLToPath } from 'url';
import { defineConfig } from 'vite';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
      '@features': path.resolve(__dirname, './src/features'),
      '@shared': path.resolve(__dirname, './src/shared'),
      '@pages': path.resolve(__dirname, './src/pages'),
      '@store': path.resolve(__dirname, './src/store'),
    },
  },
  server: {
    port: 5173,
    proxy: {
      // HR01 IAM 服務 (port 8081)
      '/api/v1/auth': { target: 'http://localhost:8081', changeOrigin: true },
      '/api/v1/users': { target: 'http://localhost:8081', changeOrigin: true },
      '/api/v1/roles': { target: 'http://localhost:8081', changeOrigin: true },
      '/api/v1/permissions': { target: 'http://localhost:8081', changeOrigin: true },
      // HR02 組織員工服務 (port 8082)
      '/api/v1/employees': { target: 'http://localhost:8082', changeOrigin: true },
      '/api/v1/organizations': { target: 'http://localhost:8082', changeOrigin: true },
      '/api/v1/departments': { target: 'http://localhost:8082', changeOrigin: true },
      // HR03 考勤服務 (port 8083)
      '/api/v1/attendance': { target: 'http://localhost:8083', changeOrigin: true },
      '/api/v1/leave': { target: 'http://localhost:8083', changeOrigin: true },
      '/api/v1/overtime': { target: 'http://localhost:8083', changeOrigin: true },
      '/api/v1/shifts': { target: 'http://localhost:8083', changeOrigin: true },
      // HR04 薪資服務 (port 8084)
      '/api/v1/payslips': { target: 'http://localhost:8084', changeOrigin: true },
      '/api/v1/payroll-runs': { target: 'http://localhost:8084', changeOrigin: true },
      '/api/v1/salary-structures': { target: 'http://localhost:8084', changeOrigin: true },
      '/api/v1/payroll-item-definitions': { target: 'http://localhost:8084', changeOrigin: true },
      // HR05 保險服務 (port 8085)
      '/api/v1/insurance': { target: 'http://localhost:8085', changeOrigin: true },
      // HR06 專案服務 (port 8086)
      '/api/v1/projects': { target: 'http://localhost:8086', changeOrigin: true },
      '/api/v1/customers': { target: 'http://localhost:8086', changeOrigin: true },
      '/api/v1/tasks': { target: 'http://localhost:8086', changeOrigin: true },
      // HR07 工時服務 (port 8087)
      '/api/v1/timesheets': { target: 'http://localhost:8087', changeOrigin: true },
      // HR08 績效服務 (port 8088)
      '/api/v1/performance': { target: 'http://localhost:8088', changeOrigin: true },
      // HR09 招募服務 (port 8089)
      '/api/v1/recruitment': { target: 'http://localhost:8089', changeOrigin: true },
      // HR10 訓練服務 (port 8090)
      '/api/v1/training': { target: 'http://localhost:8090', changeOrigin: true },
      // HR11 簽核服務 (port 8091)
      '/api/v1/workflow': { target: 'http://localhost:8091', changeOrigin: true },
      // HR12 通知服務 (port 8092)
      '/api/v1/notifications': { target: 'http://localhost:8092', changeOrigin: true },
      // HR13 文件服務 (port 8093)
      '/api/v1/documents': { target: 'http://localhost:8093', changeOrigin: true },
      // HR14 報表服務 (port 8094)
      '/api/v1/reports': { target: 'http://localhost:8094', changeOrigin: true },
    }
  }
});
