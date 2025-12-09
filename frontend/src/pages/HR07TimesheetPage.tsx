import React from 'react';
import { Layout, message } from 'antd';
import { WeeklyTimesheetView } from '@features/timesheet/components/WeeklyTimesheetView';
import { useTimesheet } from '@features/timesheet/hooks/useTimesheet';

const { Content } = Layout;

const HR07TimesheetPage: React.FC = () => {
  const weekStartDate = '2024-12-02';
  const { summary, loading, error, handleSubmit } = useTimesheet(weekStartDate);

  React.useEffect(() => {
    if (error) {
      message.error(error);
    }
  }, [error]);

  const onSubmit = async () => {
    try {
      await handleSubmit();
      message.success('工時提交成功！');
    } catch (err) {
      message.error(err instanceof Error ? err.message : '提交失敗');
    }
  };

  return (
    <Layout style={{ minHeight: '100vh', background: '#f0f2f5' }}>
      <Content style={{ padding: 24 }}>
        <div style={{ maxWidth: 1200, margin: '0 auto' }}>
          <WeeklyTimesheetView
            summary={summary}
            loading={loading}
            onSubmit={onSubmit}
            onEdit={() => {}}
            onDelete={() => {}}
          />
        </div>
      </Content>
    </Layout>
  );
};

export default HR07TimesheetPage;
