import React, { useState } from 'react';
import './AdminPanel.css';
import { Button, Form, Input, Select } from 'antd';
import TagCloud from '../../components/TagCloud/TagCloud';

const { Option } = Select; // Добавлен деструктурирующий импорт

const AdminPanel = () => {
    const [services, setServices] = useState([
        { id: 1, name: 'Автосервис "МоторСити"', services: ['Диагностика', 'Ремонт двигателя'] },
    ]);
    const [editingService, setEditingService] = useState(null);

    const handleDelete = (id) => {
        setServices(services.filter(service => service.id !== id));
    };

    const handleSubmit = (values) => {
        if (editingService) {
            setServices(services.map(s => s.id === editingService.id ? { ...s, ...values } : s));
        } else {
            setServices([...services, { id: Date.now(), ...values }]);
        }
        setEditingService(null);
    };

    return (
        <div className="admin-panel">
            <h2>Управление сервисами</h2>
            <Form onFinish={handleSubmit} initialValues={editingService || {}}>
                <Form.Item name="name" label="Название сервиса">
                    <Input />
                </Form.Item>
                <Form.Item name="services" label="Услуги">
                    <Select mode="tags" placeholder="Выберите услуги">
                        <Option value="Диагностика">Диагностика</Option>
                        <Option value="Ремонт двигателя">Ремонт двигателя</Option>
                    </Select>
                </Form.Item>
                <Button type="primary" htmlType="submit">
                    {editingService ? 'Обновить' : 'Добавить'}
                </Button>
            </Form>

            <div className="services-list">
                {services.map(service => (
                    <div key={service.id} className="service-item">
                        <h3>{service.name}</h3>
                        <TagCloud tags={service.services} />
                        <div className="actions">
                            <Button onClick={() => setEditingService(service)}>✏️</Button>
                            <Button danger onClick={() => handleDelete(service.id)}>🗑️</Button>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default AdminPanel;