package com.fixmycar.service;

import com.fixmycar.dao.CarDao;
import com.fixmycar.model.Car;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarServiceImpl implements CarService {

    private final CarDao carDao;

    @Autowired
    public CarServiceImpl(CarDao carDao) {
        this.carDao = carDao;
    }

    @Override
    public Optional<Car> getCarById(Long id) {
        return carDao.findById(id);
    }
}