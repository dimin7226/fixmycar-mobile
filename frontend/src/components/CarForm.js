import React, { useState, useEffect } from 'react';
import { Form, Input, Button, message, Card, Space, Select, DatePicker } from 'antd';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';
import dayjs from 'dayjs';

const { Option } = Select;

const CarForm = () => {
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const { id } = useParams();
  const [loading, setLoading] = useState(false);
  const [initialValues, setInitialValues] = useState(null);
  const [customers, setCustomers] = useState([]);

  useEffect(() => {
    fetchCustomers();
    if (id) {
      fetchCar();
    }
  }, [id]);

  const fetchCustomers = async () => {
    try {
      const response = await axios.get('/api/home/customers');
      setCustomers(response.data);
    } catch (error) {
      message.error('Ошибка при загрузке списка клиентов');
    }
  };

  const fetchCar = async () => {
    try {
      const response = await axios.get(`/api/home/cars/${id}`);
      const carData = response.data;
      setInitialValues({
        ...carData,
        year: dayjs(carData.year.toString()),
        customerId: carData.customer?.id
      });
      form.setFieldsValue({
        ...carData,
        year: dayjs(carData.year.toString()),
        customerId: carData.customer?.id
      });
    } catch (error) {
      message.error('Ошибка при загрузке данных автомобиля');
    }
  };

  const onFinish = async (values) => {
    setLoading(true);
    try {
      const dataToSend = {
        ...values,
        year: values.year.year(),
        customer: { id: values.customerId }
      };

      if (id) {
        await axios.put(`/api/home/cars/${id}`, dataToSend);
        message.success('Автомобиль успешно обновлен');
      } else {
        await axios.post('/api/home/cars', dataToSend);
        message.success('Автомобиль успешно добавлен');
      }
      navigate('/cars');
    } catch (error) {
      message.error('Ошибка при сохранении данных');
      console.error('Ошибка:', error.response?.data || error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: '24px 0' }}>
      <Card
        title={id ? 'Редактировать автомобиль' : 'Добавить автомобиль'}
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
            name="customerId"
            label="Владелец"
            rules={[{ required: true, message: 'Пожалуйста, выберите владельца' }]}
          >
            <Select placeholder="Выберите владельца">
              {customers.map(customer => (
                <Option key={customer.id} value={customer.id}>
                  {`${customer.firstName} ${customer.lastName}`}
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="brand"
            label="Марка"
            rules={[{ required: true, message: 'Пожалуйста, введите марку' }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="model"
            label="Модель"
            rules={[{ required: true, message: 'Пожалуйста, введите модель' }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="year"
            label="Год выпуска"
            rules={[{ required: true, message: 'Пожалуйста, выберите год' }]}
          >
            <DatePicker
              picker="year"
              style={{ width: '100%' }}
              disabledDate={current => {
                return current && (current.year() < 1980 || current.year() > 2025);
              }}
            />
          </Form.Item>

          <Form.Item
            name="vin"
            label="VIN номер"
            rules={[
              { required: true, message: 'Пожалуйста, введите VIN номер' },
              { 
                pattern: /^[A-HJ-NPR-Z0-9]{17}$/,
                message: 'VIN должен содержать 17 символов (буквы и цифры)'
              }
            ]}
          >
            <Input 
              placeholder="17-значный номер" 
              maxLength={17}
              style={{ textTransform: 'uppercase' }}
              onChange={e => {
                const value = e.target.value.toUpperCase();
                form.setFieldsValue({ vin: value });
              }}
            />
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
                onClick={() => navigate('/cars')}
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

export default CarForm;