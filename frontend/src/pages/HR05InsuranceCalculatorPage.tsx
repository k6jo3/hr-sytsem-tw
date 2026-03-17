import { CalculatorOutlined, DollarOutlined, InfoCircleOutlined } from '@ant-design/icons';
import { Button, Card, Col, Divider, InputNumber, message, Row, Space, Statistic, Table, Tag, Typography } from 'antd';
import React, { useEffect, useState } from 'react';
import { useInsuranceCalculator } from '../features/insurance/hooks';

const { Title, Text, Paragraph } = Typography;

/**
 * HR05-P04: 保險費用試算頁面
 */
export const HR05InsuranceCalculatorPage: React.FC = () => {
  const [salary, setSalary] = useState<number | null>(null);
  const { calculateFees, result, levelNumber, loading, error } = useInsuranceCalculator();

  const handleCalculate = async () => {
    if (salary === null || salary === undefined) {
      message.warning('請輸入待試算月薪');
      return;
    }
    if (salary <= 0) {
      message.warning('請輸入大於 0 的月薪金額');
      return;
    }
    await calculateFees(salary);
  };

  useEffect(() => {
    if (error) {
      message.error(error);
    }
  }, [error]);

  const columns = [
    { title: '保險項目', dataIndex: 'item', key: 'item' },
    { title: '個人負擔', dataIndex: 'employee', key: 'employee', align: 'right' as const },
    { title: '雇主負擔', dataIndex: 'employer', key: 'employer', align: 'right' as const },
  ];

  const tableData = result ? [
    {
      key: 'labor',
      item: '勞工保險',
      employee: result.laborEmployeeDisplay,
      employer: result.laborEmployerDisplay,
    },
    {
      key: 'health',
      item: '健康保險',
      employee: result.healthEmployeeDisplay,
      employer: result.healthEmployerDisplay,
    },
    {
      key: 'pension',
      item: '勞工退休金 (6%)',
      employee: '-',
      employer: result.pensionEmployerDisplay,
    }
  ] : [];

  return (
    <div style={{ padding: 24 }}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <Row justify="space-between" align="middle">
          <Col>
            <Title level={2}>保險費用試算</Title>
            <Text type="secondary">輸入員工月薪，預估勞健保及勞退費用的自付額與公提額。</Text>
          </Col>
        </Row>

        <Card>
          <Row gutter={24} align="middle">
            <Col span={10}>
              <Text strong style={{ marginRight: 16 }}>請輸入待試算月薪 (TWD):</Text>
              <InputNumber
                style={{ width: 200 }}
                placeholder="例如: 50,000"
                formatter={value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
                parser={value => Number(value!.replace(/\$\s?|(,*)/g, ''))}
                value={salary}
                onChange={val => setSalary(val)}
                min={0}
                onPressEnter={handleCalculate}
              />
            </Col>
            <Col span={4}>
              <Button 
                type="primary" 
                icon={<CalculatorOutlined />} 
                onClick={handleCalculate}
                loading={loading}
              >
                開始試算
              </Button>
            </Col>
          </Row>
        </Card>

        {result && (
          <Row gutter={24}>
            <Col span={16}>
              <Card 
                title={
                  <Space>
                    <DollarOutlined />
                    <span>試算明細</span>
                    <Tag color="blue">適用投保級距: 第 {levelNumber} 級</Tag>
                  </Space>
                }
              >
                <Table 
                  columns={columns} 
                  dataSource={tableData} 
                  pagination={false} 
                  summary={() => (
                    <Table.Summary fixed>
                      <Table.Summary.Row>
                        <Table.Summary.Cell index={0}><strong>每月小計</strong></Table.Summary.Cell>
                        <Table.Summary.Cell index={1} align="right">
                          <strong style={{ color: '#f5222d' }}>{result.totalEmployeeDisplay}</strong>
                        </Table.Summary.Cell>
                        <Table.Summary.Cell index={2} align="right">
                          <strong style={{ color: '#52c41a' }}>{result.totalEmployerDisplay}</strong>
                        </Table.Summary.Cell>
                      </Table.Summary.Row>
                    </Table.Summary>
                  )}
                />
                
                <Divider />
                
                <div style={{ textAlign: 'right', padding: '0 16px' }}>
                  <Statistic 
                    title="保險成本支出總額 (個人+公司)" 
                    value={result.grandTotalDisplay.replace('$', '')} 
                    prefix="$"
                    valueStyle={{ color: '#1890ff' }} 
                  />
                </div>
              </Card>
            </Col>

            <Col span={8}>
              <Card title={<Space><InfoCircleOutlined /><span>法規參考說明</span></Space>} size="small">
                <Paragraph style={{ fontSize: '13px' }}>
                  <ul style={{ paddingLeft: '16px' }}>
                    <li><strong>勞保費率: 11.5%</strong> (含就業保險 1%)。</li>
                    <li><strong>勞保負擔比例:</strong> 個人 20%、雇主 70%、政府 10%。</li>
                    <li><strong>健保費率: 5.17%</strong>。</li>
                    <li><strong>健保負擔比例:</strong> 個人 30%、雇主 60%、政府 10%。</li>
                    <li><strong>勞退提繳:</strong> 雇主至少 6%、個人可自願提繳 0-6%。</li>
                    <li><strong>補充保費:</strong> 單次獎金超過投保薪資 4 倍時，按 2.11% 計收。</li>
                  </ul>
                  <Text type="secondary" style={{ fontSize: '11px' }}>
                    * 本試算僅供參考，實際扣繳金額依勞保局及健保局每月正式通知單為準。
                  </Text>
                </Paragraph>
              </Card>
            </Col>
          </Row>
        )}
      </Space>
    </div>
  );
};

export default HR05InsuranceCalculatorPage;
