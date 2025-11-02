import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link, useLocation } from 'react-router-dom';
import { Layout, Menu, Typography, Button, message } from 'antd';
import { CarOutlined, ToolOutlined, UserOutlined, ShopOutlined } from '@ant-design/icons';
import CarList from './components/CarList';
import CarForm from './components/CarForm';
import ServiceRequestList from './components/ServiceRequestList';
import ServiceRequestForm from './components/ServiceRequestForm';
import CustomerList from './components/CustomerList';
import CustomerForm from './components/CustomerForm';
import ServiceCenterList from './components/ServiceCenterList';
import ServiceCenterForm from './components/ServiceCenterForm';
import 'antd/dist/reset.css';
import './App.css';

const { Header, Content, Sider } = Layout;
const { Title } = Typography;

const AppContent = () => {
    const location = useLocation();
    const [selectedKey, setSelectedKey] = useState('1');

    useEffect(() => {
        const path = location.pathname;
        if (path.startsWith('/cars')) setSelectedKey('1');
        else if (path.startsWith('/requests')) setSelectedKey('2');
        else if (path.startsWith('/customers')) setSelectedKey('3');
        else if (path.startsWith('/service-centers')) setSelectedKey('4');
    }, [location]);

    const handleMenuClick = (e) => {
        setSelectedKey(e.key);
    };

    const handleLogoClick = () => {
        setSelectedKey('1');
    };

    const handleProfileClick = () => {
        console.log('Profile button clicked'); // Добавьте эту строку для проверки
        message.error('Извините! Данная опция в данный момент недоступна');
    };

    return (
        <Layout style={{ minHeight: '100vh' }}>
            <Header className="header">
                <div className="header-content">
                    <Link to="/" className="logo" onClick={handleLogoClick}>
                        <CarOutlined style={{ fontSize: 24, marginRight: 8 }} />
                        <Title level={4} style={{ color: 'white', margin: 0 }}>FixMyCar</Title>
                    </Link>
                    <div className="profile-button-container">
                        <Button
                            type="text"
                            icon={<UserOutlined style={{ fontSize: 24, color: 'white' }} />}
                            onClick={handleProfileClick}
                            className="profile-button"
                        />
                    </div>
                </div>
            </Header>
            <Layout>
                <Sider width={200} className="site-layout-background">
                    <Menu
                        className="custom-menu"
                        mode="inline"
                        selectedKeys={[selectedKey]}
                        onClick={handleMenuClick}
                        style={{ height: '100%', borderRight: 0 }}
                    >
                        <Menu.Item key="1" icon={<CarOutlined />}>
                            <Link to="/cars">Автомобили</Link>
                        </Menu.Item>
                        <Menu.Item key="2" icon={<ToolOutlined />}>
                            <Link to="/requests">Заявки на ремонт</Link>
                        </Menu.Item>
                        <Menu.Item key="3" icon={<UserOutlined />}>
                            <Link to="/customers">Клиенты</Link>
                        </Menu.Item>
                        <Menu.Item key="4" icon={<ShopOutlined />}>
                            <Link to="/service-centers">Сервисные центры</Link>
                        </Menu.Item>
                    </Menu>
                </Sider>
                <Layout style={{ padding: '24px' }}>
                    <Content className="site-layout-background" style={{ padding: 24, margin: 0, minHeight: 280 }}>
                        <Routes>
                            <Route path="/cars" element={<CarList />} />
                            <Route path="/cars/add" element={<CarForm />} />
                            <Route path="/cars/edit/:id" element={<CarForm />} />
                            <Route path="/requests" element={<ServiceRequestList />} />
                            <Route path="/requests/add" element={<ServiceRequestForm />} />
                            <Route path="/requests/edit/:id" element={<ServiceRequestForm />} />
                            <Route path="/customers" element={<CustomerList />} />
                            <Route path="/customers/add" element={<CustomerForm />} />
                            <Route path="/customers/edit/:id" element={<CustomerForm />} />
                            <Route path="/service-centers" element={<ServiceCenterList />} />
                            <Route path="/service-centers/add" element={<ServiceCenterForm />} />
                            <Route path="/service-centers/edit/:id" element={<ServiceCenterForm />} />
                            <Route path="/" element={<CarList />} />
                        </Routes>
                    </Content>
                </Layout>
            </Layout>
        </Layout>
    );
};

function App() {
    return (
        <Router>
            <AppContent />
        </Router>
    );
}

export default App;