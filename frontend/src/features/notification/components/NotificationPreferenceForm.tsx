/**
 * 通知偏好設定表單元件
 * Domain Code: HR12
 * 自包含元件：內部呼叫 useNotificationPreference hook
 */

import React from 'react';
import {
  Card,
  Switch,
  TimePicker,
  Button,
  Spin,
  Alert,
  Row,
  Col,
  Divider,
  Form,
  message,
  Space,
} from 'antd';
import {
  MailOutlined,
  MobileOutlined,
  BellOutlined,
  MessageOutlined,
  ClockCircleOutlined,
} from '@ant-design/icons';
import { useNotificationPreference } from '../hooks';
import { Typography } from 'antd';
import dayjs from 'dayjs';

const { Text } = Typography;

/**
 * 渠道設定項
 */
const ChannelItem: React.FC<{
  icon: React.ReactNode;
  label: string;
  description: string;
  checked: boolean;
  onChange: (checked: boolean) => void;
}> = ({ icon, label, description, checked, onChange }) => (
  <div
    style={{
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      padding: '16px 0',
      borderBottom: '1px solid #f0f0f0',
    }}
  >
    <Space size="middle">
      <div
        style={{
          width: 40,
          height: 40,
          borderRadius: '50%',
          backgroundColor: checked ? '#e6f7ff' : '#f5f5f5',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          fontSize: 18,
        }}
      >
        {icon}
      </div>
      <div>
        <Text strong>{label}</Text>
        <br />
        <Text type="secondary" style={{ fontSize: 12 }}>
          {description}
        </Text>
      </div>
    </Space>
    <Switch checked={checked} onChange={onChange} />
  </div>
);

export const NotificationPreferenceForm: React.FC = () => {
  const { preference, loading, error, saving, updatePreference } =
    useNotificationPreference();
  const [form] = Form.useForm();

  const handleSave = async () => {
    try {
      const values = form.getFieldsValue();
      const result = await updatePreference({
        email_enabled: values.email_enabled,
        push_enabled: values.push_enabled,
        in_app_enabled: values.in_app_enabled,
        quiet_hours_start: values.quiet_hours
          ? values.quiet_hours[0]?.format('HH:mm')
          : undefined,
        quiet_hours_end: values.quiet_hours
          ? values.quiet_hours[1]?.format('HH:mm')
          : undefined,
      });
      if (result.success) {
        message.success(result.message);
      } else {
        message.error(result.message);
      }
    } catch {
      message.error('儲存設定失敗');
    }
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '100px 0' }}>
        <Spin size="large" />
      </div>
    );
  }

  if (error) {
    return <Alert message="載入失敗" description={error} type="error" showIcon />;
  }

  const initialValues = {
    email_enabled: preference?.emailEnabled ?? true,
    push_enabled: preference?.pushEnabled ?? false,
    in_app_enabled: preference?.inAppEnabled ?? true,
    quiet_hours_enabled: preference?.hasQuietHours ?? false,
    quiet_hours:
      preference?.quietHoursStart && preference?.quietHoursEnd
        ? [
            dayjs(preference.quietHoursStart, 'HH:mm'),
            dayjs(preference.quietHoursEnd, 'HH:mm'),
          ]
        : undefined,
  };

  return (
    <Form form={form} initialValues={initialValues} layout="vertical">
      <Row gutter={24}>
        <Col xs={24} lg={14}>
          <Card title="通知渠道" style={{ marginBottom: 24 }}>
            <Form.Item name="in_app_enabled" valuePropName="checked" noStyle>
              <div style={{ display: 'none' }} />
            </Form.Item>
            <ChannelItem
              icon={<BellOutlined style={{ color: '#1890ff' }} />}
              label="系統內通知"
              description="在系統導航列顯示通知徽章與下拉列表"
              checked={form.getFieldValue('in_app_enabled') ?? initialValues.in_app_enabled}
              onChange={(checked) => form.setFieldsValue({ in_app_enabled: checked })}
            />

            <Form.Item name="email_enabled" valuePropName="checked" noStyle>
              <div style={{ display: 'none' }} />
            </Form.Item>
            <ChannelItem
              icon={<MailOutlined style={{ color: '#52c41a' }} />}
              label="電子郵件"
              description="將通知發送至您的公司郵箱"
              checked={form.getFieldValue('email_enabled') ?? initialValues.email_enabled}
              onChange={(checked) => form.setFieldsValue({ email_enabled: checked })}
            />

            <Form.Item name="push_enabled" valuePropName="checked" noStyle>
              <div style={{ display: 'none' }} />
            </Form.Item>
            <ChannelItem
              icon={<MobileOutlined style={{ color: '#fa8c16' }} />}
              label="推播通知"
              description="透過瀏覽器推播接收即時通知"
              checked={form.getFieldValue('push_enabled') ?? initialValues.push_enabled}
              onChange={(checked) => form.setFieldsValue({ push_enabled: checked })}
            />

            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                padding: '16px 0',
                borderBottom: '1px solid #f0f0f0',
              }}
            >
              <Space size="middle">
                <div
                  style={{
                    width: 40,
                    height: 40,
                    borderRadius: '50%',
                    backgroundColor: '#f5f5f5',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontSize: 18,
                  }}
                >
                  <MessageOutlined style={{ color: '#722ed1' }} />
                </div>
                <div>
                  <Text strong>Teams / LINE</Text>
                  <br />
                  <Text type="secondary" style={{ fontSize: 12 }}>
                    透過 Microsoft Teams 或 LINE 接收通知（需管理員配置）
                  </Text>
                </div>
              </Space>
              <Switch disabled checked={false} />
            </div>
          </Card>
        </Col>

        <Col xs={24} lg={10}>
          <Card title="靜音時段" style={{ marginBottom: 24 }}>
            <Text type="secondary">
              在靜音時段內，系統不會發送推播與郵件通知。系統內通知仍會正常接收。
            </Text>

            <Divider />

            <Form.Item name="quiet_hours_enabled" valuePropName="checked" label="啟用靜音時段">
              <Switch checkedChildren="啟用" unCheckedChildren="停用" />
            </Form.Item>

            <Form.Item
              noStyle
              shouldUpdate={(prevValues, currentValues) =>
                prevValues.quiet_hours_enabled !== currentValues.quiet_hours_enabled
              }
            >
              {({ getFieldValue }) =>
                getFieldValue('quiet_hours_enabled') ? (
                  <Form.Item name="quiet_hours" label="靜音時段範圍">
                    <TimePicker.RangePicker
                      format="HH:mm"
                      placeholder={['開始時間', '結束時間']}
                      style={{ width: '100%' }}
                    />
                  </Form.Item>
                ) : null
              }
            </Form.Item>

            <div
              style={{
                padding: '12px 16px',
                background: '#f6ffed',
                borderRadius: 8,
                marginTop: 16,
              }}
            >
              <ClockCircleOutlined style={{ color: '#52c41a', marginRight: 8 }} />
              <Text style={{ fontSize: 13 }}>
                {preference?.hasQuietHours
                  ? `目前靜音時段：${preference.quietHoursDisplay}`
                  : '目前未設定靜音時段'}
              </Text>
            </div>
          </Card>

          <Card title="通知統計">
            <Text type="secondary">
              最後更新：{preference?.updatedAtDisplay ?? '尚無資料'}
            </Text>
          </Card>
        </Col>
      </Row>

      <div style={{ textAlign: 'right', marginTop: 16 }}>
        <Button type="primary" size="large" onClick={handleSave} loading={saving}>
          儲存設定
        </Button>
      </div>
    </Form>
  );
};
