import React, { useState, useCallback } from 'react';
import { Form, Input, Button, Row, Col, Space } from 'antd';
import { SearchOutlined, ReloadOutlined, DownOutlined, UpOutlined } from '@ant-design/icons';

/**
 * 搜尋表單項目定義
 */
export interface SearchFormItem {
  /** 欄位名稱 */
  name: string;
  /** 標籤 */
  label: string;
  /** 元件類型 */
  type?: 'input' | 'select' | 'datePicker' | 'rangePicker' | 'custom';
  /** 佔位文字 */
  placeholder?: string;
  /** 是否為進階搜尋欄位（預設摺疊） */
  advanced?: boolean;
  /** 自訂渲染（當 type 為 custom 時使用） */
  render?: () => React.ReactNode;
  /** 欄寬（預設 8，即 1/3） */
  colSpan?: number;
}

/**
 * 搜尋表單 Props
 */
export interface SearchFormProps {
  /** 表單項目定義 */
  items: SearchFormItem[];
  /** 搜尋時的回調 */
  onSearch: (values: Record<string, unknown>) => void;
  /** 重置時的回調 */
  onReset?: () => void;
  /** 是否顯示展開/收起按鈕 */
  showExpand?: boolean;
  /** 預設是否展開 */
  defaultExpanded?: boolean;
  /** 是否正在載入 */
  loading?: boolean;
  /** 初始值 */
  initialValues?: Record<string, unknown>;
}

/**
 * 搜尋表單元件
 * 提供統一的列表頁搜尋表單佈局
 *
 * @example
 * ```tsx
 * <SearchForm
 *   items={[
 *     { name: 'keyword', label: '關鍵字', placeholder: '請輸入員工姓名或編號' },
 *     { name: 'department', label: '部門', type: 'select' },
 *     { name: 'hireDate', label: '到職日', type: 'datePicker', advanced: true },
 *   ]}
 *   onSearch={(values) => {
 *     // 執行搜尋
 *   }}
 * />
 * ```
 */
export const SearchForm: React.FC<SearchFormProps> = ({
  items,
  onSearch,
  onReset,
  showExpand = true,
  defaultExpanded = false,
  loading = false,
  initialValues = {},
}) => {
  const [form] = Form.useForm();
  const [expanded, setExpanded] = useState(defaultExpanded);

  // 區分基本和進階欄位
  const basicItems = items.filter((item) => !item.advanced);
  const advancedItems = items.filter((item) => item.advanced);
  const hasAdvanced = advancedItems.length > 0;

  // 目前顯示的欄位
  const visibleItems = expanded ? items : basicItems;

  const handleSearch = useCallback(() => {
    const values = form.getFieldsValue();
    onSearch(values);
  }, [form, onSearch]);

  const handleReset = useCallback(() => {
    form.resetFields();
    onReset?.();
  }, [form, onReset]);

  const toggleExpand = useCallback(() => {
    setExpanded((prev) => !prev);
  }, []);

  const renderFormItem = (item: SearchFormItem) => {
    const colSpan = item.colSpan ?? 8;

    return (
      <Col span={colSpan} key={item.name}>
        <Form.Item name={item.name} label={item.label}>
          {item.type === 'custom' && item.render ? (
            item.render()
          ) : (
            <Input placeholder={item.placeholder ?? `請輸入${item.label}`} />
          )}
        </Form.Item>
      </Col>
    );
  };

  return (
    <Form
      form={form}
      layout="horizontal"
      initialValues={initialValues}
      style={{ marginBottom: 16 }}
    >
      <Row gutter={16}>
        {visibleItems.map(renderFormItem)}

        <Col span={8} style={{ textAlign: 'right' }}>
          <Space>
            <Button
              type="primary"
              icon={<SearchOutlined />}
              onClick={handleSearch}
              loading={loading}
            >
              搜尋
            </Button>
            <Button icon={<ReloadOutlined />} onClick={handleReset}>
              重置
            </Button>
            {showExpand && hasAdvanced && (
              <Button type="link" onClick={toggleExpand}>
                {expanded ? (
                  <>
                    收起 <UpOutlined />
                  </>
                ) : (
                  <>
                    展開 <DownOutlined />
                  </>
                )}
              </Button>
            )}
          </Space>
        </Col>
      </Row>
    </Form>
  );
};

export default SearchForm;
