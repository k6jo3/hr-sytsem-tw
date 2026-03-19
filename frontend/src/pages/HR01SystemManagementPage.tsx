/**
 * 系統管理頁面 (HR01-P06)
 * 三個 Tab：系統參數、功能開關、排程管理
 * 僅限 ADMIN 角色存取
 */

import { SettingOutlined } from '@ant-design/icons';
import { Alert, Spin, Tabs, Typography } from 'antd';
import React from 'react';
import { FeatureToggleTab } from '../features/auth/components/FeatureToggleTab';
import { ScheduledJobTab } from '../features/auth/components/ScheduledJobTab';
import { SystemParameterTab } from '../features/auth/components/SystemParameterTab';
import { useSystemManagement } from '../features/auth/hooks/useSystemManagement';

const { Title } = Typography;

export const HR01SystemManagementPage: React.FC = () => {
  const {
    parameters,
    features,
    jobs,
    loading,
    error,
    refreshParameters,
    refreshFeatures,
    refreshJobs,
    updateParameter,
    toggleFeature,
    updateJob,
  } = useSystemManagement();

  if (loading && parameters.length === 0) {
    return (
      <div style={{ textAlign: 'center', padding: '100px 0' }}>
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div style={{ maxWidth: 1200, margin: '0 auto' }}>
      <Title level={3}>
        <SettingOutlined style={{ marginRight: 8 }} />
        系統管理
      </Title>

      {error && (
        <Alert
          type="error"
          message={error}
          showIcon
          closable
          style={{ marginBottom: 16 }}
        />
      )}

      <Tabs
        defaultActiveKey="parameters"
        items={[
          {
            key: 'parameters',
            label: `系統參數 (${parameters.length})`,
            children: (
              <SystemParameterTab
                parameters={parameters}
                loading={loading}
                onUpdate={updateParameter}
                onRefresh={refreshParameters}
              />
            ),
          },
          {
            key: 'features',
            label: `功能開關 (${features.length})`,
            children: (
              <FeatureToggleTab
                features={features}
                loading={loading}
                onToggle={toggleFeature}
                onRefresh={refreshFeatures}
              />
            ),
          },
          {
            key: 'jobs',
            label: `排程管理 (${jobs.length})`,
            children: (
              <ScheduledJobTab
                jobs={jobs}
                loading={loading}
                onRefresh={refreshJobs}
                onToggleEnabled={updateJob}
              />
            ),
          },
        ]}
      />
    </div>
  );
};

export default HR01SystemManagementPage;
