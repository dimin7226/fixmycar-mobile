import React, { useState } from 'react';
import './HomePage.css';
import ServiceList from '../../components/ServiceList/ServiceList';
import Modal from '../../components/Modal/Modal';

const HomePage = () => {
    const [services] = useState([
        { id: 1, name: 'Автосервис "МоторСити"', rating: 4.5, address: 'ул. Ленина, 10', phone: '+7 (123) 456-7890' },
        { id: 2, name: 'СТО "Мастер"', rating: 4.2, address: 'пр. Мира, 25', phone: '+7 (987) 654-3210' },
    ]);
    const [isModalOpen, setIsModalOpen] = useState(false);

    return (
        <div className="home-page">
            <h1>Найдите лучший автосервис</h1>
            <ServiceList services={services} />
            <Modal visible={isModalOpen} onClose={() => setIsModalOpen(false)} />
        </div>
    );
};

export default HomePage;