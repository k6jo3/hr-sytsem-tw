import { LoginForm } from '@features/auth';
import { Col, Layout, Row } from 'antd';
import React from 'react';
import { useNavigate } from 'react-router-dom';

const { Content } = Layout;

/**
 * HR01 登入頁面
 * 頁面代碼：HR01-P01
 */
const HR01LoginPage: React.FC = () => {
  const navigate = useNavigate();

  const handleLoginSuccess = () => {
    navigate('/employees');
  };

  return (
    <Layout style={{ minHeight: '100vh', background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' }}>
      <Content>
        <Row justify="center" align="middle" style={{ minHeight: '100vh' }}>
          <Col xs={22} sm={18} md={12} lg={8} xl={6}>
            <LoginForm onSuccess={handleLoginSuccess} />
          </Col>
        </Row>
      </Content>
    </Layout>
  );
};

export default HR01LoginPage;
