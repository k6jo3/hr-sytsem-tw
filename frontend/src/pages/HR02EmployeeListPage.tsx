import React, { useState } from 'react';
import { Layout, message } from 'antd';
import { EmployeeList } from '@features/organization/components/EmployeeList';
import { useEmployees } from '@features/organization/hooks/useEmployees';

const { Content } = Layout;

/**
 * HR02 員工列表頁面
 * 頁面代碼：HR02-P01
 */
const HR02EmployeeListPage: React.FC = () => {
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  const { employees, total, loading, error, refresh } = useEmployees({
    page,
    page_size: pageSize,
  });

  // 顯示錯誤訊息
  React.useEffect(() => {
    if (error) {
      message.error(error);
    }
  }, [error]);

  // 處理新增員工
  const handleAdd = () => {
    message.info('新增員工功能開發中...');
  };

  // 處理分頁變更
  const handlePageChange = (newPage: number, newPageSize: number) => {
    setPage(newPage);
    setPageSize(newPageSize);
  };

  return (
    <Layout style={{ minHeight: '100vh', background: '#f0f2f5' }}>
      <Content style={{ padding: 24 }}>
        <EmployeeList
          employees={employees}
          loading={loading}
          total={total}
          onRefresh={refresh}
          onAdd={handleAdd}
          onPageChange={handlePageChange}
        />
      </Content>
    </Layout>
  );
};

export default HR02EmployeeListPage;
