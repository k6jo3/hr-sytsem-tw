import { EmployeeList } from '@features/organization/components/EmployeeList';
import { EmployeeFormModal } from '@features/organization/components/EmployeeFormModal';
import { OrganizationApi } from '@features/organization/api/OrganizationApi';
import { UserApi } from '@features/auth/api/UserApi';
import type { CreateUserRequest } from '@features/auth/api/AuthTypes';
import { useEmployees } from '@features/organization/hooks/useEmployees';
import { PageHeader } from '@shared/components/PageHeader';
import { PlusOutlined, ReloadOutlined } from '@ant-design/icons';
import { Button, Layout, message, Space } from 'antd';
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
      message.error(typeof error === 'string' ? error : '載入員工清單失敗');
    }
  }, [error]);

  // 處理新增員工
  const handleAdd = () => {
    setModalOpen(true);
  };

  // 處理新增員工提交（支援同時建立系統帳號）
  const handleCreateEmployee = async (values: any) => {
    setModalLoading(true);
    try {
      // 分離員工資料和帳號資料
      const { createAccount, accountInfo, ...employeeData } = values;

      // 步驟1：建立員工
      await OrganizationApi.createEmployee(employeeData);

      // 步驟2：如果需要，建立系統帳號
      if (createAccount && accountInfo) {
        try {
          const createUserReq: CreateUserRequest = {
            username: accountInfo.username,
            email: employeeData.companyEmail,
            password: accountInfo.password,
            display_name: `${employeeData.lastName}${employeeData.firstName}`,
            first_name: employeeData.firstName,
            last_name: employeeData.lastName,
            employee_id: employeeData.employeeNumber,
            role_ids: accountInfo.roleIds,
            must_change_password: accountInfo.mustChangePassword ?? true,
          };
          await UserApi.createUser(createUserReq);
          message.success('員工建立成功，系統帳號已同步建立');
        } catch (accountErr: any) {
          const accountMsg = accountErr?.message || '帳號建立失敗';
          message.warning(`員工已建立成功，但系統帳號建立失敗：${accountMsg}`);
        }
      } else {
        message.success('員工建立成功');
      }

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
        <PageHeader
          title="員工列表"
          breadcrumbs={[
            { title: '組織與員工', path: '/admin/employees' },
            { title: '員工列表' },
          ]}
          extra={
            <Space>
              <Button icon={<ReloadOutlined />} onClick={refresh}>
                重新整理
              </Button>
              <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
                新增員工
              </Button>
            </Space>
          }
        />
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
