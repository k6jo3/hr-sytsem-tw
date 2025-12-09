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
        {/* ========== 公開頁面 ========== */}
        {/* IAM - 登入 (HR01) */}
        <Route path="/login" element={<HR01LoginPage />} />

        {/* ========== 員工自助服務 (ESS - Employee Self Service) ========== */}
        {/* HR02 - 我的資料 */}
        {/* TODO: <Route path="/profile" element={<HR02MyProfilePage />} /> */}

        {/* HR03 - 考勤打卡與請假 */}
        <Route path="/attendance/check-in" element={<HR03CheckInPage />} />
        <Route path="/attendance/leave/apply" element={<HR03LeaveListPage />} />
        {/* TODO: <Route path="/attendance/my-records" element={<HR03MyRecordsPage />} /> */}
        {/* TODO: <Route path="/attendance/leave/balance" element={<HR03LeaveBalancePage />} /> */}

        {/* HR04 - 我的薪資單 */}
        <Route path="/profile/payslips" element={<HR04PayrollListPage />} />
        <Route path="/profile/payslips/:id" element={<HR04PayslipPage />} />

        {/* HR05 - 我的保險資訊 */}
        <Route path="/profile/insurance" element={<HR05InsuranceListPage />} />

        {/* HR06 - 我參與的專案 */}
        {/* TODO: <Route path="/profile/projects" element={<HR06MyProjectsPage />} /> */}

        {/* HR07 - 我的工時回報 */}
        <Route path="/profile/timesheets" element={<HR07TimesheetPage />} />

        {/* ========== 後台管理 (Admin) ========== */}
        {/* HR02 - 組織員工管理 */}
        <Route path="/admin/employees" element={<HR02EmployeeListPage />} />
        {/* TODO: <Route path="/admin/employees/:id" element={<HR02EmployeeDetailPage />} /> */}

        {/* HR06 - 專案管理 */}
        <Route path="/admin/projects" element={<HR06ProjectListPage />} />
        <Route path="/admin/projects/:id" element={<HR06ProjectDetailPage />} />

        {/* HR08 - 績效管理 */}
        <Route path="/admin/performance" element={<HR08PerformanceListPage />} />

        {/* HR09 - 招募管理 */}
        <Route path="/admin/recruitment" element={<HR09RecruitmentPage />} />

        {/* HR10 - 訓練管理 */}
        <Route path="/admin/training" element={<HR10TrainingListPage />} />

        {/* HR11 - 簽核流程 */}
        <Route path="/admin/workflow" element={<HR11WorkflowListPage />} />

        {/* HR12 - 通知管理 */}
        <Route path="/admin/notifications" element={<HR12NotificationPage />} />

        {/* HR13 - 文件管理 */}
        <Route path="/admin/documents" element={<HR13DocumentListPage />} />

        {/* HR14 - 報表分析 */}
        <Route path="/admin/reports" element={<HR14ReportDashboardPage />} />

        {/* ========== 預設與錯誤處理 ========== */}
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </ConfigProvider>
  );
};

export default App;
