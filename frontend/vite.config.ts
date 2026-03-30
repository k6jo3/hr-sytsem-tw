import react from '@vitejs/plugin-react';
import path from 'path';
import { fileURLToPath } from 'url';
import { defineConfig } from 'vite';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// GitHub Pages 部署時需設定子路徑（透過 CI 環境變數 GITHUB_PAGES=true 啟用）
const isGitHubPages = process.env.GITHUB_PAGES === 'true';

// https://vite.dev/config/
export default defineConfig({
  base: isGitHubPages ? '/hr-sytsem-tw/' : '/',
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
  build: {
    chunkSizeWarningLimit: 1300,
    rollupOptions: {
      output: {
        manualChunks: {
          // 第三方核心庫
          'vendor-react': ['react', 'react-dom', 'react-router-dom'],
          'vendor-antd': ['antd', '@ant-design/icons'],
          'vendor-redux': ['@reduxjs/toolkit', 'react-redux'],
          'vendor-echarts': ['echarts'],
          'vendor-utils': ['axios', 'dayjs'],
        },
      },
    },
  },
  server: {
    port: 5173,
    // 統一透過 API Gateway 路由（port 8080）
    proxy: {
      '/api': { target: 'http://localhost:8080', changeOrigin: true },
    }
  }
});
