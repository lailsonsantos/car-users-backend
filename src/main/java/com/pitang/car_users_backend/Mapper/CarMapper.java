package com.pitang.car_users_backend.Mapper;

import com.pitang.car_users_backend.dto.CarRequest;
import com.pitang.car_users_backend.dto.CarResponse;
import com.pitang.car_users_backend.model.Car;
import com.pitang.car_users_backend.model.UserEntity;

/**
 * Classe utilitária para conversão entre Car e seus DTOs.
 */
public class CarMapper {

    /**
     * Converte CarRequest para Car.
     * @param request o objeto de requisição
     * @return a entidade de carro
     */
    public static Car toEntity(CarRequest request, UserEntity user) {
        Car car = new Car();
        car.setYear(request.getYear());
        car.setLicensePlate(request.getLicensePlate());
        car.setModel(request.getModel());
        car.setColor(request.getColor());
        car.setUser(user);
        return car;
    }

    /**
     * Converte Car para CarResponse.
     * @param car a entidade de carro
     * @return o objeto de resposta
     */
    public static CarResponse toResponse(Car car) {
        CarResponse response = new CarResponse();
        response.setId(car.getId());
        response.setYear(car.getYear());
        response.setLicensePlate(car.getLicensePlate());
        response.setModel(car.getModel());
        response.setColor(car.getColor());
        response.setUsageCount(car.getUsageCount());
        response.setPhotoUrl(car.getPhotoUrl());
        return response;
    }
}
