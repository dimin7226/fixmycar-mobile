import React, { useState, useEffect } from 'react';
import { Table, Button, Space, message, Card, Tag } from 'antd';
import { EditOutlined, DeleteOutlined } from '@ant-design/icons';
import axios from 'axios';

const ServiceRequestList = () => {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchRequests();
  }, []);

  const fetchRequests = async () => {
    setLoading(true);
    try {
      const response = await axios.get('/api/home/requests');
      setRequests(response.data);
    } catch (error) {
      message.error('Ошибка при загрузке списка заявок');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    try {
      await axios.delete(`/api/home/requests/${id}`);
      message.success('Заявка успешно удалена');
      fetchRequests();
    } catch (error) {
      message.error('Ошибка при удалении заявки');
    }
  };

  const getStatusColor = (status) => {
    const colors = {
      'PENDING': 'orange',
      'IN_PROGRESS': 'blue',
      'COMPLETED': 'green',
      'CANCELLED': 'red'
    };
    return colors[status] || 'default';
  };

  const getStatusText = (status) => {
    const statuses = {
      'PENDING': 'Ожидает',
      'IN_PROGRESS': 'В работе',
      'COMPLETED': 'Завершено',
      'CANCELLED': 'Отменено'
    };
    return statuses[status] || status;
  };

  const columns = [
    {
      title: 'Автомобиль',
      key: 'car',
      width: 180,
      render: (_, record) => (
          <span className="text-bold">
          {record.car?.brand} {record.car?.model}
        </span>
      ),
    },
    {
      title: 'Сервисный центр',
      key: 'serviceCenter',
      width: 200,
      render: (_, record) => (
        <span className="text-bold">
          {record.serviceCenter?.name}
        </span>
      ),
    },
    {
      title: 'Описание',
      dataIndex: 'description',
      key: 'description',
      width: 250,
      render: (text) => text || <span style={{ color: '#bfbfbf' }}>Нет описания</span>,
    },
    {
      title: 'Статус',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (status) => (
        <Tag color={getStatusColor(status)} style={{ 
          fontWeight: 500, 
          padding: '4px 8px',
          minWidth: '100px',
          textAlign: 'center',
          display: 'inline-block'
        }}>
          {getStatusText(status)}
        </Tag>
      ),
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
            href={`/requests/edit/${record.id}`}
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
            title={<span style={{ fontSize: '18px', fontWeight: '500' }}>Список заявок на ремонт</span>}
            bordered={false}
            extra={
              <Button type="primary" href="/requests/add" size="middle">
                Создать заявку
              </Button>
            }
            style={{ boxShadow: '0 2px 8px rgba(0, 0, 0, 0.09)' }}
        >
          <Table
              columns={columns}
              dataSource={requests}
              rowKey="id"
              loading={loading}
              pagination={{
                pageSize: 10,
                showSizeChanger: false,
                showTotal: (total) => `Всего ${total} заявок`
              }}
              scroll={{ x: 1000 }}
              style={{ marginTop: '16px' }}
              rowClassName={() => 'table-row'}
          />
        </Card>

        <style jsx global>{`
          .text-bold {
            font-weight: 500;
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
          .ant-tag {
            border-radius: 4px;
          }
        `}</style>
      </div>
  );
};

export default ServiceRequestList;