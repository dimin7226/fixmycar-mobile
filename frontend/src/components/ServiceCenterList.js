import React, { useState, useEffect } from 'react';
import { Table, Button, Space, message, Card, Tag } from 'antd';
import { EditOutlined, DeleteOutlined } from '@ant-design/icons';
import axios from '../api/api';

const ServiceCenterList = () => {
  const [serviceCenters, setServiceCenters] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchServiceCenters();
  }, []);

  const fetchServiceCenters = async () => {
    setLoading(true);
    try {
      const response = await axios.get('/api/home/service-centers');
      setServiceCenters(response.data);
    } catch (error) {
      message.error('Ошибка при загрузке списка сервисных центров');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    try {
      await axios.delete(`/api/home/service-centers/${id}`);
      message.success('Сервисный центр успешно удален');
      fetchServiceCenters();
    } catch (error) {
      message.error('Ошибка при удалении сервисного центра');
    }
  };

  const columns = [
    {
      title: 'Название',
      dataIndex: 'name',
      key: 'name',
      width: 200,
      render: (text) => <span className="text-bold">{text}</span>,
    },
    {
      title: 'Адрес',
      dataIndex: 'address',
      key: 'address',
      width: 300,
      render: (text) => <span className="text-regular">{text}</span>,
    },
    {
      title: 'Телефон',
      dataIndex: 'phone',
      key: 'phone',
      width: 150,
      render: (text) => <a href={`tel:${text}`} className="link">{text}</a>,
    },
    {
      title: 'Действия',
      key: 'actions',
      width: 120,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button
            type="text"
            icon={<EditOutlined style={{ color: '#1890ff', fontSize: '16px' }} />}
            href={`/service-centers/edit/${record.id}`}
          />
          <Button
            type="text"
            icon={<DeleteOutlined style={{ color: '#ff4d4f', fontSize: '16px' }} />}
            onClick={() => handleDelete(record.id)}
          />
        </Space>
      ),
    },
  ];

  return (
      <div style={{ padding: '24px' }}>
        <Card
            title={<span style={{ fontSize: '18px', fontWeight: '500' }}>Список сервисных центров</span>}
            bordered={false}
            extra={
              <Button type="primary" href="/service-centers/add" size="middle">
                Добавить сервисный центр
              </Button>
            }
            style={{ boxShadow: '0 2px 8px rgba(0, 0, 0, 0.09)' }}
        >
          <Table
              columns={columns}
              dataSource={serviceCenters}
              rowKey="id"
              loading={loading}
              pagination={{
                pageSize: 10,
                showSizeChanger: false,
                showTotal: (total) => `Всего ${total} сервисных центров`
              }}
              scroll={{ x: 900 }}
              style={{ marginTop: '16px' }}
              rowClassName={() => 'table-row'}
          />
        </Card>

        <style jsx global>{`
        .text-bold {
          font-weight: 500;
        }
        .text-regular {
          color: #595959;
        }
        .link {
          color: #1890ff;
          text-decoration: none;
        }
        .link:hover {
          text-decoration: underline;
        }
        .ant-table .table-row:hover td {
          background: #f5f5f5 !important;
        }
        .ant-table-thead > tr > th {
          background: #f0f2f5;
          font-weight: 600;
          color: #262626;
        }
        .ant-card-head-title {
          padding: 16px 0;
        }
        .ant-table-tbody > tr > td {
          padding: 16px 8px;
        }
      `}</style>
      </div>
  );
};

export default ServiceCenterList;