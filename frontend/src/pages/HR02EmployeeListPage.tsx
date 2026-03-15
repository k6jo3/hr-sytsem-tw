import { EmployeeList } from '@features/organization/components/EmployeeList';
import { EmployeeFormModal } from '@features/organization/components/EmployeeFormModal';
import { OrganizationApi } from '@features/organization/api/OrganizationApi';
import { useEmployees } from '@features/organization/hooks/useEmployees';
import { Layout, message } from 'antd';
import React, { useState } from 'react';

const { Content } = Layout;

/**
 * HR02 員工列表頁面
 * 頁面代碼：HR02-P02
 */
const HR02EmployeeListPage: React.FC = () => {
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [modalOpen, setModalOpen] = useState(false);
  const [modalLoading, setModalLoading] = useState(false);

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
    setModalOpen(true);
  };

  // 處理新增員工提交
  const handleCreateEmployee = async (values: any) => {
    setModalLoading(true);
    try {
      await OrganizationApi.createEmployee(values);
      message.success('員工建立成功');
      setModalOpen(false);
      refresh();
    } catch (err: any) {
      const msg = err?.message || '員工建立失敗';
      message.error(msg);
      throw err;
    } finally {
      setModalLoading(false);
    }
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
          currentPage={page}
          pageSize={pageSize}
          onRefresh={refresh}
          onAdd={handleAdd}
          onPageChange={handlePageChange}
        />
        <EmployeeFormModal
          open={modalOpen}
          onCancel={() => setModalOpen(false)}
          onSubmit={handleCreateEmployee}
          loading={modalLoading}
        />
      </Content>
    </Layout>
  );
};

export default HR02EmployeeListPage;
