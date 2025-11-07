import React, { useState, useEffect } from 'react';
import { Table, Button, Space, message, Card, Tag } from 'antd';
import { EditOutlined, DeleteOutlined } from '@ant-design/icons';
import axios from '../api/api';

const CustomerList = () => {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchCustomers();
  }, []);

  const fetchCustomers = async () => {
    setLoading(true);
    try {
      const response = await axios.get('/api/home/customers');
      // Форматируем телефоны перед установкой в состояние
      const formattedCustomers = response.data.map(customer => ({
        ...customer,
        // Если телефон не отформатирован - форматируем его
        phone: formatPhoneNumber(customer.phone)
      }));
      setCustomers(formattedCustomers);
    } catch (error) {
      message.error('Ошибка при загрузке списка клиентов');
    } finally {
      setLoading(false);
    }
  };

  // Функция для форматирования телефона
  const formatPhoneNumber = (phone) => {
    if (!phone) return '';

    // Если телефон уже отформатирован - возвращаем как есть
    if (phone.includes('(') && phone.includes(')')) {
      return phone;
    }

    // Удаляем все нецифровые символы
    const cleaned = phone.replace(/\D/g, '');

    // Форматируем по шаблону +375 (__) ___ __ __
    const match = cleaned.match(/^(\d{3})(\d{2})(\d{3})(\d{2})(\d{2})$/);
    if (match) {
      return `+${match[1]} (${match[2]}) ${match[3]}-${match[4]}-${match[5]}`;
    }

    // Если номер не полный - возвращаем как есть
    return phone;
  };

  const handleDelete = async (id) => {
    try {
      await axios.delete(`/api/home/customers/${id}`);
      message.success('Клиент успешно удален');
      fetchCustomers();
    } catch (error) {
      message.error('Ошибка при удалении клиента');
    }
  };

  const columns = [
    {
      title: 'Имя',
      dataIndex: 'firstName',
      key: 'firstName',
      width: 150,
      render: (text) => <span className="text-bold">{text}</span>,
    },
    {
      title: 'Фамилия',
      dataIndex: 'lastName',
      key: 'lastName',
      width: 150,
      render: (text) => <span className="text-bold">{text}</span>,
    },
    {
      title: 'Email',
      dataIndex: 'email',
      key: 'email',
      width: 200,
      render: (text) => <a href={`mailto:${text}`} className="link">{text}</a>,
    },
    {
      title: 'Телефон',
      dataIndex: 'phone',
      key: 'phone',
      width: 180,
      render: (text) => (
        <a href={`tel:${text.replace(/\D/g, '')}`} className="link">
          {formatPhoneNumber(text)}
        </a>
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
            href={`/customers/edit/${record.id}`}
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
            title={<span style={{ fontSize: '18px', fontWeight: '500' }}>Список клиентов</span>}
            bordered={false}
            extra={
              <Button type="primary" href="/customers/add" size="middle">
                Добавить клиента
              </Button>
            }
            style={{ boxShadow: '0 2px 8px rgba(0, 0, 0, 0.09)' }}
        >
          <Table
              columns={columns}
              dataSource={customers}
              rowKey="id"
              loading={loading}
              pagination={{
                pageSize: 10,
                showSizeChanger: false,
                showTotal: (total) => `Всего ${total} клиентов`
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

export default CustomerList;