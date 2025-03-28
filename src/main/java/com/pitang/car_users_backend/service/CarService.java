package com.pitang.car_users_backend.service;

import com.pitang.car_users_backend.model.Car;

import java.util.List;

public interface CarService {
    Car createCar(Car car);
    Car updateCar(Long id, Car car);
    void deleteCar(Long id);
    List<Car> getAllCars();
    Car getCarById(Long id);
}
