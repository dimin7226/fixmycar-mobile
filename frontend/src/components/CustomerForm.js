import React, { useState, useEffect, useCallback } from 'react';
import { Form, Input, Button, message, Card, Space } from 'antd';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';
import PhoneInput from './PhoneInput';

const CustomerForm = () => {
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const { id } = useParams();
  const [loading, setLoading] = useState(false);
  const [initialValues, setInitialValues] = useState(null);

  const fetchCustomer = useCallback(async () => {
    try {
      const response = await axios.get(`/api/home/customers/${id}`);
      const data = response.data;
      setInitialValues(data);
      form.setFieldsValue(data);
    } catch (error) {
      message.error('Ошибка при загрузке данных клиента');
      console.error('Ошибка:', error);
    }
  }, [id, form]);

  useEffect(() => {
    if (id) {
      fetchCustomer();
    }
  }, [id, fetchCustomer]);

  const onFinish = async (values) => {
    setLoading(true);
    try {
      const data = {
        ...values
      };

      if (id) {
        await axios.put(`/api/home/customers/${id}`, data);
        message.success('Клиент успешно обновлен');
      } else {
        await axios.post('/api/home/customers', data);
        message.success('Клиент успешно добавлен');
      }
      navigate('/customers');
    } catch (error) {
      const errorMessage = error.response?.data?.message || 'Ошибка при сохранении данных';
      message.error(errorMessage);
      console.error('Ошибка:', error.response?.data || error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: '24px 0' }}>
      <Card
        title={id ? 'Редактировать клиента' : 'Добавить клиента'}
        bordered={false}
        style={{
          maxWidth: 600,
          margin: '0 auto',
          boxShadow: '0 1px 2px rgba(0, 0, 0, 0.1)'
        }}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={onFinish}
          initialValues={initialValues || {}}
        >
          <Form.Item
            name="firstName"
            label="Имя"
            rules={[{ required: true, message: 'Пожалуйста, введите имя' }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="lastName"
            label="Фамилия"
            rules={[{ required: true, message: 'Пожалуйста, введите фамилию' }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="email"
            label="Email"
            rules={[
              { required: true, message: 'Пожалуйста, введите email' },
              { type: 'email', message: 'Введите корректный email' }
            ]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="phone"
            label="Номер телефона"
            rules={[{ required: true, message: 'Пожалуйста, введите номер телефона' }]}
          >
            <PhoneInput placeholder="+375 (XX) XXX-XX-XX" />
          </Form.Item>

          <Form.Item>
            <Space>
              <Button
                type="primary"
                htmlType="submit"
                loading={loading}
                style={{ width: 120 }}
              >
                {id ? 'Сохранить' : 'Добавить'}
              </Button>
              <Button
                onClick={() => navigate('/customers')}
                style={{ width: 120 }}
              >
                Отмена
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default CustomerForm;