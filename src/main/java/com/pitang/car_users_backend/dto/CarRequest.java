package com.pitang.car_users_backend.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO para criação/atualização de carro.
 */
@Setter
@Getter
public class CarRequest {
    private Integer year;
    private String licensePlate;
    private String model;
    private String color;
}