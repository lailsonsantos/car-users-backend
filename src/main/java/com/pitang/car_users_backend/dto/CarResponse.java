package com.pitang.car_users_backend.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO para retorno dos dados do carro.
 */
@Setter
@Getter
public class CarResponse {
    private Long id;
    private Integer year;
    private String licensePlate;
    private String model;
    private String color;
    private int usageCount;
    private String photoUrl;
}