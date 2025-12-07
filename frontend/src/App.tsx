import { HR01LoginPage, HR02EmployeeListPage } from '@pages/index';
import { ConfigProvider } from 'antd';
import zhTW from 'antd/locale/zh_TW';
import React from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';

/**
 * 主應用程式元件
 * 配置路由與全域設定
 */
const App: React.FC = () => {
  return (
    <ConfigProvider
      locale={zhTW}
      theme={{
        token: {
          colorPrimary: '#667eea',
          borderRadius: 8,
          fontFamily: "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif",
        },
      }}
    >
      <Routes>
        {/* 登入頁面 */}
        <Route path="/login" element={<HR01LoginPage />} />
        
        {/* 員工列表頁面 */}
        <Route path="/employees" element={<HR02EmployeeListPage />} />
        
        {/* 預設導向 */}
        <Route path="/" element={<Navigate to="/login" replace />} />
        
        {/* 404 頁面 - TODO: 建立專用 404 頁面 */}
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </ConfigProvider>
  );
};

export default App;
