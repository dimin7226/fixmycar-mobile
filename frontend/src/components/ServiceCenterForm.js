import React, { useState, useEffect, useCallback } from 'react';
import { Form, Input, Button, message, Card, Space } from 'antd';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';
import PhoneInput from './PhoneInput';

const ServiceCenterForm = () => {
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const { id } = useParams();
  const [loading, setLoading] = useState(false);
  const [initialValues, setInitialValues] = useState(null);

  const fetchServiceCenter = useCallback(async () => {
    try {
      const response = await axios.get(`/api/home/service-centers/${id}`);
      const data = response.data;
      setInitialValues(data);
      form.setFieldsValue(data);
    } catch (error) {
      message.error('Ошибка при загрузке данных сервисного центра');
      console.error('Ошибка:', error);
    }
  }, [id, form]);

  useEffect(() => {
    if (id) {
      fetchServiceCenter();
    }
  }, [id, fetchServiceCenter]);

  const onFinish = async (values) => {
    setLoading(true);
    try {
      const dataToSend = {
        ...values
      };

      if (id) {
        await axios.put(`/api/home/service-centers/${id}`, dataToSend);
        message.success('Сервисный центр успешно обновлен');
      } else {
            await axios.post('/api/home/service-centers', dataToSend);
        message.success('Сервисный центр успешно добавлен');
      }
      navigate('/service-centers');
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
        title={id ? 'Редактировать сервисный центр' : 'Добавить сервисный центр'}
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
            name="name"
            label="Название"
            rules={[{ required: true, message: 'Пожалуйста, введите название' }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="address"
            label="Адрес"
            rules={[{ required: true, message: 'Пожалуйста, введите адрес' }]}
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
                onClick={() => navigate('/service-centers')}
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

export default ServiceCenterForm;