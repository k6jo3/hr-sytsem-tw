import { Button, Result } from 'antd';
import React from 'react';
import { useNavigate } from 'react-router-dom';

/**
 * 404 頁面不存在
 * 當使用者訪問不存在的路由時顯示此頁面
 */
const NotFoundPage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div style={{
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      minHeight: '60vh',
    }}>
      <Result
        status="404"
        title="404 - 頁面不存在"
        subTitle="抱歉，您訪問的頁面不存在或已被移除。"
        extra={[
          <Button
            type="primary"
            key="home"
            onClick={() => navigate('/dashboard')}
          >
            返回首頁
          </Button>,
          <Button
            key="back"
            onClick={() => navigate(-1)}
          >
            返回上一頁
          </Button>,
        ]}
      />
    </div>
  );
};

export default NotFoundPage;
