/**
 * 功能開關管理 Tab
 * Domain Code: HR01
 */

import { Button, Card, Col, message, Row, Switch, Tag, Typography } from 'antd';
import React from 'react';
import type { FeatureToggleViewModel } from '../model/SystemViewModel';

const { Text, Paragraph } = Typography;

interface FeatureToggleTabProps {
  features: FeatureToggleViewModel[];
  loading: boolean;
  onToggle: (featureCode: string, enabled: boolean) => Promise<void>;
  onRefresh: () => Promise<void>;
}

export const FeatureToggleTab: React.FC<FeatureToggleTabProps> = ({
  features, loading, onToggle, onRefresh,
}) => {
  const handleToggle = async (featureCode: string, checked: boolean) => {
    try {
      await onToggle(featureCode, checked);
      message.success(`功能已${checked ? '啟用' : '停用'}`);
    } catch {
      message.error('切換功能開關失敗');
    }
  };

  // 依模組分組
  const grouped = features.reduce<Record<string, FeatureToggleViewModel[]>>((acc, f) => {
    const key = f.moduleLabel;
    if (!acc[key]) acc[key] = [];
    acc[key].push(f);
    return acc;
  }, {});

  return (
    <div>
      <div style={{ marginBottom: 16, textAlign: 'right' }}>
        <Button size="small" onClick={onRefresh}>重新整理</Button>
      </div>
      {Object.entries(grouped).map(([moduleLabel, items]) => (
        <Card key={moduleLabel} title={moduleLabel} size="small" style={{ marginBottom: 16 }}>
          <Row gutter={[16, 16]}>
            {items.map(feature => (
              <Col key={feature.featureCode} xs={24} sm={12} lg={8}>
                <Card
                  size="small"
                  hoverable
                  style={{
                    borderLeft: `3px solid ${feature.enabled ? '#52c41a' : '#d9d9d9'}`,
                  }}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                    <div style={{ flex: 1 }}>
                      <Text strong>{feature.featureName}</Text>
                      <Tag style={{ marginLeft: 8 }} color={feature.enabled ? 'success' : 'default'}>
                        {feature.enabled ? '啟用' : '停用'}
                      </Tag>
                      <Paragraph
                        type="secondary"
                        style={{ fontSize: 12, marginTop: 4, marginBottom: 0 }}
                        ellipsis={{ rows: 2 }}
                      >
                        {feature.description}
                      </Paragraph>
                    </div>
                    <Switch
                      checked={feature.enabled}
                      loading={loading}
                      onChange={(checked) => handleToggle(feature.featureCode, checked)}
                    />
                  </div>
                </Card>
              </Col>
            ))}
          </Row>
        </Card>
      ))}
    </div>
  );
};
