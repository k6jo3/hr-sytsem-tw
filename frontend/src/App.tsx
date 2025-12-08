import {
  HR01LoginPage,
  HR02EmployeeListPage,
  HR03CheckInPage,
  HR03LeaveListPage,
  HR04PayrollListPage,
  HR04PayslipPage,
  HR05InsuranceListPage,
  HR06ProjectListPage,
  HR06ProjectDetailPage,
  HR07TimesheetPage,
  HR08PerformanceListPage,
  HR09RecruitmentPage,
  HR10TrainingListPage,
  HR11WorkflowListPage,
  HR12NotificationPage,
  HR13DocumentListPage,
  HR14ReportDashboardPage,
} from '@pages/index';
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
        {/* IAM - 登入 (HR01) */}
        <Route path="/login" element={<HR01LoginPage />} />
        
        {/* Organization - 組織員工 (HR02) */}
        <Route path="/employees" element={<HR02EmployeeListPage />} />
        
        {/* Attendance - 考勤管理 (HR03) */}
        <Route path="/attendance/check-in" element={<HR03CheckInPage />} />
        <Route path="/attendance/leaves" element={<HR03LeaveListPage />} />
        
        {/* Payroll - 薪資管理 (HR04) */}
        <Route path="/payroll" element={<HR04PayrollListPage />} />
        <Route path="/payroll/payslip/:id" element={<HR04PayslipPage />} />
        
        {/* Insurance - 保險管理 (HR05) */}
        <Route path="/insurance" element={<HR05InsuranceListPage />} />
        
        {/* Project - 專案管理 (HR06) */}
        <Route path="/projects" element={<HR06ProjectListPage />} />
        <Route path="/projects/:id" element={<HR06ProjectDetailPage />} />
        
        {/* Timesheet - 工時管理 (HR07) */}
        <Route path="/timesheet" element={<HR07TimesheetPage />} />
        
        {/* Performance - 績效管理 (HR08) */}
        <Route path="/performance" element={<HR08PerformanceListPage />} />
        
        {/* Recruitment - 招募管理 (HR09) */}
        <Route path="/recruitment" element={<HR09RecruitmentPage />} />
        
        {/* Training - 訓練管理 (HR10) */}
        <Route path="/training" element={<HR10TrainingListPage />} />
        
        {/* Workflow - 簽核流程 (HR11) */}
        <Route path="/workflow" element={<HR11WorkflowListPage />} />
        
        {/* Notification - 通知服務 (HR12) */}
        <Route path="/notifications" element={<HR12NotificationPage />} />
        
        {/* Document - 文件管理 (HR13) */}
        <Route path="/documents" element={<HR13DocumentListPage />} />
        
        {/* Report - 報表分析 (HR14) */}
        <Route path="/reports" element={<HR14ReportDashboardPage />} />
        
        {/* 預設導向 */}
        <Route path="/" element={<Navigate to="/login" replace />} />
        
        {/* 404 頁面 */}
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </ConfigProvider>
  );
};

export default App;
