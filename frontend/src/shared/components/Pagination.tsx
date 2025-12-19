import React from 'react';
import { Pagination as AntPagination, Select } from 'antd';
import type { PageResponse, PageRequest } from '../types';

/**
 * 分頁元件 Props
 */
export interface PaginationProps {
  /** 分頁資訊 */
  pageInfo: Pick<PageResponse<unknown>, 'page' | 'size' | 'totalElements' | 'totalPages'>;
  /** 頁碼或每頁筆數變更時的回調 */
  onChange: (params: PageRequest) => void;
  /** 是否顯示每頁筆數選擇器 */
  showSizeChanger?: boolean;
  /** 每頁筆數選項 */
  pageSizeOptions?: number[];
  /** 是否顯示快速跳轉 */
  showQuickJumper?: boolean;
  /** 是否顯示總筆數 */
  showTotal?: boolean;
  /** 是否禁用 */
  disabled?: boolean;
  /** 尺寸 */
  size?: 'default' | 'small';
  /** 對齊方式 */
  align?: 'left' | 'center' | 'right';
}

/**
 * 分頁元件
 * 封裝 Ant Design Pagination，提供與後端 PageResponse 的整合
 *
 * @example
 * ```tsx
 * <Pagination
 *   pageInfo={{
 *     page: 1,
 *     size: 20,
 *     totalElements: 100,
 *     totalPages: 5
 *   }}
 *   onChange={({ page, size }) => {
 *     // 重新載入資料
 *   }}
 * />
 * ```
 */
export const Pagination: React.FC<PaginationProps> = ({
  pageInfo,
  onChange,
  showSizeChanger = true,
  pageSizeOptions = [10, 20, 50, 100],
  showQuickJumper = true,
  showTotal = true,
  disabled = false,
  size = 'default',
  align = 'right',
}) => {
  const { page, size: pageSize, totalElements } = pageInfo;

  const handleChange = (newPage: number, newSize: number) => {
    onChange({
      page: newPage,
      size: newSize,
    });
  };

  const renderTotal = (total: number, range: [number, number]) => {
    return `第 ${range[0]}-${range[1]} 筆，共 ${total} 筆`;
  };

  const containerStyle: React.CSSProperties = {
    display: 'flex',
    justifyContent: align === 'left' ? 'flex-start' : align === 'right' ? 'flex-end' : 'center',
    padding: '16px 0',
  };

  return (
    <div style={containerStyle}>
      <AntPagination
        current={page}
        pageSize={pageSize}
        total={totalElements}
        onChange={handleChange}
        onShowSizeChange={handleChange}
        showSizeChanger={showSizeChanger}
        pageSizeOptions={pageSizeOptions.map(String)}
        showQuickJumper={showQuickJumper}
        showTotal={showTotal ? renderTotal : undefined}
        disabled={disabled}
        size={size}
      />
    </div>
  );
};

export default Pagination;
