/**
 * HR02-P01 - 組織架構頁面
 * Domain Code: HR02
 * Page Code: HR02-P01
 * Displays organization tree and structure management
 */
import { Card } from 'antd';
import React from 'react';
import { OrganizationTreeView } from '../features/organization/components/OrganizationTreeView'; // Ensure component import path is correct

export const HR02OrganizationTreePage: React.FC = () => {
    return (
        <Card title="部門與組織架構管理" bordered={false}>
            <OrganizationTreeView />
        </Card>
    );
};
