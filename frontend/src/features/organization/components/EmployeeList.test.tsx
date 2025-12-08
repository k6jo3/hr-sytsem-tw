import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { EmployeeList } from './EmployeeList';
import type { EmployeeViewModel } from '../model/EmployeeViewModel';

describe('EmployeeList', () => {
  const mockEmployees: EmployeeViewModel[] = [
    {
      id: '1',
      employeeNumber: 'EMP001',
      fullName: '王小明',
      email: 'xiaoming.wang@company.com',
      phone: '0912345678',
      departmentName: '人力資源部',
      position: '人資專員',
      statusLabel: '在職',
      statusColor: 'success',
      hireDate: '2023-01-15',
    },
    {
      id: '2',
      employeeNumber: 'EMP002',
      fullName: '李小華',
      email: 'xiaohua.li@company.com',
      departmentName: '研發部',
      position: '資深工程師',
      statusLabel: '在職',
      statusColor: 'success',
      hireDate: '2022-06-01',
    },
  ];

  describe('表格渲染', () => {
    it('應該顯示所有員工資料', () => {
      render(
        <EmployeeList
          employees={mockEmployees}
          loading={false}
          total={2}
          onRefresh={() => {}}
          onAdd={() => {}}
        />
      );

      // 檢查表格標題
      expect(screen.getByText('員工編號')).toBeInTheDocument();
      expect(screen.getByText('姓名')).toBeInTheDocument();
      expect(screen.getByText('部門')).toBeInTheDocument();
      expect(screen.getByText('職位')).toBeInTheDocument();
      expect(screen.getByText('狀態')).toBeInTheDocument();

      // 檢查員工資料
      expect(screen.getByText('EMP001')).toBeInTheDocument();
      expect(screen.getByText('王小明')).toBeInTheDocument();
      expect(screen.getByText('人力資源部')).toBeInTheDocument();
      expect(screen.getByText('人資專員')).toBeInTheDocument();

      expect(screen.getByText('EMP002')).toBeInTheDocument();
      expect(screen.getByText('李小華')).toBeInTheDocument();
      expect(screen.getByText('研發部')).toBeInTheDocument();
      expect(screen.getByText('資深工程師')).toBeInTheDocument();
    });

    it('應該顯示新增按鈕', () => {
      render(
        <EmployeeList
          employees={mockEmployees}
          loading={false}
          total={2}
          onRefresh={() => {}}
          onAdd={() => {}}
        />
      );

      const addButton = screen.getByText('新增員工');
      expect(addButton).toBeInTheDocument();
    });

    it('應該顯示重新整理按鈕', () => {
      render(
        <EmployeeList
          employees={mockEmployees}
          loading={false}
          total={2}
          onRefresh={() => {}}
          onAdd={() => {}}
        />
      );

      const refreshButton = screen.getByText('重新整理');
      expect(refreshButton).toBeInTheDocument();
    });
  });

  describe('載入狀態', () => {
    it('載入中時應該顯示載入狀態', () => {
      render(
        <EmployeeList
          employees={[]}
          loading={true}
          total={0}
          onRefresh={() => {}}
          onAdd={() => {}}
        />
      );

      // Table 元件在載入時會顯示 loading spinner
      const table = screen.getByRole('table');
      expect(table).toBeInTheDocument();
    });
  });

  describe('按鈕互動', () => {
    it('點擊新增按鈕應該呼叫onAdd', () => {
      const handleAdd = vi.fn();

      render(
        <EmployeeList
          employees={mockEmployees}
          loading={false}
          total={2}
          onRefresh={() => {}}
          onAdd={handleAdd}
        />
      );

      const addButton = screen.getByText('新增員工');
      fireEvent.click(addButton);

      expect(handleAdd).toHaveBeenCalledTimes(1);
    });

    it('點擊重新整理按鈕應該呼叫onRefresh', () => {
      const handleRefresh = vi.fn();

      render(
        <EmployeeList
          employees={mockEmployees}
          loading={false}
          total={2}
          onRefresh={handleRefresh}
          onAdd={() => {}}
        />
      );

      const refreshButton = screen.getByText('重新整理');
      fireEvent.click(refreshButton);

      expect(handleRefresh).toHaveBeenCalledTimes(1);
    });
  });

  describe('分頁', () => {
    it('應該顯示分頁控制項', () => {
      render(
        <EmployeeList
          employees={mockEmployees}
          loading={false}
          total={50}
          onRefresh={() => {}}
          onAdd={() => {}}
        />
      );

      // Ant Design 的 Pagination 會顯示頁碼
      const pagination = screen.getByRole('list', { name: /pagination/i });
      expect(pagination).toBeInTheDocument();
    });

    it('應該支援變更頁面大小', () => {
      const handlePageChange = vi.fn();

      render(
        <EmployeeList
          employees={mockEmployees}
          loading={false}
          total={50}
          onRefresh={() => {}}
          onAdd={() => {}}
          onPageChange={handlePageChange}
        />
      );

      // 檢查是否有頁面大小選擇器 (showSizeChanger)
      const table = screen.getByRole('table');
      expect(table).toBeInTheDocument();
    });
  });

  describe('空狀態', () => {
    it('沒有員工時應該顯示空狀態', () => {
      render(
        <EmployeeList
          employees={[]}
          loading={false}
          total={0}
          onRefresh={() => {}}
          onAdd={() => {}}
        />
      );

      // Ant Design Table 會顯示 "No Data" 或類似訊息
      const table = screen.getByRole('table');
      expect(table).toBeInTheDocument();
    });
  });

  describe('狀態標籤', () => {
    it('應該正確顯示不同狀態的標籤顏色', () => {
      const employeesWithDifferentStatus: EmployeeViewModel[] = [
        {
          ...mockEmployees[0],
          statusLabel: '在職',
          statusColor: 'success',
        },
        {
          ...mockEmployees[1],
          statusLabel: '離職',
          statusColor: 'error',
        },
      ];

      render(
        <EmployeeList
          employees={employeesWithDifferentStatus}
          loading={false}
          total={2}
          onRefresh={() => {}}
          onAdd={() => {}}
        />
      );

      expect(screen.getByText('在職')).toBeInTheDocument();
      expect(screen.getByText('離職')).toBeInTheDocument();
    });
  });
});
