package com.pitang.car_users_backend.service;

import com.pitang.car_users_backend.exception.CarErrorCode;
import com.pitang.car_users_backend.exception.CarException;
import com.pitang.car_users_backend.model.Car;
import com.pitang.car_users_backend.repository.CarRepository;
import com.pitang.car_users_backend.util.CarValidationUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementação de {@link CarService}, contendo a lógica de negócio
 * para criação, atualização, deleção e consulta de carros.
 */
@Service
public class CarServiceImpl implements CarService {

    private final CarRepository repository;

    public CarServiceImpl(CarRepository repository) {
        this.repository = repository;
    }

    @Override
    public Car createCar(Car car) {
        if (CarValidationUtil.hasMissingFields(car)) {
            throw new CarException(CarErrorCode.MISSING_FIELDS);
        }

        if (!CarValidationUtil.isValid(car)) {
            throw new CarException(CarErrorCode.INVALID_FIELDS);
        }

        if (licensePlateExists(car.getLicensePlate())) {
            throw new CarException(CarErrorCode.LICENSE_PLATE_EXISTS);
        }

        return repository.save(car);
    }

    @Override
    public Car updateCar(Long id, Car car) {
        Car existing = repository.findById(id)
                .orElseThrow(() -> new CarException(CarErrorCode.CAR_NOT_FOUND));

        if (CarValidationUtil.hasMissingFields(car)) {
            throw new CarException(CarErrorCode.MISSING_FIELDS);
        }

        if (!CarValidationUtil.isValid(car)) {
            throw new CarException(CarErrorCode.INVALID_FIELDS);
        }

        if (car.getPhotoUrl() != null && !car.getPhotoUrl().isEmpty()) {
            existing.setPhotoUrl(car.getPhotoUrl());
        }

        existing.setLicensePlate(car.getLicensePlate());
        existing.setColor(car.getColor());
        existing.setModel(car.getModel());
        existing.setYear(car.getYear());

        return repository.save(existing);
    }

    @Override
    public Car updateCarPhoto(Long id, String photoUrl) {
        Car car = repository.findById(id)
                .orElseThrow(() -> new CarException(CarErrorCode.CAR_NOT_FOUND));

        car.setPhotoUrl(photoUrl);
        return repository.save(car);
    }

    @Override
    public void deleteCar(Long id) {
        if (!repository.existsById(id)) {
            throw new CarException(CarErrorCode.CAR_NOT_FOUND);
        }
        repository.deleteById(id);
    }

    @Override
    public List<Car> getAllCars() {
        return repository.findAll();
    }

    @Override
    public Car getCarById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CarException(CarErrorCode.CAR_NOT_FOUND));
    }

    @Override
    public Car getCarUserById(Long id) {
        Car car = repository.findById(id)
                .orElseThrow(() -> new CarException(CarErrorCode.CAR_NOT_FOUND));

        car.setUsageCount(car.getUsageCount() + 1);
        this.updateCar(id, car);

        if (car.getUser() != null) {
            car.getUser().recalculateTotalUsage();
        }

        return car;
    }

    @Override
    public List<Car> getCarsByLoggedUser(Long id) {
        return repository.findAllByUser_Id(id);
    }

    @Override
    public boolean licensePlateExists(String licensePlate) {
        return repository.existsByLicensePlate(licensePlate);
    }

}
