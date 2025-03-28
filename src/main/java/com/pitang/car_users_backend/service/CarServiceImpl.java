package com.pitang.car_users_backend.service;

import com.pitang.car_users_backend.model.Car;
import com.pitang.car_users_backend.repository.CarRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository repository;

    public CarServiceImpl(CarRepository repository) {
        this.repository = repository;
    }

    @Override
    public Car createCar(Car car) {
        return repository.save(car);
    }

    @Override
    public Car updateCar(Long id, Car car) {
        car.setId(id);
        return repository.save(car);
    }

    @Override
    public void deleteCar(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Car> getAllCars() {
        return repository.findAll();
    }

    @Override
    public Car getCarById(Long id) {
        return repository.findById(id).orElse(null);
    }
}
