/**
 * HR01 密碼修改頁面
 * 頁面代碼：HR01-P04
 * 路由：/profile/password
 */

import React, { useState } from 'react';
import {
  Layout,
  Card,
  Form,
  Input,
  Button,
  Typography,
  Space,
  message,
  Progress,
  List,
} from 'antd';
import {
  LockOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
} from '@ant-design/icons';
import { AuthApi } from '@features/auth/api/AuthApi';

const { Content } = Layout;
const { Title, Text, Paragraph } = Typography;

interface PasswordFormValues {
  old_password: string;
  new_password: string;
  confirm_password: string;
}

interface PasswordStrength {
  score: number;
  label: string;
  color: string;
}

/**
 * 計算密碼強度
 */
const calculatePasswordStrength = (password: string): PasswordStrength => {
  let score = 0;

  if (password.length >= 8) score += 25;
  if (password.length >= 12) score += 10;
  if (/[a-z]/.test(password)) score += 15;
  if (/[A-Z]/.test(password)) score += 15;
  if (/\d/.test(password)) score += 15;
  if (/[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]/.test(password)) score += 20;

  if (score < 40) return { score, label: '弱', color: '#ff4d4f' };
  if (score < 70) return { score, label: '中', color: '#faad14' };
  return { score, label: '強', color: '#52c41a' };
};

/**
 * 密碼驗證規則
 */
const passwordRules = [
  { rule: (pwd: string) => pwd.length >= 8, label: '至少 8 個字元' },
  { rule: (pwd: string) => /[a-z]/.test(pwd), label: '包含小寫字母' },
  { rule: (pwd: string) => /[A-Z]/.test(pwd), label: '包含大寫字母' },
  { rule: (pwd: string) => /\d/.test(pwd), label: '包含數字' },
];

/**
 * 密碼修改頁面
 */
const HR01PasswordChangePage: React.FC = () => {
  const [form] = Form.useForm<PasswordFormValues>();
  const [loading, setLoading] = useState(false);
  const [newPassword, setNewPassword] = useState('');

  const passwordStrength = calculatePasswordStrength(newPassword);

  const handleSubmit = async (values: PasswordFormValues) => {
    setLoading(true);
    try {
      await AuthApi.changePassword({
        old_password: values.old_password,
        new_password: values.new_password,
        confirm_password: values.confirm_password,
      });
      message.success('密碼變更成功！');
      form.resetFields();
      setNewPassword('');
    } catch (err) {
      if (err instanceof Error) {
        if (err.message.includes('incorrect') || err.message.includes('wrong')) {
          message.error('目前密碼不正確');
        } else {
          message.error(err.message || '密碼變更失敗');
        }
      } else {
        message.error('密碼變更失敗');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout style={{ minHeight: '100vh', background: '#f0f2f5' }}>
      <Content style={{ padding: 24, display: 'flex', justifyContent: 'center' }}>
        <Card style={{ width: '100%', maxWidth: 600 }}>
          <Space direction="vertical" size="large" style={{ width: '100%' }}>
            {/* 頁面標題 */}
            <div>
              <Title level={4}>
                <LockOutlined style={{ marginRight: 8 }} />
                變更密碼
              </Title>
              <Paragraph type="secondary">
                定期變更密碼可以增加帳號安全性。新密碼必須符合以下規則。
              </Paragraph>
            </div>

            {/* 密碼規則檢查 */}
            <Card size="small" title="密碼規則" style={{ backgroundColor: '#fafafa' }}>
              <List
                size="small"
                dataSource={passwordRules}
                renderItem={(item) => {
                  const passed = item.rule(newPassword);
                  return (
                    <List.Item style={{ padding: '4px 0', border: 'none' }}>
                      <Space>
                        {passed ? (
                          <CheckCircleOutlined style={{ color: '#52c41a' }} />
                        ) : (
                          <CloseCircleOutlined style={{ color: '#d9d9d9' }} />
                        )}
                        <Text type={passed ? undefined : 'secondary'}>
                          {item.label}
                        </Text>
                      </Space>
                    </List.Item>
                  );
                }}
              />
            </Card>

            {/* 密碼表單 */}
            <Form
              form={form}
              layout="vertical"
              onFinish={handleSubmit}
              autoComplete="off"
            >
              <Form.Item
                name="old_password"
                label="目前密碼"
                rules={[{ required: true, message: '請輸入目前密碼' }]}
              >
                <Input.Password
                  placeholder="請輸入目前密碼"
                  size="large"
                />
              </Form.Item>

              <Form.Item
                name="new_password"
                label="新密碼"
                rules={[
                  { required: true, message: '請輸入新密碼' },
                  { min: 8, message: '密碼至少 8 個字元' },
                  {
                    pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/,
                    message: '密碼必須包含大小寫字母和數字',
                  },
                  ({ getFieldValue }) => ({
                    validator(_, value) {
                      if (!value || getFieldValue('old_password') !== value) {
                        return Promise.resolve();
                      }
                      return Promise.reject(new Error('新密碼不能與目前密碼相同'));
                    },
                  }),
                ]}
              >
                <Input.Password
                  placeholder="請輸入新密碼"
                  size="large"
                  onChange={(e) => setNewPassword(e.target.value)}
                />
              </Form.Item>

              {/* 密碼強度指示器 */}
              {newPassword && (
                <div style={{ marginBottom: 24 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 4 }}>
                    <Text type="secondary">密碼強度：</Text>
                    <Text style={{ color: passwordStrength.color }}>
                      {passwordStrength.label}
                    </Text>
                  </div>
                  <Progress
                    percent={passwordStrength.score}
                    showInfo={false}
                    strokeColor={passwordStrength.color}
                    size="small"
                  />
                </div>
              )}

              <Form.Item
                name="confirm_password"
                label="確認新密碼"
                dependencies={['new_password']}
                rules={[
                  { required: true, message: '請再次輸入新密碼' },
                  ({ getFieldValue }) => ({
                    validator(_, value) {
                      if (!value || getFieldValue('new_password') === value) {
                        return Promise.resolve();
                      }
                      return Promise.reject(new Error('兩次輸入的密碼不一致'));
                    },
                  }),
                ]}
              >
                <Input.Password
                  placeholder="請再次輸入新密碼"
                  size="large"
                />
              </Form.Item>

              <Form.Item style={{ marginBottom: 0, marginTop: 24 }}>
                <Button
                  type="primary"
                  htmlType="submit"
                  loading={loading}
                  size="large"
                  block
                >
                  變更密碼
                </Button>
              </Form.Item>
            </Form>
          </Space>
        </Card>
      </Content>
    </Layout>
  );
};

export default HR01PasswordChangePage;
