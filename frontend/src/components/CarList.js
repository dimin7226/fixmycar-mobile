import React, { useState, useEffect } from 'react';
import { Table, Button, Space, message, Card, Tag } from 'antd';
import { EditOutlined, DeleteOutlined } from '@ant-design/icons';
import axios from 'axios';

const CarList = () => {
  const [cars, setCars] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchCars();
  }, []);

  const fetchCars = async () => {
    setLoading(true);
    try {
      const response = await axios.get('/api/home/cars');
      setCars(response.data);
    } catch (error) {
      message.error('Ошибка при загрузке списка автомобилей');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    try {
      await axios.delete(`/api/home/cars/${id}`);
      message.success('Автомобиль успешно удален');
      fetchCars();
    } catch (error) {
      message.error('Ошибка при удалении автомобиля');
    }
  };

  const columns = [
    {
      title: 'Марка',
      dataIndex: 'brand',
      key: 'brand',
      width: 150,
      render: (text) => <span className="text-bold">{text}</span>,
    },
    {
      title: 'Модель',
      dataIndex: 'model',
      key: 'model',
      width: 150,
    },
    {
      title: 'Год выпуска',
      dataIndex: 'year',
      key: 'year',
      width: 120,
      align: 'center',
      render: (text) => <Tag color="blue">{text}</Tag>,
    },
    {
      title: 'VIN-код',
      dataIndex: 'vin',
      key: 'vin',
      width: 200,
      render: (text) => <span className="text-mono">{text}</span>,
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
                icon={<EditOutlined style={{ color: '#1890ff' }} />}
                href={`/cars/edit/${record.id}`}
            />
            <Button
                type="text"
                icon={<DeleteOutlined style={{ color: '#ff4d4f' }} />}
                onClick={() => handleDelete(record.id)}
            />
          </Space>
      ),
    },
  ];

  return (
      <div style={{ padding: '24px' }}>
        <Card
            title={<span style={{ fontSize: '18px', fontWeight: '500' }}>Список автомобилей</span>}
            bordered={false}
            extra={
              <Button type="primary" href="/cars/add" size="middle">
                Добавить автомобиль
              </Button>
            }
            style={{ boxShadow: '0 2px 8px rgba(0, 0, 0, 0.09)' }}
        >
          <Table
              columns={columns}
              dataSource={cars}
              rowKey="id"
              loading={loading}
              pagination={{
                pageSize: 10,
                showSizeChanger: false,
                showTotal: (total) => `Всего ${total} автомобилей`
              }}
              scroll={{ x: 800 }}
              style={{ marginTop: '16px' }}
              rowClassName={() => 'table-row'}
          />
        </Card>

        <style jsx global>{`
        .text-bold {
          font-weight: 500;
        }
        .text-mono {
          font-family: monospace;
        }
        .ant-table .table-row:hover td {
          background: #fafafa !important;
        }
        .ant-table-thead > tr > th {
          background: #f8f8f8;
          font-weight: 500;
        }
        .ant-card-head-title {
          padding: 16px 0;
        }
      `}</style>
      </div>
  );
};

export default CarList;