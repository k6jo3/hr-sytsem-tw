import React, { useState } from 'react';
import { Card, List, Tag, Select, message, Space, Button } from 'antd';
import { EyeOutlined, ReloadOutlined } from '@ant-design/icons';
import { usePayslips } from '../features/payroll/hooks/usePayslips';
import { PayslipDetail } from '../features/payroll/components/PayslipDetail';

const { Option } = Select;

/**
 * HR04-P06: 我的薪資單頁面 (ESS)
 */
export const HR04MyPayslipsPage: React.FC = () => {
  const currentYear = new Date().getFullYear();
  const [selectedYear, setSelectedYear] = useState(currentYear);
  const [selectedPayslipId, setSelectedPayslipId] = useState<string | null>(null);

  const {
    payslips,
    selectedPayslip,
    loading,
    detailLoading,
    error,
    fetchPayslipDetail,
    downloadPdf,
    refresh,
  } = usePayslips(selectedYear);

  // 顯示錯誤訊息
  React.useEffect(() => {
    if (error) {
      message.error(error);
    }
  }, [error]);

  const handleViewDetail = async (id: string) => {
    setSelectedPayslipId(id);
    await fetchPayslipDetail(id);
  };

  const handleDownload = async (id: string) => {
    await downloadPdf(id);
    message.success('薪資單PDF下載成功');
  };

  const handleYearChange = (year: number) => {
    setSelectedYear(year);
    setSelectedPayslipId(null);
  };

  // 生成年度選項
  const yearOptions = Array.from({ length: 5 }, (_, i) => currentYear - i);

  return (
    <div style={{ padding: 24 }}>
      <Card
        title={<span style={{ fontSize: 20, fontWeight: 600 }}>我的薪資單</span>}
        extra={
          <Space>
            <Select
              value={selectedYear}
              onChange={handleYearChange}
              style={{ width: 120 }}
              placeholder="選擇年度"
            >
              {yearOptions.map((year) => (
                <Option key={year} value={year}>
                  {year}年
                </Option>
              ))}
            </Select>
            <Button icon={<ReloadOutlined />} onClick={refresh}>
              重新整理
            </Button>
          </Space>
        }
      >
        <div style={{ display: 'flex', gap: 24 }}>
          {/* 左側：薪資單列表 */}
          <div style={{ flex: '0 0 400px' }}>
            <List
              loading={loading}
              dataSource={payslips}
              renderItem={(item) => (
                <List.Item
                  key={item.id}
                  onClick={() => handleViewDetail(item.id)}
                  style={{
                    cursor: 'pointer',
                    backgroundColor:
                      selectedPayslipId === item.id ? '#e6f7ff' : 'transparent',
                    borderRadius: 4,
                    padding: '12px 16px',
                  }}
                  actions={[
                    <Button
                      key="view"
                      type="link"
                      icon={<EyeOutlined />}
                      onClick={(e) => {
                        e.stopPropagation();
                        handleViewDetail(item.id);
                      }}
                    >
                      查看詳情
                    </Button>,
                  ]}
                >
                  <List.Item.Meta
                    title={
                      <Space>
                        <span>{item.payPeriod}</span>
                        <Tag color={item.statusColor}>{item.statusLabel}</Tag>
                      </Space>
                    }
                    description={
                      <div>
                        <div>發薪日：{item.paymentDateDisplay}</div>
                        <div style={{ marginTop: 4 }}>
                          <span style={{ color: '#52c41a' }}>
                            應發：{item.grossPayDisplay}
                          </span>
                          <span style={{ marginLeft: 16, color: '#1890ff', fontWeight: 'bold' }}>
                            實發：{item.netPayDisplay}
                          </span>
                        </div>
                      </div>
                    }
                  />
                </List.Item>
              )}
              locale={{ emptyText: '查無薪資單資料' }}
            />
          </div>

          {/* 右側：薪資單詳情 */}
          <div style={{ flex: 1 }}>
            <PayslipDetail
              payslip={selectedPayslip}
              loading={detailLoading}
              onDownload={handleDownload}
            />
          </div>
        </div>
      </Card>
    </div>
  );
};
