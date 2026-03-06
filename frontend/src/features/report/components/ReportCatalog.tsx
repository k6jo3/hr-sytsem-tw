/**
 * 報表目錄元件
 * Domain Code: HR14
 * 自包含元件：內部呼叫 useReportDefinitions hook
 */

import React from 'react';
import {
  Typography,
  Card,
  List,
  Spin,
  Empty,
} from 'antd';
import { FileTextOutlined } from '@ant-design/icons';
import { useReportDefinitions, useGenerateReport } from '../hooks';
import type { ReportDefinitionViewModel } from '../model/ReportViewModel';
import type { ReportFormat } from '../api/ReportTypes';

const { Title, Text } = Typography;

export const ReportCatalog: React.FC = () => {
  const { data, isLoading } = useReportDefinitions();
  const { mutate: generateReport } = useGenerateReport();

  const handleGenerate = (definition: ReportDefinitionViewModel) => {
    const format = definition.availableFormats[0] || 'PDF';
    generateReport({
      report_definition_id: definition.definitionId,
      format: format as ReportFormat,
      parameters: {},
    });
  };

  if (isLoading) {
    return (
      <div style={{ textAlign: 'center', padding: 48 }}>
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div>
      <Text type="secondary" style={{ display: 'block', marginBottom: 24 }}>
        選擇報表類型，快速產生所需報表
      </Text>

      {data && data.definitions.length > 0 ? (
        <List
          grid={{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 4 }}
          dataSource={data.definitions}
          renderItem={(item) => (
            <List.Item>
              <Card hoverable onClick={() => handleGenerate(item)}>
                <div style={{ textAlign: 'center' }}>
                  <div style={{ fontSize: 32, marginBottom: 12 }}>
                    <FileTextOutlined />
                  </div>
                  <Title level={5} style={{ marginBottom: 4 }}>
                    {item.reportName}
                  </Title>
                  <Text type="secondary" style={{ fontSize: 12 }}>
                    {item.description}
                  </Text>
                  <div style={{ marginTop: 8 }}>
                    <Text type="secondary" style={{ fontSize: 11 }}>
                      格式: {item.availableFormatsDisplay}
                    </Text>
                  </div>
                </div>
              </Card>
            </List.Item>
          )}
        />
      ) : (
        <Empty description="暫無可用報表" />
      )}
    </div>
  );
};
